package massage.cs4224c.document;

import com.fasterxml.jackson.annotation.JsonInclude;

public class OrderItemOrderLines {

    private int ol_number;
    private Timestamp ol_delivery_d;
    private double ol_amount;
    private int ol_supply_w_id;
    private int ol_quantity;
    private String ol_dist_info;
    private int ol_i_id;

    private String i_name;

    public int getOl_number() {
        return ol_number;
    }

    public void setOl_number(int ol_number) {
        this.ol_number = ol_number;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Timestamp getOl_delivery_d() {
        return ol_delivery_d;
    }

    public void setOl_delivery_d(Long ol_delivery_d_timestamp) {
        if (ol_delivery_d_timestamp != null) {
            this.ol_delivery_d = new Timestamp(ol_delivery_d_timestamp);
        }
    }

    public double getOl_amount() {
        return ol_amount;
    }

    public void setOl_amount(double ol_amount) {
        this.ol_amount = ol_amount;
    }

    public int getOl_supply_w_id() {
        return ol_supply_w_id;
    }

    public void setOl_supply_w_id(int ol_supply_w_id) {
        this.ol_supply_w_id = ol_supply_w_id;
    }

    public int getOl_quantity() {
        return ol_quantity;
    }

    public void setOl_quantity(int ol_quantity) {
        this.ol_quantity = ol_quantity;
    }

    public String getOl_dist_info() {
        return ol_dist_info;
    }

    public void setOl_dist_info(String ol_dist_info) {
        this.ol_dist_info = ol_dist_info;
    }

    public int getOl_i_id() {
        return ol_i_id;
    }

    public void setOl_i_id(int ol_i_id) {
        this.ol_i_id = ol_i_id;
    }

    public String getI_name() {
        return i_name;
    }

    public void setI_name(String i_name) {
        this.i_name = i_name;
    }
}
