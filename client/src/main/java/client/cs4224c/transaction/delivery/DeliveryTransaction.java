package client.cs4224c.transaction.delivery;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.delivery.data.DeliveryTransactionData;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.BulkWriteError;
import com.mongodb.BulkWriteException;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryTransaction extends AbstractTransaction{

    private final Logger logger = LoggerFactory.getLogger(DeliveryTransaction.class);

    private DeliveryTransactionData data;

    public DeliveryTransactionData getData() {
        return data;
    }

    public void setData(DeliveryTransactionData data) {
        this.data = data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();

        List<WriteModel<Document>> bulkWritesOrderItem = new ArrayList<WriteModel<Document>>();
        List<WriteModel<Document>> bulkWritesCustomer = new ArrayList<WriteModel<Document>>();
        for (short O_D_ID = 1; O_D_ID <= 10; O_D_ID++) {
            int minUndeliveredOrderId = 1; // if there is no row in DB, we assume the order number start from 1
            String districtKey = getCompoundKey(getStr(data.getW_ID()), getStr(O_D_ID));

            Document districtDocument = collectionPool.getCollection(Collection.District)
                    .find(Filters.eq("_id", districtKey))
                    .projection(Projections.fields(Projections.include("dt_min_ud_o_id"), Projections.excludeId()))
                    .first();
            if (districtDocument.size() != 0) {
                minUndeliveredOrderId = districtDocument.getInteger("dt_min_ud_o_id");
            }

            String orderItemKey = getCompoundKey(getStr(data.getW_ID()), getStr(O_D_ID), getStr(minUndeliveredOrderId));
            Document orderItemDocument = collectionPool.getCollection(Collection.OrderItem)
                    .find(Filters.eq("_id", orderItemKey))
                    .projection(Projections.fields(Projections.include("o_c_id", "o_ol_cnt", "orderlines.ol_amount"), Projections.excludeId()))
                    .first();
            if (orderItemDocument == null) {
                logger.warn("No unDeliveredOrder for W_ID: {}, D_ID: {}, it doesn't make sense to make delivery transaction", data.getW_ID(), O_D_ID);
                continue;
            }
            // update next undelivered order
            collectionPool.getCollection(Collection.District)
                    .updateOne(Filters.eq("_id", districtKey), new Document("$inc", new Document("dt_min_ud_o_id", 1)));

            // bulk write begin

            double orderLineAmount = 0;
            Document setDateDocument = new Document();
            Date now = new Date();
            List<Document> orderItemOrderLines = (List<Document>) orderItemDocument.get("orderlines", List.class);
            for (int i = 0; i < orderItemDocument.getInteger("o_ol_cnt"); i++) {
                orderLineAmount += orderItemOrderLines.get(i).getDouble("ol_amount");
                setDateDocument.put(String.format("orderlines.%d.ol_delivery_d", i), now);
            }
            setDateDocument.put("o_carrier_id", data.getCARRIER_ID());
            bulkWritesOrderItem.add(new UpdateOneModel<Document>(Filters.eq("_id", orderItemKey), new Document("$set", setDateDocument)));

            Document incrementCustomer = new Document();
            incrementCustomer.put("c_balance", orderLineAmount);
            incrementCustomer.put("c_delivery_cnt", 1);
            bulkWritesCustomer.add(new UpdateOneModel<Document>(
                    Filters.eq("_id", getCompoundKey(getStr(data.getW_ID()), getStr(O_D_ID), getStr(orderItemDocument.getInteger("o_c_id")))),
                    new Document("$inc", incrementCustomer)));
        }

        // bulk write for less I/O
        StringBuilder errStr = new StringBuilder();
        try {
            if (bulkWritesOrderItem.size() > 0) {
                collectionPool.getCollection(Collection.OrderItem).bulkWrite(bulkWritesOrderItem, new BulkWriteOptions().ordered(false));
            }
        } catch (BulkWriteException e) {
            if (e.getWriteConcernError() != null) {
                errStr.append(e.getWriteConcernError().getMessage());
            } else {
                for (BulkWriteError writeError : e.getWriteErrors()) {
                    errStr.append(writeError.getMessage());
                }
            }
        }

        try {
            if (bulkWritesCustomer.size() > 0) {
                collectionPool.getCollection(Collection.Customer).bulkWrite(bulkWritesCustomer, new BulkWriteOptions().ordered(false));
            }
        } catch (BulkWriteException e) {
            if (e.getWriteConcernError() != null) {
                errStr.append(e.getWriteConcernError().getMessage());
            } else {
                for (BulkWriteError writeError : e.getWriteErrors()) {
                    errStr.append(writeError.getMessage());
                }
            }
        }

        if (errStr.length() > 0) {
            throw new RuntimeException(errStr.toString());
        }
    }
}
