package massage.cs4224c.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import massage.cs4224c.util.Constant;

public class District {

    private String _id;

    private String d_name;
    private String d_street_1;
    private String d_street_2;
    private String d_city;
    private String d_state;
    private String d_zip;
    private double d_tax;

    private double d_ytd;
    private int d_next_o_id;
    private Integer dt_min_ud_o_id;

    private Warehouse warehouse = new Warehouse();

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    public void set_id(String d_w_id, String d_id) {
        this._id = String.join(Constant.FIELD_CONNECTOR, d_w_id, d_id);
    }

    public String getD_name() {
        return d_name;
    }

    public void setD_name(String d_name) {
        this.d_name = d_name;
    }

    public String getD_street_1() {
        return d_street_1;
    }

    public void setD_street_1(String d_street_1) {
        this.d_street_1 = d_street_1;
    }

    public String getD_street_2() {
        return d_street_2;
    }

    public void setD_street_2(String d_street_2) {
        this.d_street_2 = d_street_2;
    }

    public String getD_city() {
        return d_city;
    }

    public void setD_city(String d_city) {
        this.d_city = d_city;
    }

    public String getD_state() {
        return d_state;
    }

    public void setD_state(String d_state) {
        this.d_state = d_state;
    }

    public String getD_zip() {
        return d_zip;
    }

    public void setD_zip(String d_zip) {
        this.d_zip = d_zip;
    }

    public double getD_tax() {
        return d_tax;
    }

    public void setD_tax(double d_tax) {
        this.d_tax = d_tax;
    }

    public double getD_ytd() {
        return d_ytd;
    }

    public void setD_ytd(double d_ytd) {
        this.d_ytd = d_ytd;
    }

    public int getD_next_o_id() {
        return d_next_o_id;
    }

    public void setD_next_o_id(int d_next_o_id) {
        this.d_next_o_id = d_next_o_id;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getDt_min_ud_o_id() {
        return dt_min_ud_o_id;
    }

    public void setDt_min_ud_o_id(Integer dt_min_ud_o_id) {
        this.dt_min_ud_o_id = dt_min_ud_o_id;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}
