package client.cs4224c.transaction.stocklevel;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.stocklevel.data.StockLevelTransactionData;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StockLevelTransaction extends AbstractTransaction {

    private final Logger logger = LoggerFactory.getLogger(StockLevelTransaction.class);

    private StockLevelTransactionData data;

    public StockLevelTransactionData getData() {
        return data;
    }

    public void setData(StockLevelTransactionData data) {
        this.data = data;
    }

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
                    .projection(Projections.fields(Projections.include("orderlines.ol_i_id"), Projections.excludeId()))
                    .first();
            if (orderItemDocument == null) {
                logger.warn("OrderItem cannot be find in database [{}, {}, {}]. This might due to interleave execution.", data.getW_ID(), data.getD_ID(), i);
                continue;
            }
            for (Object orderItemOrderLineObj : orderItemDocument.get("orderlines", List.class)) {
                Document orderLineDocument = (Document) orderItemOrderLineObj;
                int i_id = orderLineDocument.getInteger("ol_i_id");

                if (data.getOrderlineItems().contains(i_id)) {
                    // without duplicates
                    // no need to query as it is already there.
                    // in case that someone minus stock in between this, we cannot do anything about it.
                    continue;
                }
                data.getOrderlineItems().add(i_id);

                String stockKey = getCompoundKey(getStr(data.getW_ID()), orderLineDocument.getInteger("ol_i_id").toString());
                Document stockDocument = collectionPool.getCollection(Collection.Stock)
                        .find(Filters.eq("_id", stockKey))
                        .projection(Projections.fields(Projections.include("s_quantity"), Projections.excludeId()))
                        .first();
                int quantity = stockDocument.getInteger("s_quantity");
                if (quantity < data.getT()) {
                    data.getLowStockItems().add(i_id);
                }
            }
        }

        logger.info("Output information now!");

        System.out.println(String.format("%d", data.getLowStockItems().size()));
    }
}
