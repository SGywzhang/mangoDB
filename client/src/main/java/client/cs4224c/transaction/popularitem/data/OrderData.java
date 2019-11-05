package client.cs4224c.transaction.popularitem.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderData {
    private int O_ID;
    private Date O_ENTRY_DATE;

    private String C_FIRST;
    private String C_MIDDLE;
    private String C_LAST;

    private List<OrderLineItemData> popularItems = new ArrayList<OrderLineItemData>();

    public int getO_ID() {
        return O_ID;
    }

    public void setO_ID(int o_ID) {
        O_ID = o_ID;
    }

    public Date getO_ENTRY_DATE() {
        return O_ENTRY_DATE;
    }

    public void setO_ENTRY_DATE(Date o_ENTRY_DATE) {
        O_ENTRY_DATE = o_ENTRY_DATE;
    }

    public String getC_FIRST() {
        return C_FIRST;
    }

    public void setC_FIRST(String c_FIRST) {
        C_FIRST = c_FIRST;
    }

    public String getC_MIDDLE() {
        return C_MIDDLE;
    }

    public void setC_MIDDLE(String c_MIDDLE) {
        C_MIDDLE = c_MIDDLE;
    }

    public String getC_LAST() {
        return C_LAST;
    }

    public void setC_LAST(String c_LAST) {
        C_LAST = c_LAST;
    }

    public List<OrderLineItemData> getPopularItems() {
        return popularItems;
    }

    public void setPopularItems(List<OrderLineItemData> popularItems) {
        this.popularItems = popularItems;
    }
}
