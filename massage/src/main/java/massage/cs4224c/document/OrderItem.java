package massage.cs4224c.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import massage.cs4224c.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {
    private String _id;

    private Integer o_carrier_id;
    private int o_c_id;
    private int o_ol_cnt;
    private boolean o_all_local;
    private Timestamp o_entry_d;

    private PartialCustomer customer = new PartialCustomer();

    private List<OrderItemOrderLines> orderlines = new ArrayList<>();

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    public void set_id(String o_w_id, String o_d_id, String o_id) {
        this._id = String.join(Constant.FIELD_CONNECTOR, o_w_id, o_d_id, o_id);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getO_carrier_id() {
        return o_carrier_id;
    }

    public void setO_carrier_id(Integer o_carrier_id) {
        this.o_carrier_id = o_carrier_id;
    }

    public int getO_c_id() {
        return o_c_id;
    }

    public void setO_c_id(int o_c_id) {
        this.o_c_id = o_c_id;
    }

    public int getO_ol_cnt() {
        return o_ol_cnt;
    }

    public void setO_ol_cnt(int o_ol_cnt) {
        this.o_ol_cnt = o_ol_cnt;
    }

    public boolean isO_all_local() {
        return o_all_local;
    }

    public void setO_all_local(boolean o_all_local) {
        this.o_all_local = o_all_local;
    }

    public Timestamp getO_entry_d() {
        return o_entry_d;
    }

    public void setO_entry_d(long o_entry_d_timestamp) {
        this.o_entry_d = new Timestamp(o_entry_d_timestamp);
    }

    public PartialCustomer getCustomer() {
        return customer;
    }

    public List<OrderItemOrderLines> getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(List<OrderItemOrderLines> orderlines) {
        this.orderlines = orderlines;
    }
}
