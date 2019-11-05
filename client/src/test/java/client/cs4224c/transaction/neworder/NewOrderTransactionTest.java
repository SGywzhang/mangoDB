package client.cs4224c.transaction.neworder;

import client.cs4224c.parser.NewOrderParser;
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

public class NewOrderTransactionTest extends BaseTransactionTest {

    private final Logger logger = LoggerFactory.getLogger(NewOrderTransactionTest.class);

    public NewOrderTransactionTest() {
        super(NewOrderParser.class);
    }

    @Test
    public void testCase1() throws Exception {
        this.executeFlowWithData("case1.txt");

        logger.info("Begin to validate database for NewOrderTransaction");
        CollectionPool collectionPool = CollectionPool.getInstance();

        // validate d_next_o_id
        Document districtDocument = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", "7-7"))
                .first();
        Assert.assertEquals(Integer.valueOf(3002), districtDocument.getInteger("d_next_o_id"));

        // validate order
        Document orderItemDocument = collectionPool.getCollection(Collection.OrderItem)
                .find(Filters.eq("_id", "7-7-3001"))
                .first();
        Assert.assertEquals(Integer.valueOf(347), orderItemDocument.getInteger("o_c_id"));
        Assert.assertNull(orderItemDocument.getInteger("o_carrier_id"));
        Assert.assertEquals(Integer.valueOf(3), orderItemDocument.getInteger("o_ol_cnt"));
        Assert.assertFalse(orderItemDocument.getBoolean("o_all_local"));

        // validate order-lines
        List<Document> orderItemOLDocument = orderItemDocument.get("orderlines", List.class);
        Assert.assertEquals(Integer.valueOf(1), orderItemOLDocument.get(0).getInteger("ol_number"));
        Assert.assertEquals(Integer.valueOf(14), orderItemOLDocument.get(0).getInteger("ol_i_id"));
        Assert.assertEquals(Integer.valueOf(10), orderItemOLDocument.get(0).getInteger("ol_supply_w_id"));
        Assert.assertEquals(Integer.valueOf(68), orderItemOLDocument.get(0).getInteger("ol_quantity"));
        Assert.assertNull(orderItemOLDocument.get(0).getInteger("ol_delivery_d"));
        Assert.assertEquals("dvsxaadjazcomwlmaghaxzd", orderItemOLDocument.get(0).getString("ol_dist_info"));

        Assert.assertEquals(Integer.valueOf(2), orderItemOLDocument.get(1).getInteger("ol_number"));
        Assert.assertEquals(Integer.valueOf(283), orderItemOLDocument.get(1).getInteger("ol_i_id"));
        Assert.assertEquals(Integer.valueOf(7), orderItemOLDocument.get(1).getInteger("ol_supply_w_id"));
        Assert.assertEquals(Integer.valueOf(40), orderItemOLDocument.get(1).getInteger("ol_quantity"));
        Assert.assertNull(orderItemOLDocument.get(1).getInteger("ol_delivery_d"));
        Assert.assertEquals("jkbhqpbduokgvstorxleumy", orderItemOLDocument.get(1).getString("ol_dist_info"));

        Assert.assertEquals(Integer.valueOf(3), orderItemOLDocument.get(2).getInteger("ol_number"));
        Assert.assertEquals(Integer.valueOf(312), orderItemOLDocument.get(2).getInteger("ol_i_id"));
        Assert.assertEquals(Integer.valueOf(12), orderItemOLDocument.get(2).getInteger("ol_supply_w_id"));
        Assert.assertEquals(Integer.valueOf(10), orderItemOLDocument.get(2).getInteger("ol_quantity"));
        Assert.assertNull(orderItemOLDocument.get(2).getInteger("ol_delivery_d"));
        Assert.assertEquals("oakqwytwbrxgmkaipaxxeyk", orderItemOLDocument.get(2).getString("ol_dist_info"));

        // validate customer last order
        Document customerDocument = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", "7-7-347"))
                .first();
        Assert.assertEquals(Integer.valueOf(3001), customerDocument.getInteger("c_last_order"));

        // validate stock
        Document stockDocument1 = collectionPool.getCollection(Collection.Stock)
                .find(Filters.eq("_id", "10-14"))
                .first();
        Assert.assertEquals(Integer.valueOf(10), stockDocument1.getInteger("s_quantity"));
        Assert.assertEquals(Double.valueOf(68), stockDocument1.getDouble("s_ytd"));
        Assert.assertEquals(Integer.valueOf(1), stockDocument1.getInteger("s_order_cnt"));
        Assert.assertEquals(Integer.valueOf(1), stockDocument1.getInteger("s_remote_cnt"));

        Document stockDocument2 = collectionPool.getCollection(Collection.Stock)
                .find(Filters.eq("_id", "7-283"))
                .first();
        Assert.assertEquals(Integer.valueOf(107), stockDocument2.getInteger("s_quantity"));
        Assert.assertEquals(Double.valueOf(40), stockDocument2.getDouble("s_ytd"));
        Assert.assertEquals(Integer.valueOf(1), stockDocument2.getInteger("s_order_cnt"));
        Assert.assertEquals(Integer.valueOf(0), stockDocument2.getInteger("s_remote_cnt"));

        Document stockDocument3 = collectionPool.getCollection(Collection.Stock)
                .find(Filters.eq("_id", "12-312"))
                .first();
        Assert.assertEquals(Integer.valueOf(69), stockDocument3.getInteger("s_quantity"));
        Assert.assertEquals(Double.valueOf(10), stockDocument3.getDouble("s_ytd"));
        Assert.assertEquals(Integer.valueOf(1), stockDocument3.getInteger("s_order_cnt"));
        Assert.assertEquals(Integer.valueOf(1), stockDocument3.getInteger("s_remote_cnt"));

        logger.info("End: Validate database for NewOrderTransaction");

        logger.info("Begin to validate System output");

        this.validateSystemOutput("expectedCase1.txt", ", O_ENTRY_D.+");

        logger.info("End: Validate System output");
    }
}
