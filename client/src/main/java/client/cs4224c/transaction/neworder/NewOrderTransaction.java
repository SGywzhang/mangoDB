package client.cs4224c.transaction.neworder;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.neworder.data.NewOrderTransactionData;
import client.cs4224c.transaction.neworder.data.NewOrderTransactionOrderLine;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import client.cs4224c.util.TimeUtility;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewOrderTransaction extends AbstractTransaction {

    private final Logger logger = LoggerFactory.getLogger(NewOrderTransaction.class);

    private NewOrderTransactionData data;

    public NewOrderTransactionData getData() {
        return data;
    }

    public void setData(NewOrderTransactionData data) {
        this.data = data;
    }

    @Override
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();

        logger.info("Get and update d_next_o_id");
        Document districtDocument = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()))))
                .projection(Projections.fields(Projections.include("d_next_o_id", "d_tax", "warehouse.w_tax"), Projections.excludeId()))
                .first();
        collectionPool.getCollection(Collection.District)
                .updateOne(Filters.eq("_id", getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()))),
                        new Document("$inc", new Document("d_next_o_id", 1)));
        int next_o_id = districtDocument.getInteger("d_next_o_id");

        data.setD_TAX(districtDocument.getDouble("d_tax"));
        data.setW_TAX(districtDocument.get("warehouse", Document.class).getDouble("w_tax"));
        logger.info("Get the D_NEXT_O_ID and update it already {}", next_o_id);

        logger.info("Update customer last order");
        collectionPool.getCollection(Collection.Customer)
                .updateOne(Filters.eq("_id", getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()), getStr(data.getC_ID()))),
                        new Document("$set", new Document("c_last_order", next_o_id)));

        logger.info("Create new order");
        data.setO_ENTRY_D(new Date());
        Document orderItem = new Document();
        orderItem.put("_id", getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()), getStr(next_o_id)));
        orderItem.put("o_c_id", data.getC_ID());
        orderItem.put("o_ol_cnt", data.getOrderLines().size());
        orderItem.put("o_all_local", data.isAllLocal());
        orderItem.put("o_entry_d", data.getO_ENTRY_D());

        double total_amount = 0;
        List<Document> orderItemOrderLines = new ArrayList<>();
        for (int i = 0; i < data.getOrderLines().size(); i++) {
            logger.info("Create order-line[{}]", i);
            Document orderLineDocument = new Document();
            NewOrderTransactionOrderLine orderLineRecord = data.getOrderLines().get(i);
            String stockKey = getCompoundKey(getStr(orderLineRecord.getOL_SUPPLY_W_ID()), getStr(orderLineRecord.getOL_I_ID()));
            String sDistKey = String.format("s_dist_%02d", data.getD_ID());

            Document stockDocument = collectionPool.getCollection(Collection.Stock)
                    .find(Filters.eq("_id", stockKey))
                    .projection(Projections.fields(Projections.include("s_quantity", "item.i_price", "item.i_name", sDistKey), Projections.excludeId()))
                    .first();
            int updatedQuantity = stockDocument.getInteger("s_quantity") - orderLineRecord.getOL_QUANTITY();
            int incrementQuantity = -orderLineRecord.getOL_QUANTITY();
            int incrementRemoteCnt = 0;
            int incrementOrderCnt = 1;
            int incrementStockYtd = orderLineRecord.getOL_QUANTITY();
            if (updatedQuantity < 10) {
                incrementQuantity += 100;
            }
            if (orderLineRecord.getOL_SUPPLY_W_ID() != data.getW_ID()) {
                incrementRemoteCnt++;
            }
            orderLineRecord.setS_QUANTITY(stockDocument.getInteger("s_quantity") + incrementQuantity);
            logger.info("Update stock");
            Document updateIncrementDocument = new Document();
            updateIncrementDocument.put("s_quantity", incrementQuantity);
            updateIncrementDocument.put("s_ytd", incrementStockYtd);
            updateIncrementDocument.put("s_order_cnt", incrementOrderCnt);
            updateIncrementDocument.put("s_remote_cnt", incrementRemoteCnt);
            collectionPool.getCollection(Collection.Stock)
                    .updateOne(Filters.eq("_id", stockKey), new Document("$inc", updateIncrementDocument));

            double itemPrice = stockDocument.get("item", Document.class).getDouble("i_price");
            String distInfo = stockDocument.getString(sDistKey);
            orderLineRecord.setI_NAME(stockDocument.get("item", Document.class).getString("i_name"));

            double itemAmount = orderLineRecord.getOL_QUANTITY() * itemPrice;
            orderLineRecord.setOL_AMOUNT(itemAmount);
            total_amount += itemAmount;

            orderLineDocument.put("ol_number", i + 1);
            orderLineDocument.put("ol_amount", itemAmount);
            orderLineDocument.put("ol_supply_w_id", orderLineRecord.getOL_SUPPLY_W_ID());
            orderLineDocument.put("ol_quantity", orderLineRecord.getOL_QUANTITY());
            orderLineDocument.put("ol_dist_info", distInfo);
            orderLineDocument.put("ol_i_id", orderLineRecord.getOL_I_ID());
            orderLineDocument.put("i_name", orderLineRecord.getI_NAME());

            orderItemOrderLines.add(orderLineDocument);
        }
        orderItem.put("orderlines", orderItemOrderLines);

        Document customerDocument = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", getCompoundKey(getStr(data.getW_ID()), getStr(data.getD_ID()), getStr(data.getC_ID()))))
                .projection(Projections.fields(Projections.include("c_discount", "c_first", "c_middle", "c_last", "c_credit"), Projections.excludeId()))
                .first();

        Document customerPartial = new Document();
        customerPartial.put("c_first", customerDocument.get("c_first"));
        customerPartial.put("c_middle", customerDocument.get("c_middle"));
        customerPartial.put("c_last", customerDocument.get("c_last"));
        orderItem.put("customer", customerPartial);

        collectionPool.getCollection(Collection.OrderItem).insertOne(orderItem);

        data.setC_DISCOUNT(customerDocument.getDouble("c_discount"));
        data.setC_CREDIT(customerDocument.getString("c_credit"));
        data.setC_LAST(customerDocument.getString("c_last"));

        total_amount *= (1 + data.getD_TAX() + data.getW_TAX()) * (1 - data.getC_DISCOUNT());

        logger.info("Output information now!");
        System.out.println(String.format("1. (W_ID: %d, D_ID: %d, C_ID, %d), C_LAST: %s, C_CREDIT: %s, C_DISCOUNT: %.4f", data.getW_ID(), data.getD_ID(), data.getC_ID(),
                data.getC_LAST(), data.getC_CREDIT(), data.getC_DISCOUNT()));
        System.out.println(String.format("2. W_TAX: %.4f, D_TAX: %.4f", data.getW_TAX(), data.getD_TAX()));
        System.out.println(String.format("3. O_ID: %d, O_ENTRY_D: %s", next_o_id, TimeUtility.format(data.getO_ENTRY_D())));
        System.out.println(String.format("4. NUM_ITEMS: %s, TOTAL_AMOUNT: %.2f", data.getOrderLines().size(), total_amount));
        System.out.println("5. DETAILS OF ITEMS");
        for (NewOrderTransactionOrderLine orderLine : data.getOrderLines()) {
            System.out.println(String.format("\t ITEM_NUMBER: %s, I_NAME: %s, SUPPLIER_WAREHOUSE: %d, QUANTITY: %d, OL_AMOUNT: %.2f, S_QUANTITY: %d",
                    orderLine.getOL_I_ID(), orderLine.getI_NAME(), orderLine.getOL_SUPPLY_W_ID(), orderLine.getOL_QUANTITY(), orderLine.getOL_AMOUNT(), orderLine.getS_QUANTITY()));
        }
    }
}
