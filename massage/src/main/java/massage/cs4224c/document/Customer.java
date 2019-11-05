package massage.cs4224c.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import massage.cs4224c.util.Constant;

public class Customer {

    private String _id;
    private String c_first;
    private String c_middle;
    private String c_last;
    private String c_street_1;
    private String c_street_2;
    private String c_city;
    private String c_state;
    private String c_zip;
    private String c_phone;
    private Timestamp c_since;
    private String c_credit;
    private double c_credit_lim;
    private double c_discount;
    private String c_data;

    private Integer c_last_order;
    private double c_balance;
    private double c_ytd_payment;
    private int c_payment_cnt;
    private int c_delivery_cnt;

    private String c_w_name;
    private String c_d_name;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    public void set_id(String c_w_id, String c_d_id, String c_id) {
        this._id = String.join(Constant.FIELD_CONNECTOR, c_w_id, c_d_id, c_id);
    }

    public String getC_first() {
        return c_first;
    }

    public void setC_first(String c_first) {
        this.c_first = c_first;
    }

    public String getC_middle() {
        return c_middle;
    }

    public void setC_middle(String c_middle) {
        this.c_middle = c_middle;
    }

    public String getC_last() {
        return c_last;
    }

    public void setC_last(String c_last) {
        this.c_last = c_last;
    }

    public String getC_street_1() {
        return c_street_1;
    }

    public void setC_street_1(String c_street_1) {
        this.c_street_1 = c_street_1;
    }

    public String getC_street_2() {
        return c_street_2;
    }

    public void setC_street_2(String c_street_2) {
        this.c_street_2 = c_street_2;
    }

    public String getC_city() {
        return c_city;
    }

    public void setC_city(String c_city) {
        this.c_city = c_city;
    }

    public String getC_state() {
        return c_state;
    }

    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    public String getC_zip() {
        return c_zip;
    }

    public void setC_zip(String c_zip) {
        this.c_zip = c_zip;
    }

    public String getC_phone() {
        return c_phone;
    }

    public void setC_phone(String c_phone) {
        this.c_phone = c_phone;
    }

    public Timestamp getC_since() {
        return c_since;
    }

    public void setC_since(long c_since_timestamp) {
        this.c_since = new Timestamp(c_since_timestamp);
    }

    public String getC_credit() {
        return c_credit;
    }

    public void setC_credit(String c_credit) {
        this.c_credit = c_credit;
    }

    public double getC_credit_lim() {
        return c_credit_lim;
    }

    public void setC_credit_lim(double c_credit_lim) {
        this.c_credit_lim = c_credit_lim;
    }

    public double getC_discount() {
        return c_discount;
    }

    public void setC_discount(double c_discount) {
        this.c_discount = c_discount;
    }

    public String getC_data() {
        return c_data;
    }

    public void setC_data(String c_data) {
        this.c_data = c_data;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getC_last_order() {
        return c_last_order;
    }

    public void setC_last_order(Integer c_last_order) {
        this.c_last_order = c_last_order;
    }

    public double getC_balance() {
        return c_balance;
    }

    public void setC_balance(double c_balance) {
        this.c_balance = c_balance;
    }

    public double getC_ytd_payment() {
        return c_ytd_payment;
    }

    public void setC_ytd_payment(double c_ytd_payment) {
        this.c_ytd_payment = c_ytd_payment;
    }

    public int getC_payment_cnt() {
        return c_payment_cnt;
    }

    public void setC_payment_cnt(int c_payment_cnt) {
        this.c_payment_cnt = c_payment_cnt;
    }

    public int getC_delivery_cnt() {
        return c_delivery_cnt;
    }

    public void setC_delivery_cnt(int c_delivery_cnt) {
        this.c_delivery_cnt = c_delivery_cnt;
    }

    public String getC_w_name() {
        return c_w_name;
    }

    public void setC_w_name(String c_w_name) {
        this.c_w_name = c_w_name;
    }

    public String getC_d_name() {
        return c_d_name;
    }

    public void setC_d_name(String c_d_name) {
        this.c_d_name = c_d_name;
    }
}
