package client.cs4224c.transaction.popularitem.data;

public class OrderLineItemData {
    private int OL_I_ID;
    private int OL_QUANTITY;
    private String I_NAME;

    public int getOL_I_ID() {
        return OL_I_ID;
    }

    public void setOL_I_ID(int OL_I_ID) {
        this.OL_I_ID = OL_I_ID;
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
}
