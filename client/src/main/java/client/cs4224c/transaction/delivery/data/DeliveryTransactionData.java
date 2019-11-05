package client.cs4224c.transaction.delivery.data;

public class DeliveryTransactionData {
    private short W_ID;
    private int CARRIER_ID;

    public short getW_ID() {
        return W_ID;
    }

    public void setW_ID(short w_ID) {
        W_ID = w_ID;
    }

    public int getCARRIER_ID() {
        return CARRIER_ID;
    }

    public void setCARRIER_ID(int CARRIER_ID) {
        this.CARRIER_ID = CARRIER_ID;
    }
}
