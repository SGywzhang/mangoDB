package client.cs4224c.transaction.stocklevel;

import client.cs4224c.parser.StockLevelParser;
import client.cs4224c.transaction.BaseTransactionTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockLevelTransactionTest extends BaseTransactionTest {
    private final Logger logger = LoggerFactory.getLogger(StockLevelTransactionTest.class);

    public StockLevelTransactionTest() {
        super(StockLevelParser.class);
    }

    private static short W_ID = 1;
    private static short D_ID = 1;

    @Test
    public void testCase1() throws Exception {

        this.executeFlowWithData("case1.txt");

        logger.info("Begin to validate System output");

        this.validateSystemOutput("expectedCase1.txt", "");

        logger.info("End: Validate System output");
    }
}
