package client.cs4224c.transaction;

import client.cs4224c.parser.AbstractParser;
import client.cs4224c.util.Constant;
import client.cs4224c.util.ProjectConfig;
import org.apache.commons.io.IOUtils;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class BaseTransactionTest {

    private final Logger logger = LoggerFactory.getLogger(BaseTransactionTest.class);

    protected PrintStream systemOutput;

    protected ByteArrayOutputStream baos;

    private Class<? extends AbstractParser> abstractParserClass;

    protected BaseTransactionTest(Class<? extends AbstractParser> abstractParserClass) {
        this.abstractParserClass = abstractParserClass;
    }

    @Before
    public void before() {
        System.setProperty(Constant.PROPERTY_KEY_ENV, Constant.ENV_TEST); // set test env
        reImportAllTestData();
        interceptSystemOutput();
    }

    private void interceptSystemOutput() {
        systemOutput = System.out;
        // Create a stream to hold the output
        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    @After
    public void after() {
        logger.info("Output from System:\n{}", baos.toString());
        System.setOut(systemOutput); //
    }

    private void reImportAllTestData() {
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File(ProjectConfig.getInstance().getProjectRoot()))
                .connect();
        try {
            connection.newBuild().forTasks("import::test").withArguments("--rerun-tasks").run(); // inject all test data
        } finally {
            connection.close();
        }
    }

    protected AbstractTransaction executeFlowWithData(String dataFileName) throws Exception {
        Scanner sc = new Scanner(this.getClass().getResourceAsStream(dataFileName));
        String[] commandArgs = sc.nextLine().split(Constant.COMMA_SEPARATOR);
        AbstractTransaction transaction = abstractParserClass.newInstance().parse(sc, commandArgs);

        transaction.executeFlow();

        return transaction;
    }

    protected void validateSystemOutput(String expectedContentFileName, String... regexToIgnore) throws IOException {
        String systemOut = baos.toString();
        String expectedOut = IOUtils.toString(this.getClass().getResourceAsStream(expectedContentFileName));

        for (String regex : regexToIgnore) {
            systemOut = systemOut.replaceAll(regex, "");
            expectedOut = expectedOut.replaceAll(regex, "");
        }

        // compare without any line-breaker
        Assert.assertEquals(
                expectedOut.replace("\r", ""),
                systemOut.replace("\r", "")
        );
    }
}
