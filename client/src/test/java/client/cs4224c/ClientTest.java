package client.cs4224c;

import client.cs4224c.parser.AbstractParser;
import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.BaseTransactionTest;
import org.junit.Test;

import java.io.InputStream;

public class ClientTest extends BaseTransactionTest {

    public ClientTest() {
        super(AbstractParser.class);
    }

    @Override
    protected AbstractTransaction executeFlowWithData(String dataFileName) throws Exception {
        throw new UnsupportedOperationException("This test is just to verify ExperimentClient");
    }

    @Test
    public void testMain_typicalCase() throws Exception {
        InputStream backUpSysInput = System.in; // backup
        System.setIn(this.getClass().getResourceAsStream("clientInput.txt"));

        Client.main(new String[] {});

        validateSystemOutput("expectedClientOutput.txt",
                ", O_ENTRY_D.+", " \\(seconds\\):.+", " throughput:.+");

        System.setIn(backUpSysInput); // set back
    }
}
