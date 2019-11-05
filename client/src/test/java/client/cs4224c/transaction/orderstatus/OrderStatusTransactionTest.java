package client.cs4224c.transaction.orderstatus;

import client.cs4224c.parser.OrderStatusParser;
import client.cs4224c.transaction.BaseTransactionTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderStatusTransactionTest extends BaseTransactionTest{
    private final Logger logger = LoggerFactory.getLogger(OrderStatusTransactionTest.class);

    public OrderStatusTransactionTest() {
        super(OrderStatusParser.class);
    }

    @Test
    public void testCase1() throws Exception {
        this.executeFlowWithData("case1.txt");

        this.validateSystemOutput("expectedCase1.txt", "");
    }
}