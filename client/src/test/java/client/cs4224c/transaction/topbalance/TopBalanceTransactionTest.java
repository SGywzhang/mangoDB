package client.cs4224c.transaction.topbalance;

import client.cs4224c.parser.TopBalanceParser;
import client.cs4224c.transaction.BaseTransactionTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopBalanceTransactionTest extends BaseTransactionTest {

    private final Logger logger = LoggerFactory.getLogger(TopBalanceTransactionTest.class);


    public TopBalanceTransactionTest() {
        super(TopBalanceParser.class);
    }

    @Test
    public void testCase1() throws Exception {
        this.executeFlowWithData("case1.txt");
        logger.info("Begin to validate System output");
        validateSystemOutput("expectedCase1.txt", "");
        logger.info("End: Validate System output");
    }
}
