package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.topbalance.TopBalanceTransaction;

import java.util.Scanner;

public class TopBalanceParser extends AbstractParser {

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
        TopBalanceTransaction transaction = new TopBalanceTransaction();

        return transaction;
    }

}
