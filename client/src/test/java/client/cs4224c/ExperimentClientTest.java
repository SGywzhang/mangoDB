package client.cs4224c;

import client.cs4224c.parser.AbstractParser;
import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.BaseTransactionTest;
import org.junit.Test;

public class ExperimentClientTest extends BaseTransactionTest {

    public ExperimentClientTest() {
        super(AbstractParser.class);
    }

    @Override
    protected AbstractTransaction executeFlowWithData(String dataFileName) throws Exception {
        throw new UnsupportedOperationException("This test is just to verify ExperimentClient");
    }

    @Test
    public void testMain_typicalCase() throws Exception {
        ExperimentClient.main(new String[] {"1.txt", "127.0.0.1"});

        validateSystemOutput("expectedExperimentClientOutput.txt",
                ", O_ENTRY_D.+", " \\(seconds\\):.+", " throughput:.+");
    }
}
