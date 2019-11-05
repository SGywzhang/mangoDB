package client.cs4224c.transaction.topbalance.data;

public class CustomerData {

    private short W_ID;
    private short D_ID;
    private int C_ID;

    private double C_BALANCE;

    private String W_NAME;
    private String D_NAME;

    private String C_FIRST;
    private String C_MIDDLE;
    private String C_LAST;
    
    public CustomerData(short w_ID, short d_ID, int c_ID, double c_BALANCE) {
        W_ID = w_ID;
        D_ID = d_ID;
        C_ID = c_ID;
        C_BALANCE = c_BALANCE;
    }

    public void setW_ID(short w_ID) {
        W_ID = w_ID;
    }

    public void setD_ID(short d_ID) {
        D_ID = d_ID;
    }

    public void setC_ID(int c_ID) {
        C_ID = c_ID;
    }

    public void setC_BALANCE(double c_BALANCE) {
        C_BALANCE = c_BALANCE;
    }

    public void setW_NAME(String w_NAME) {
        W_NAME = w_NAME;
    }

    public void setD_NAME(String d_NAME) {
        D_NAME = d_NAME;
    }

    public void setC_FIRST(String c_FIRST) {
        C_FIRST = c_FIRST;
    }

    public void setC_MIDDLE(String c_MIDDLE) {
        C_MIDDLE = c_MIDDLE;
    }

    public void setC_LAST(String c_LAST) {
        C_LAST = c_LAST;
    }

    public short getW_ID() {
        return W_ID;
    }
  
    public short getD_ID() {
        return D_ID;
    }

    public int getC_ID() {
        return C_ID;
    }

    public double getC_BALANCE() {
        return C_BALANCE;
    }

    public String getW_NAME() {
        return W_NAME;
    }

    public String getD_NAME() {
        return D_NAME;
    }

    public String getC_FIRST() {
        return C_FIRST;
    }
    
    public String getC_MIDDLE() {
        return C_MIDDLE;
    }
    
    public String getC_LAST() {
        return C_LAST;
    }

}
