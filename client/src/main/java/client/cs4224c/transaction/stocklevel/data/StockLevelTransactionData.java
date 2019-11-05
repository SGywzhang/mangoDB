package client.cs4224c.transaction.stocklevel.data;

import java.util.ArrayList;
import java.util.List;

public class StockLevelTransactionData {

    private short W_ID;

    private short D_ID;

    private int T;

    private int L;

    private List<Integer> orderlineItems = new ArrayList<Integer>();
    private List<Integer> lowStockItems = new ArrayList<Integer>();

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

    public int getT() {
        return T;
    }

    public void setT(int t) {
        T = t;
    }

    public int getL() {
        return L;
    }

    public void setL(int l) {
        L = l;
    }

    public List<Integer> getOrderlineItems() {
        return orderlineItems;
    }

    public void setOrderlineItems(List<Integer> orderlineItems) {
        this.orderlineItems = orderlineItems;
    }

    public List<Integer> getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(List<Integer> lowStockItems) {
        this.lowStockItems = lowStockItems;
    }
}
