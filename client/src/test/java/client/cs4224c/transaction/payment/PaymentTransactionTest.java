package client.cs4224c.transaction.payment;

import client.cs4224c.parser.PaymentParser;
import client.cs4224c.transaction.BaseTransactionTest;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentTransactionTest extends BaseTransactionTest {

    private final Logger logger = LoggerFactory.getLogger(PaymentTransactionTest.class);


    public PaymentTransactionTest() {
        super(PaymentParser.class);
    }

    @Test
    public void testCase1() throws Exception {
        this.executeFlowWithData("case1.txt");

        logger.info("Begin to validate database for PaymentTransaction");
        CollectionPool collectionPool = CollectionPool.getInstance();

        // customer
        Document customerDocument = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", "1-1-105"))
                .first();
        Assert.assertEquals(Double.valueOf(-1944.29), customerDocument.getDouble("c_balance"));
        Assert.assertEquals(Double.valueOf(1944.29), customerDocument.getDouble("c_ytd_payment"));
        Assert.assertEquals(Integer.valueOf(2), customerDocument.getInteger("c_payment_cnt"));

        // district
        Document districtDocument = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", "1-1"))
                .first();
        Assert.assertEquals(Double.valueOf(31934.29), districtDocument.getDouble("d_ytd"));

        logger.info("End: Validate database for PaymentTransaction");

        logger.info("Begin to validate System output");
        validateSystemOutput("expectedCase1.txt", "");
        logger.info("End: Validate System output");
    }
}
