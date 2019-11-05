package client.cs4224c.transaction.database;

import client.cs4224c.transaction.AbstractTransaction;
import client.cs4224c.util.Collection;
import client.cs4224c.util.CollectionPool;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import java.util.Arrays;

public class DatabaseStateTransaction extends AbstractTransaction {

    @Override
    public void executeFlow() {
        CollectionPool collectionPool = CollectionPool.getInstance();

        Document districtAggregation = collectionPool.getCollection(Collection.District)
                .aggregate(
                        Arrays.asList(
                            Aggregates.group("", Arrays.asList(Accumulators.sum("d_next_o_id", "$d_next_o_id"), Accumulators.sum("d_ytd", "$d_ytd")))
                        )
                )
                .first();
        Document customerAggregation = collectionPool.getCollection(Collection.Customer)
                .aggregate(
                        Arrays.asList(
                                Aggregates.group("", Arrays.asList(Accumulators.sum("c_balance", "$c_balance"), Accumulators.sum("c_ytd_payment", "$c_ytd_payment"),
                                        Accumulators.sum("c_payment_cnt", "$c_payment_cnt"), Accumulators.sum("c_delivery_cnt", "$c_delivery_cnt")))
                        )
                )
                .first();
        System.out.println(String.format("(a). select sum(W_YTD) from Warehouse : %.2f", districtAggregation.getDouble("d_ytd")));
        System.out.println(String.format("(b). select sum(D_YTD), sum(D_NEXT_O_ID) from District : %.2f, %d", districtAggregation.getDouble("d_ytd"),
                districtAggregation.getInteger("d_next_o_id")));
        System.out.println(String.format("(c). select sum(C_BALANCE), sum(C_YTD_PAYMENT), sum(C_PAYMENT CNT), sum(C_DELIVERY_CNT) from Customer : %.2f, %.4f, %d, %d",
                customerAggregation.getDouble("c_balance"), customerAggregation.getDouble("c_ytd_payment"), customerAggregation.getInteger("c_payment_cnt"), customerAggregation.getInteger("c_delivery_cnt")));

        // map-reduce to get o_id as it is represent inside a string
        Document orderItemReduce = collectionPool.getCollection(Collection.OrderItem)
                .mapReduce("function() { emit('all', parseInt(this._id.substring(this._id.lastIndexOf('-') + 1, this._id.length))) }", "function(key, values) { var max = values[0]; values.forEach(function(val){ if (val > max) {max = val}; }); return max; }")
                .first();

        Document orderItemAggregation = collectionPool.getCollection(Collection.OrderItem)
                .aggregate(
                        Arrays.asList(
                                Aggregates.group("", Arrays.asList(Accumulators.sum("o_ol_cnt", "$o_ol_cnt")))
                        )
                )
                .first();
        System.out.println(String.format("(d). select max(O_ID), sum(O_OL_CNT) from Order : %.0f, %d", orderItemReduce.getDouble("value"), orderItemAggregation.getInteger("o_ol_cnt")));

        Document orderLineAggregation = collectionPool.getCollection(Collection.OrderItem)
                .aggregate(
                        Arrays.asList(
                                Aggregates.unwind("$orderlines"),
                                Aggregates.group("", Arrays.asList(Accumulators.sum("ol_amount", "$orderlines.ol_amount"), Accumulators.sum("ol_quantity", "$orderlines.ol_quantity")))
                        )
                )
                .first();
        System.out.println(String.format("(e). select sum(OL_AMOUNT), sum(OL_QUANTITY) from Order-Line : %.2f, %d",
                orderLineAggregation.getDouble("ol_amount"), orderLineAggregation.getInteger("ol_quantity")));

        Document stockAggregation = collectionPool.getCollection(Collection.Stock)
                .aggregate(
                        Arrays.asList(
                                Aggregates.group("", Arrays.asList(Accumulators.sum("s_quantity", "$s_quantity"), Accumulators.sum("s_ytd", "$s_ytd"),
                                        Accumulators.sum("s_order_cnt", "$s_order_cnt"), Accumulators.sum("s_remote_cnt", "$s_remote_cnt")))
                        )
                )
                .first();
        System.out.println(String.format("(f). select sum(S_QUANTITY), sum(S_YTD), sum(S_ORDER_CNT), sum(S_REMOTE_CNT) from Stock: %d, %.2f, %d, %d",
                stockAggregation.getInteger("s_quantity"), stockAggregation.getDouble("s_ytd"), stockAggregation.getInteger("s_order_cnt"), stockAggregation.getInteger("s_remote_cnt")));
    }

}
