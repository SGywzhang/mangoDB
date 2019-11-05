package client.cs4224c.transaction.topbalance;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopBalanceTransaction extends AbstractTransaction {

    private final Logger logger = LoggerFactory.getLogger(TopBalanceTransaction.class);

    @Override
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();

        FindIterable<Document> topCustomers = collectionPool.getCollection(Collection.Customer)
                .find()
                .projection(Projections.fields(Projections.include("c_balance", "c_w_name", "c_d_name", "c_first", "c_middle", "c_last"), Projections.excludeId()))
                .sort(new Document("c_balance", -1))
                .limit(10);

        logger.info("Output information");

        int count = 1;
        for (Document customerDocument : topCustomers) {
            System.out.println(String. format("%d. Name: (%s, %s, %s), Balance: %.4f, Warehouse Name: %s, District Name: %s",
                    count, customerDocument.getString("c_first"), customerDocument.getString("c_middle"), customerDocument.getString("c_last"),
                    customerDocument.getDouble("c_balance"), customerDocument.getString("c_w_name"), customerDocument.getString("c_d_name")));
            count++;
        }
    }
}
