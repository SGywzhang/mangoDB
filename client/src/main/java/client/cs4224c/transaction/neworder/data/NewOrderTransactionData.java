package client.cs4224c.transaction.neworder.data;

import java.util.Date;
import java.util.List;

public class NewOrderTransactionData {

    private int C_ID;
    private short W_ID;
    private short D_ID;
    private int M;

    private boolean isAllLocal;

    private List<NewOrderTransactionOrderLine> orderLines;

    private double W_TAX;
    private double D_TAX;

    private double C_DISCOUNT;
    private String C_CREDIT;
    private String C_LAST;

    private Date O_ENTRY_D;


    public int getC_ID() {
        return C_ID;
    }

    public void setC_ID(int c_ID) {
        C_ID = c_ID;
    }

    public short getW_ID() {
        return W_ID;
    }

    public void setW_ID(short w_ID) {
        W_ID = w_ID;
    }

    public short getD_ID() {
        return D_ID;
    }

    public void setD_ID(short d_ID) {
        D_ID = d_ID;
    }

    public int getM() {
        return M;
    }

    public void setM(int m) {
        M = m;
    }

    public List<NewOrderTransactionOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<NewOrderTransactionOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public boolean isAllLocal() {
        return isAllLocal;
    }

    public void setAllLocal(boolean allLocal) {
        isAllLocal = allLocal;
    }

    public double getW_TAX() {
        return W_TAX;
    }

    public void setW_TAX(double w_TAX) {
        W_TAX = w_TAX;
    }

    public double getD_TAX() {
        return D_TAX;
    }

    public void setD_TAX(double d_TAX) {
        D_TAX = d_TAX;
    }

    public double getC_DISCOUNT() {
        return C_DISCOUNT;
    }

    public void setC_DISCOUNT(double c_DISCOUNT) {
        C_DISCOUNT = c_DISCOUNT;
    }

    public String getC_CREDIT() {
        return C_CREDIT;
    }

    public void setC_CREDIT(String c_CREDIT) {
        C_CREDIT = c_CREDIT;
    }

    public String getC_LAST() {
        return C_LAST;
    }

    public void setC_LAST(String c_LAST) {
        C_LAST = c_LAST;
    }

    public Date getO_ENTRY_D() {
        return O_ENTRY_D;
    }

    public void setO_ENTRY_D(Date o_ENTRY_D) {
        O_ENTRY_D = o_ENTRY_D;
    }
}
