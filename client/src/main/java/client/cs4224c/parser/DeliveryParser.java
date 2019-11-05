package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.delivery.DeliveryTransaction;
import client.cs4224c.transaction.delivery.data.DeliveryTransactionData;

import java.util.Scanner;

public class DeliveryParser extends AbstractParser {
    private static int INDEX_W_ID = 1;
    private static int INDEX_CARRIER_ID = 2;

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
        DeliveryTransaction transaction = new DeliveryTransaction();

        DeliveryTransactionData data = new DeliveryTransactionData();
        data.setW_ID(Short.parseShort(arguments[INDEX_W_ID]));
        data.setCARRIER_ID(Integer.parseInt(arguments[INDEX_CARRIER_ID]));

        transaction.setData(data);
        return transaction;
    }
}
