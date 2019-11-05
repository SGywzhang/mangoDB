package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.orderstatus.OrderStatusTransaction;
import client.cs4224c.transaction.orderstatus.data.OrderStatusTransactionData;

import java.util.Scanner;

public class OrderStatusParser extends AbstractParser {
    private static int INDEX_C_W_ID = 1;
    private static int INDEX_C_D_ID = 2;
    private static int INDEX_C_ID = 3;

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
        OrderStatusTransaction transaction = new OrderStatusTransaction();

        OrderStatusTransactionData data = new OrderStatusTransactionData();
        data.setC_W_ID(Short.parseShort(arguments[INDEX_C_W_ID]));
        data.setC_D_ID(Short.parseShort(arguments[INDEX_C_D_ID]));
        data.setC_ID(Integer.parseInt(arguments[INDEX_C_ID]));

        transaction.setData(data);

        return transaction;
    }
}
