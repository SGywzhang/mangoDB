package client.cs4224c;

import client.cs4224c.parser.AbstractParser;
import client.cs4224c.parser.ParserMap;
import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.util.Constant;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Scanner;

public class Client {

    private static int INDEX_COMMAND = 0;

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        stopWatch.suspend();
        int numOfTransaction = 0;

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] arguments = line.split(Constant.COMMA_SEPARATOR);
            String command = arguments[INDEX_COMMAND];

            Class<? extends AbstractParser> parserClass = ParserMap.get(command);
            if (parserClass == null) {
                System.out.println("Invalid command: " + command);
                continue;
            }

            AbstractParser parser = parserClass.newInstance();
            AbstractTransaction transaction = parser.parse(sc, arguments);

            numOfTransaction++;
            stopWatch.resume();
            transaction.execute();
            stopWatch.suspend();
            System.out.println();
        }
        stopWatch.stop();

        System.err.println("\n[SUMMARY]");
        System.err.println("Number of executed transactions: " + numOfTransaction);
        System.err.println("Total transaction execution time (seconds): " + stopWatch.getTime() / 1000.0);
        System.err.println("Transaction throughput: " + numOfTransaction / (stopWatch.getTime() / 1000.0));
    }
}
