package client.cs4224c.transaction.popularitem;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.popularitem.data.OrderData;
import client.cs4224c.transaction.popularitem.data.OrderLineItemData;
import client.cs4224c.transaction.popularitem.data.PopularItemTransactionData;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import client.cs4224c.util.TimeUtility;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopularItemTransaction extends AbstractTransaction {

    private final Logger logger = LoggerFactory.getLogger(PopularItemTransaction.class);

    private PopularItemTransactionData data;

    public PopularItemTransactionData getData() {
        return data;
    }

    public void setData(PopularItemTransactionData data) {
        this.data = data;
    }

    private static int INDEX_FIRST = 0;

    @Override
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();

        Document districtDocument = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()))))
                .projection(Projections.fields(Projections.include("d_next_o_id"), Projections.excludeId()))
                .first();
        int next_o_id = districtDocument.getInteger("d_next_o_id");

        for (int i = next_o_id - data.getL(); i < next_o_id; i++) {

            // validation of L
            if (i <= 0) {
                logger.warn("No more orders for warehouse {} district {} already.", data.getW_ID(), data.getD_ID());
                continue;
            }

            String orderItemKey = getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()), getStr(i));
            Document orderItemDocument = collectionPool.getCollection(Collection.OrderItem)
                    .find(Filters.eq("_id", orderItemKey))
                    .projection(Projections.fields(
                            Projections.include("o_entry_d", "customer.c_first", "customer.c_middle", "customer.c_last", "orderlines.ol_i_id", "orderlines.ol_quantity", "orderlines.i_name"),
                            Projections.excludeId()
                    ))
                    .first();
            if (orderItemDocument == null) {
                logger.warn("NEXT_O_ID is not consecutive as OrderItem cannot be find in database [{}, {}, {}].", data.getW_ID(), data.getD_ID(), i);
                continue;
            }

            OrderData orderData = new OrderData();
            orderData.setO_ID(i);
            orderData.setO_ENTRY_DATE(orderItemDocument.getDate("o_entry_d"));
            orderData.setC_FIRST(orderItemDocument.get("customer", Document.class).getString("c_first"));
            orderData.setC_MIDDLE(orderItemDocument.get("customer", Document.class).getString("c_middle"));
            orderData.setC_LAST(orderItemDocument.get("customer", Document.class).getString("c_last"));


            for (Object orderItemOrderLineObj : orderItemDocument.get("orderlines", List.class)) {
                Document orderLineDocument = (Document) orderItemOrderLineObj;

                OrderLineItemData orderLineItemData = new OrderLineItemData();
                orderLineItemData.setOL_I_ID(orderLineDocument.getInteger("ol_i_id"));
                orderLineItemData.setOL_QUANTITY(orderLineDocument.getInteger("ol_quantity"));
                orderLineItemData.setI_NAME(orderLineDocument.getString("i_name"));

                // Add to HashMap to keep track of orders containing order line item
                if (!data.getItemsMap().containsKey(orderLineItemData.getOL_I_ID())) {
                    data.getItemsMap().put(orderLineItemData.getOL_I_ID(), new ArrayList<>());
                }
                if (!data.getItemsMap().get(orderLineItemData.getOL_I_ID()).contains(orderData.getO_ID())) {
                    data.getItemsMap().get(orderLineItemData.getOL_I_ID()).add(orderData.getO_ID());
                }

                // Find popular Item for each order
                if (orderData.getPopularItems().isEmpty() ||
                        orderLineItemData.getOL_QUANTITY() >= orderData.getPopularItems().get(INDEX_FIRST).getOL_QUANTITY()) {
                    if (!orderData.getPopularItems().isEmpty() &&
                            orderLineItemData.getOL_QUANTITY() > orderData.getPopularItems().get(INDEX_FIRST).getOL_QUANTITY()) {
                        orderData.getPopularItems().clear();
                    }
                    orderData.getPopularItems().add(orderLineItemData);
                }
            }

            data.getLastLOrders().add(orderData);
        }

        logger.info("Output information now!");

        System.out.println(String.format("1. W_ID: %d, D_ID: %d", data.getW_ID(), data.getD_ID()));
        System.out.println(String.format("2. Number of last orders to be examined: %d", data.getL()));

        System.out.println("3.");

        Set<Integer> distinctItemIds = new HashSet<Integer>();

        for (OrderData orderData: data.getLastLOrders()) {
            System.out.println(String.format("\t(a) O_ID: %d, O_ENTRY_D: %s", orderData.getO_ID(), TimeUtility.format(orderData.getO_ENTRY_DATE())));
            System.out.println(String.format("\t(b) C_FIRST: %s, C_MIDDLE: %s, C_LAST: %s", orderData.getC_FIRST(), orderData.getC_MIDDLE(), orderData.getC_LAST()));

            System.out.println("\t(c)");
            for(OrderLineItemData popularItem: orderData.getPopularItems()) {
                System.out.println(String.format("\t\t(i) Item: %s", popularItem.getI_NAME()));
                System.out.println(String.format("\t\t(ii) Quantity Ordered: %d", popularItem.getOL_QUANTITY()));
                System.out.println();

                // for distinct item
                if (distinctItemIds.contains(popularItem.getOL_I_ID())) {
                    continue;
                }
                distinctItemIds.add(popularItem.getOL_I_ID());
                data.getPopularItems().add(popularItem);
            }
            System.out.println();
        }

        System.out.println("4.");
        for (OrderLineItemData popularItem: data.getPopularItems()) {
            System.out.println(String.format("\t(i) Item: %s", popularItem.getI_NAME()));
            System.out.println(String.format("\t(ii) Percentage of orders that contain the popular item: %.2f%%", (data.getItemsMap().get(popularItem.getOL_I_ID()).size() / (data.getL() * 1.0f)) * 100));
            System.out.println();
        }
    }
}
