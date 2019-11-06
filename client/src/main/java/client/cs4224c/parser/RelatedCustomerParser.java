package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.relatedcustomer.RelatedCustomerTransaction;
import client.cs4224c.transaction.relatedcustomer.data.RelatedCustomerData;

import java.util.Scanner;

public class RelatedCustomerParser extends AbstractParser {

    private static int INDEX_W_ID = 1;
    private static int INDEX_CARRIER_ID = 2;

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
    	RelatedCustomerTransaction transaction = new RelatedCustomerTransaction ();

        RelatedCustomerData data = new RelatedCustomerData ();

        data.setC_W_ID(Integer.parseInt(arguments[1]));
        data.setC_D_ID(Integer.parseInt(arguments[2]));
        data.setC_ID(Integer.parseInt(arguments[3]));

        transaction.setData(data);
        return transaction;
    }
}
