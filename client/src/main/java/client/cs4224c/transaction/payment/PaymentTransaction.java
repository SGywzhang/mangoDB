package client.cs4224c.transaction.payment;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.transaction.payment.data.PaymentTransactionData;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import client.cs4224c.util.TimeUtility;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentTransaction extends AbstractTransaction {

    private final Logger logger = LoggerFactory.getLogger(PaymentTransaction.class);

    private PaymentTransactionData data;

    public PaymentTransactionData getData() {
        return data;
    }

    public void setData(PaymentTransactionData data) {
        this.data = data;
    }

    @Override
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();
        String customerKey = getCompoundKey(getStr(data.getC_W_ID()), getStr(data.getC_D_ID()), getStr(data.getC_ID()));
        String districtKey = getCompoundKey(getStr(data.getC_W_ID()), getStr(data.getC_D_ID()));

        logger.info("Update payment for customer and warehouse_district.");
        Document customerIncrement = new Document();
        customerIncrement.put("c_balance", -data.getPAYMENT());
        customerIncrement.put("c_ytd_payment", data.getPAYMENT());
        customerIncrement.put("c_payment_cnt", 1);
        collectionPool.getCollection(Collection.Customer)
                .updateOne(Filters.eq("_id", customerKey), new Document("$inc", customerIncrement));
        collectionPool.getCollection(Collection.District)
                .updateOne(Filters.eq("_id", districtKey), new Document("$inc", new Document("d_ytd", data.getPAYMENT())));

        Document customerDocument = collectionPool.getCollection(Collection.Customer)
                .find(Filters.eq("_id", customerKey))
                .projection(Projections.fields(
                        Projections.include("c_balance", "c_first", "c_middle", "c_last", "c_street_1", "c_street_2", "c_city", "c_state", "c_zip", "c_phone", "c_since", "c_credit", "c_credit_lim", "c_discount"),
                        Projections.excludeId())
                )
                .first();
        Document districtDocument = collectionPool.getCollection(Collection.District)
                .find(Filters.eq("_id", districtKey))
                .projection(Projections.fields(
                        Projections.include("d_street_1", "d_street_2", "d_city", "d_state", "d_zip", "warehouse.w_street_1", "warehouse.w_street_2", "warehouse.w_city", "warehouse.w_zip"),
                        Projections.excludeId())
                )
                .projection(Projections.excludeId())
                .first();

        logger.info("Output information now!");

        System.out.println(String.format("1. (C_W_ID: %d, C_D_ID: %d, C_ID: %d), Name: (%s, %s, %s), Address: (%s, %s, %s, %s, %s), C_PHONE: %s, C_SINCE: %s, C_CREDIT: %s, C_CREDIT_LIM: %.2f, C_DISCOUNT: %.4f, C_BALANCE: %.2f",
                data.getC_W_ID(), data.getC_D_ID(), data.getC_ID(),
                customerDocument.getString("c_first"), customerDocument.getString("c_middle"), customerDocument.getString("c_last"),
                customerDocument.getString("c_street_1"), customerDocument.getString("c_street_2"), customerDocument.getString("c_city"), customerDocument.getString("c_state"), customerDocument.getString("c_zip"),
                customerDocument.getString("c_phone"), TimeUtility.format(customerDocument.getDate("c_since")),
                customerDocument.getString("c_credit"), customerDocument.getDouble("c_credit_lim"), customerDocument.getDouble("c_discount"),
                customerDocument.getDouble("c_balance")
        ));
        Document warehouseDocument = districtDocument.get("warehouse", Document.class);
        System.out.println(String.format("2. Warehouse: %s, %s, %s, %s, %s",
                warehouseDocument.getString("w_street_1"), warehouseDocument.getString("w_street_2"), warehouseDocument.getString("w_city"),
                warehouseDocument.getString("w_state"), warehouseDocument.getString("w_zip")));
        System.out.println(String.format("3. District: %s, %s, %s, %s, %s",
                districtDocument.getString("d_street_1"), districtDocument.getString("d_street_2"), districtDocument.getString("d_city"),
                districtDocument.getString("d_state"), districtDocument.getString("d_zip")));
        System.out.println(String.format("4. PAYMENT: %.2f", data.getPAYMENT()));
    }
}
