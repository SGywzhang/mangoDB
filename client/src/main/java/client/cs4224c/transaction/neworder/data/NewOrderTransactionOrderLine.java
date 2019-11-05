package client.cs4224c.transaction.neworder.data;

public class NewOrderTransactionOrderLine {

    private int OL_I_ID;
    private short OL_SUPPLY_W_ID;
    private int OL_QUANTITY;

    private String I_NAME;
    private double OL_AMOUNT;
    private int S_QUANTITY;

    public int getOL_I_ID() {
        return OL_I_ID;
    }

    public void setOL_I_ID(int OL_I_ID) {
        this.OL_I_ID = OL_I_ID;
    }

    public short getOL_SUPPLY_W_ID() {
        return OL_SUPPLY_W_ID;
    }

    public void setOL_SUPPLY_W_ID(short OL_SUPPLY_W_ID) {
        this.OL_SUPPLY_W_ID = OL_SUPPLY_W_ID;
    }

    public int getOL_QUANTITY() {
        return OL_QUANTITY;
    }

    public void setOL_QUANTITY(int OL_QUANTITY) {
        this.OL_QUANTITY = OL_QUANTITY;
    }

    public String getI_NAME() {
        return I_NAME;
    }

    public void setI_NAME(String i_NAME) {
        I_NAME = i_NAME;
    }

    public double getOL_AMOUNT() {
        return OL_AMOUNT;
    }

    public void setOL_AMOUNT(double OL_AMOUNT) {
        this.OL_AMOUNT = OL_AMOUNT;
    }

    public int getS_QUANTITY() {
        return S_QUANTITY;
    }

    public void setS_QUANTITY(int s_QUANTITY) {
        S_QUANTITY = s_QUANTITY;
    }
}
