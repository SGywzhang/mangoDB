package client.cs4224c;

import client.cs4224c.parser.AbstractParser;
import client.cs4224c.parser.ParserMap;
import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.util.Constant;
import client.cs4224c.util.ProjectConfig;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ExperimentClient {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentClient.class);

    private static int INDEX_COMMAND = 0;

    public static double median(Long[] numbers) {
        Arrays.sort(numbers);
        int middle = numbers.length / 2;
        if (numbers.length % 2 == 1) {
            return numbers[middle];
        } else {
            return (numbers[middle - 1] + numbers[middle]) / 2.0;
        }
    }

    public static double average(Long[] numbers) {
        int sum = 0;
        for (Long i : numbers)
            sum += i;
        double average = 1.0d * sum / numbers.length;
        return average;
    }

    public static double getPercentile(Long[] number, double p) {
        if (number == null)
            return 0;
        double res = 0;
        Arrays.sort(number);
        double x = (number.length - 1) * p;
        int i = (int) x;
        double j = x - i;
        res = (1 - j) * number[i] + j * number[i + 1];
        return res;
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            logger.warn("Expect input file name and host name");
            System.exit(1);
        }

        logger.info("Requested host is {}, this will override settings in project.properties", args[1]);

        System.setProperty(Constant.PROPERTY_EXPERIMENT_HOST, args[1]);

        logger.info("Transaction file name is {}", args[0]);

        Scanner sc = new Scanner(Paths.get(ProjectConfig.getInstance().getProjectRoot(), ProjectConfig.getInstance().getTransactionFileFolder(),
                args[0]));

        String outFilePath = "/home/stuproj/cs4224c/ExperimentClient_performance_Measurement.txt";

        PrintWriter out = new PrintWriter(new FileOutputStream(new File(outFilePath), true));

        int numOfTransaction = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        stopWatch.suspend();

        List<Long> transactionLatencyList = new ArrayList<>();
        long startTime = 0L;
        long endTime = 0L;

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] arguments = line.split(Constant.COMMA_SEPARATOR);
            String command = arguments[INDEX_COMMAND];

            Class<? extends AbstractParser> parserClass = ParserMap.get(command);
            if (parserClass == null) {
                System.out.println("Invalid command: " + command);
            }

            AbstractParser parser = parserClass.newInstance();
            AbstractTransaction transaction = parser.parse(sc, arguments);

            numOfTransaction++;
            stopWatch.resume();
            startTime = System.currentTimeMillis();
            transaction.execute();
            endTime = System.currentTimeMillis();
            transactionLatencyList.add(endTime-startTime);
            stopWatch.suspend();
            System.out.println();
        }

        System.err.println("\n[SUMMARY]");
        System.err.println("Number of executed transactions: " + numOfTransaction);
        System.err.println("Total transaction execution time (seconds): " + stopWatch.getTime() / 1000.0);
        System.err.println("Transaction throughput: " + numOfTransaction / (stopWatch.getTime() / 1000.0));

        out.println("******************************");
        out.println("Transaction file name is" + args[0]);
        out.println();
        out.println("Transaction count: " + numOfTransaction);
        out.println("Total transaction execution time: " + stopWatch.getTime() / 1000.0);
        out.println("Transaction throughput: " + numOfTransaction / (stopWatch.getTime() / 1000.0));

        Long[] tl = new Long[transactionLatencyList.size()];
        out.println("Average transaction latency: " + ExperimentClient.average(transactionLatencyList.toArray(tl)));
        out.println("Median transaction latency: " + ExperimentClient.median(transactionLatencyList.toArray(tl)));
        out.println("95th percentile transaction latency: " + ExperimentClient.getPercentile(transactionLatencyList.toArray(tl),0.95));
        out.println("99th percentile transaction latency: " + ExperimentClient.getPercentile(transactionLatencyList.toArray(tl),0.99));
        out.println();
        out.println("******************************");
        out.println();
        out.close();
    }
}
