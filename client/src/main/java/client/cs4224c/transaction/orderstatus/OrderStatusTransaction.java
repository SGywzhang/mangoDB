package client.cs4224c.transaction.orderstatus;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.orderstatus.data.OrderStatusTransactionData;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import client.cs4224c.util.TimeUtility;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class OrderStatusTransaction extends AbstractTransaction {
    private final Logger logger = LoggerFactory.getLogger(OrderStatusTransaction.class);

    private OrderStatusTransactionData data;

    public OrderStatusTransactionData getData() {
        return data;
    }

    public void setData(OrderStatusTransactionData data) {
        this.data = data;
    }

    @Override
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();
        String customerKey = getCompoundKey(getStr(data.getC_W_ID()), getStr(data.getC_D_ID()), getStr(data.getC_ID()));

        Document customerDocument = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", customerKey))
                .projection(Projections.fields(Projections.include("c_first", "c_middle", "c_last", "c_last_order", "c_balance"), Projections.excludeId()))
                .first();

        if (customerDocument.getInteger("c_last_order") == null) {
            logger.warn(String.format("The customer [%d, %d, %d] doesn't have any order.", data.getC_W_ID(), data.getC_D_ID(), data.getC_ID()));
            return;
        }

        System.out.println(String.format("1. Customer(C_FIRST: %s, C_MIDDLE: %s, C_LAST: %s), C_BALANCE: %.2f",
                customerDocument.getString("c_first"),
                customerDocument.getString("c_middle"),
                customerDocument.getString("c_last"),
                customerDocument.getDouble("c_balance"))); // DECIMAL(12,2)

        String orderItemKey = getCompoundKey(getStr(data.getC_W_ID()), getStr(data.getC_D_ID()), customerDocument.getInteger("c_last_order").toString());
        Document orderItemDocument = collectionPool.getCollection(Collection.OrderItem)
                .find(Filters.eq("_id", orderItemKey))
                .projection(Projections.fields(
                        Projections.include("o_entry_d", "o_carrier_id", "orderlines.ol_i_id", "orderlines.ol_supply_w_id", "orderlines.ol_quantity", "orderlines.ol_amount", "orderlines.ol_delivery_d"),
                        Projections.excludeId()
                ))
                .first();

        System.out.println(String.format("2. Last order : O_ID: %d, O_ENTRY_D: %s, O_CARRIER_ID: %s",
                customerDocument.getInteger("c_last_order"),
                TimeUtility.format(orderItemDocument.getDate("o_entry_d")),
                orderItemDocument.getInteger("o_carrier_id") == null ? "NO_CARRIER" : orderItemDocument.getInteger("o_carrier_id")));

        System.out.println("3. Item in last order:");
        for (Object orderItemOrderLineObj : orderItemDocument.get("orderlines", List.class)) {
            Document orderLineDocument = (Document) orderItemOrderLineObj;
            Date OL_DELIVERY_D = orderLineDocument.getDate("ol_delivery_d");

            System.out.println(String.format("\tOL_I_ID: %s, OL_SUPPLY_W_ID: %d, OL_QUANTITY: %d, OL_AMOUNT: %s, OL_DELIVERY_D: %s",
                    orderLineDocument.getInteger("ol_i_id"),
                    orderLineDocument.getInteger("ol_supply_w_id"),
                    orderLineDocument.getInteger("ol_quantity"),
                    orderLineDocument.getDouble("ol_amount"),
                    OL_DELIVERY_D == null ? "NOT_DELIVERED" : TimeUtility.format(OL_DELIVERY_D)));
        }
    }
}
