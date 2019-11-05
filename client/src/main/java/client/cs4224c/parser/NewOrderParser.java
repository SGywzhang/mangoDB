package client.cs4224c.parser;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.neworder.NewOrderTransaction;
import client.cs4224c.transaction.neworder.data.NewOrderTransactionData;
import client.cs4224c.transaction.neworder.data.NewOrderTransactionOrderLine;
import client.cs4224c.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NewOrderParser extends AbstractParser {

    private static int INDEX_C_ID = 1;
    private static int INDEX_W_ID = 2;
    private static int INDEX_D_ID = 3;
    private static int INDEX_M = 4;

    private static int INDEX_OL_I_ID = 0;
    private static int INDEX_OL_SUPPLY_W_ID = 1;
    private static int INDEX_OL_QUANTITY = 2;

    @Override
    public AbstractTransaction parse(Scanner sc, String[] arguments) {
        NewOrderTransaction transaction = new NewOrderTransaction();

        NewOrderTransactionData data = new NewOrderTransactionData();
        data.setC_ID(Integer.parseInt(arguments[INDEX_C_ID]));
        data.setW_ID(Short.parseShort(arguments[INDEX_W_ID]));
        data.setD_ID(Short.parseShort(arguments[INDEX_D_ID]));
        data.setM(Integer.parseInt(arguments[INDEX_M]));

        List<NewOrderTransactionOrderLine> orderLines = new ArrayList<>();
        boolean isAllLocal = true;
        for (int i = 0; i < data.getM(); i++) {
            NewOrderTransactionOrderLine orderLine = new NewOrderTransactionOrderLine();
            String[] lineArguments = sc.nextLine().split(Constant.COMMA_SEPARATOR);

            orderLine.setOL_I_ID(Integer.parseInt(lineArguments[INDEX_OL_I_ID]));
            orderLine.setOL_SUPPLY_W_ID(Short.parseShort(lineArguments[INDEX_OL_SUPPLY_W_ID]));
            orderLine.setOL_QUANTITY(Integer.parseInt(lineArguments[INDEX_OL_QUANTITY]));

            if (isAllLocal && (data.getW_ID() != orderLine.getOL_SUPPLY_W_ID())) {
                isAllLocal = false;
            }

            orderLines.add(orderLine);
        }
        data.setAllLocal(isAllLocal);
        data.setOrderLines(orderLines);

        transaction.setData(data);

        return transaction;
    }

}
