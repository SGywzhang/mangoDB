package client.cs4224c.transaction.database;

import client.cs4224c.parser.DatabaseStateParser;
import client.cs4224c.transaction.BaseTransactionTest;
import org.junit.Test;

public class DatabaseStateTransactionTest extends BaseTransactionTest {

    public DatabaseStateTransactionTest() {
        super(DatabaseStateParser.class);
    }

    @Test
    public void testDatabaseState() throws Exception {
        this.executeFlowWithData("case1.txt");

        this.validateSystemOutput("expectedOutput.txt", "");
    }
}
