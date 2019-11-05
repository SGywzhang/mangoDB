package client.cs4224c.transaction.payment.data;

public class PaymentTransactionData {

    private short C_W_ID;

    private short C_D_ID;

    private int C_ID;

    private double PAYMENT;

    public short getC_W_ID() {
        return C_W_ID;
    }

    public void setC_W_ID(short c_W_ID) {
        C_W_ID = c_W_ID;
    }

    public short getC_D_ID() {
        return C_D_ID;
    }

    public void setC_D_ID(short c_D_ID) {
        C_D_ID = c_D_ID;
    }

    public int getC_ID() {
        return C_ID;
    }

    public void setC_ID(int c_ID) {
        C_ID = c_ID;
    }

    public double getPAYMENT() {
        return PAYMENT;
    }

    public void setPAYMENT(double PAYMENT) {
        this.PAYMENT = PAYMENT;
    }
}
