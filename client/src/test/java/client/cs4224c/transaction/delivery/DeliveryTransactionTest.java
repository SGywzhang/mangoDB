package client.cs4224c.transaction.delivery;

import client.cs4224c.parser.DeliveryParser;
import client.cs4224c.transaction.BaseTransactionTest;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeliveryTransactionTest extends BaseTransactionTest{

    private final Logger logger = LoggerFactory.getLogger(DeliveryTransactionTest.class);

    public DeliveryTransactionTest() {super(DeliveryParser.class);}

    @Test
    public void test1() throws Exception {
        this.executeFlowWithData("case1.txt");

        logger.info("Begin to validate database for DeliveryTransaction");
        CollectionPool collectionPool = CollectionPool.getInstance();

        // validate updatedMinOrderIdWithNullCarrierId
        Document districtDocument1 = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", "7-2"))
                .first();
        Assert.assertEquals(Integer.valueOf(2595), districtDocument1.getInteger("dt_min_ud_o_id"));

        Document districtDocument2 = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", "7-7"))
                .first();
        Assert.assertEquals(Integer.valueOf(2544), districtDocument2.getInteger("dt_min_ud_o_id"));

        Document districtDocument3 = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", "7-9"))
                .first();
        Assert.assertEquals(Integer.valueOf(2442), districtDocument3.getInteger("dt_min_ud_o_id"));

        // non-undelivered order district will not be accidentally updated
        Document districtDocument4 = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", "7-1"))
                .first();
        Assert.assertNull(districtDocument4.getInteger("dt_min_ud_o_id"));

        // validate order carrier_id / orderLines ol_delivery_d
        Document orderItemDocument1 = collectionPool.getCollection(Collection.OrderItem)
                .find(Filters.eq("_id", "7-2-2594"))
                .first();
        Assert.assertEquals(Integer.valueOf(3), orderItemDocument1.getInteger("o_carrier_id"));
        for (Document orderItemOrderLine : (List<Document>) orderItemDocument1.get("orderlines", List.class)) {
            Assert.assertNotNull(orderItemOrderLine.get("ol_delivery_d"));
        }

        Document orderItemDocument2 = collectionPool.getCollection(Collection.OrderItem)
                .find(Filters.eq("_id", "7-7-2543"))
                .first();
        Assert.assertEquals(Integer.valueOf(3), orderItemDocument2.getInteger("o_carrier_id"));
        for (Document orderItemOrderLine : (List<Document>) orderItemDocument2.get("orderlines", List.class)) {
            Assert.assertNotNull(orderItemOrderLine.get("ol_delivery_d"));
        }

        Document orderItemDocument3 = collectionPool.getCollection(Collection.OrderItem)
                .find(Filters.eq("_id", "7-9-2441"))
                .first();
        Assert.assertEquals(Integer.valueOf(3), orderItemDocument3.getInteger("o_carrier_id"));
        for (Document orderItemOrderLine : (List<Document>) orderItemDocument3.get("orderlines", List.class)) {
            Assert.assertNotNull(orderItemOrderLine.get("ol_delivery_d"));
        }


        // validate customer
        Document customerDocument1 = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", "7-2-2816"))
                .first();
        Assert.assertEquals(Double.valueOf(1011.35), customerDocument1.getDouble("c_balance"));
        Assert.assertEquals(Integer.valueOf(1), customerDocument1.getInteger("c_delivery_cnt"));

        Document customerDocument2 = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", "7-7-2410"))
                .first();
        Assert.assertEquals(Double.valueOf(5409.29), customerDocument2.getDouble("c_balance"));
        Assert.assertEquals(Integer.valueOf(1), customerDocument2.getInteger("c_delivery_cnt"));

        Document customerDocument3 = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", "7-9-2043"))
                .first();
        Assert.assertEquals(Double.valueOf(1230.71), customerDocument3.getDouble("c_balance"));
        Assert.assertEquals(Integer.valueOf(1), customerDocument3.getInteger("c_delivery_cnt"));
    }
}