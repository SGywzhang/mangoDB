package client.cs4224c.transaction.popularitem.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PopularItemTransactionData {

    private short W_ID;
    private short D_ID;
    private int L;

    private List<OrderData> lastLOrders = new ArrayList<OrderData>();
    private List<OrderLineItemData> popularItems = new ArrayList<OrderLineItemData>();
    private HashMap<Integer, List<Integer>> itemsMap = new HashMap<Integer, List<Integer>>();

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

    public int getL() {
        return L;
    }

    public void setL(int l) {
        L = l;
    }

    public List<OrderData> getLastLOrders() {
        return lastLOrders;
    }

    public void setLastLOrders(List<OrderData> lastLOrders) {
        this.lastLOrders = lastLOrders;
    }

    public List<OrderLineItemData> getPopularItems() {
        return popularItems;
    }

    public void setPopularItems(List<OrderLineItemData> popularItems) {
        this.popularItems = popularItems;
    }

    public HashMap<Integer, List<Integer>> getItemsMap() {
        return itemsMap;
    }

    public void setItemsMap(HashMap<Integer, List<Integer>> itemsMap) {
        this.itemsMap = itemsMap;
    }
}
