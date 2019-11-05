package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.stocklevel.StockLevelTransaction;
import client.cs4224c.transaction.stocklevel.data.StockLevelTransactionData;

import java.util.Scanner;

public class StockLevelParser extends AbstractParser {

    private static int INDEX_W_ID = 1;
    private static int INDEX_D_ID = 2;
    private static int INDEX_T = 3;
    private static int INDEX_L = 4;

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
        StockLevelTransaction transaction = new StockLevelTransaction();

        StockLevelTransactionData data = new StockLevelTransactionData();
        data.setW_ID(Short.parseShort(arguments[INDEX_W_ID]));
        data.setD_ID(Short.parseShort(arguments[INDEX_D_ID]));
        data.setT(Integer.parseInt(arguments[INDEX_T]));
        data.setL(Integer.parseInt(arguments[INDEX_L]));

        transaction.setData(data);

        return transaction;
    }
}
