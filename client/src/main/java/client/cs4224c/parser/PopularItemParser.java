package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.popularitem.PopularItemTransaction;
import client.cs4224c.transaction.popularitem.data.PopularItemTransactionData;

import java.util.Scanner;

public class PopularItemParser extends AbstractParser {

    private static int INDEX_W_ID = 1;
    private static int INDEX_D_ID = 2;
    private static int INDEX_L = 3;

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
        PopularItemTransaction transaction = new PopularItemTransaction();

        PopularItemTransactionData data = new PopularItemTransactionData();
        data.setW_ID(Short.parseShort(arguments[INDEX_W_ID]));
        data.setD_ID(Short.parseShort(arguments[INDEX_D_ID]));
        data.setL(Integer.parseInt(arguments[INDEX_L]));

        transaction.setData(data);

        return transaction;
    }
}
