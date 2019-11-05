package massage.cs4224c.converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import massage.cs4224c.document.OrderItem;
import massage.cs4224c.document.OrderItemOrderLines;
import massage.cs4224c.util.ProjectConfig;
import massage.cs4224c.util.TimeUtility;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderItemCollection extends AbstractConverter {

    private final static Logger logger = LoggerFactory.getLogger(OrderItemCollection.class);

    private static final int C_W_ID = 0;
    private static final int C_D_ID = 1;
    private static final int C_ID = 2;
    private static final int C_FIRST = 3;
    private static final int C_MIDDLE = 4;
    private static final int C_LAST = 5;

    private static final int OL_W_ID = 0;
    private static final int OL_D_ID = 1;
    private static final int OL_O_ID = 2;
    private static final int OL_NUMBER = 3;
    private static final int OL_I_ID = 4;
    private static final int OL_DELIVERY_D = 5;
    private static final int OL_AMOUNT = 6;
    private static final int OL_SUPPLY_W_ID = 7;
    private static final int OL_QUANTITY = 8;
    private static final int OL_DIST_INFO = 9;

    private static final int I_ID = 0;
    private static final int I_NAME = 1;
    private static final int I_PRICE = 2;
    private static final int I_IM_ID = 3;
    private static final int I_DATA = 4;

    private static final int O_W_ID = 0;
    private static final int O_D_ID = 1;
    private static final int O_ID = 2;
    private static final int O_C_ID = 3;
    private static final int O_CARRIER_ID = 4;
    private static final int O_OL_CNT = 5;
    private static final int O_ALL_LOCAL = 6;
    private static final int O_ENTRY_D = 7;

    public static void main(String[] args) {
        AbstractConverter orderLineItem = new OrderItemCollection();
        orderLineItem.run();
    }

    @Override
    public void massage() throws Exception {
        ProjectConfig config = ProjectConfig.getInstance();

        Reader orderLineReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "order-line.csv").toFile());
        Iterable<CSVRecord> orderLineRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(orderLineReader);
        Map<Triple<String, String, String>, List<CSVRecord>> orderlines = new HashMap<>();
        for (CSVRecord orderline : orderLineRecords) {
            Triple<String, String, String> triple = new ImmutableTriple<>(orderline.get(OL_W_ID), orderline.get(OL_D_ID), orderline.get(OL_O_ID));
            if (!orderlines.containsKey(triple)) {
                orderlines.put(triple, new ArrayList<>());
            }
            orderlines.get(triple).add(orderline);
        }


        Reader customerReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "customer.csv").toFile());
        Iterable<CSVRecord> customerRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(customerReader);
        Map<Triple<String, String, String>, CSVRecord> customers = new HashMap<>();
        for (CSVRecord customer : customerRecords) {
            customers.put(new ImmutableTriple<>(customer.get(C_W_ID), customer.get(C_D_ID), customer.get(C_ID)), customer);
        }

        Reader itemReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "item.csv").toFile());
        Iterable<CSVRecord> itemRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(itemReader);
        Map<String, CSVRecord> items = new HashMap<String, CSVRecord>();
        for (CSVRecord item : itemRecords) {
            items.put(item.get(I_ID), item);
        }

        Reader orderReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "order.csv").toFile());
        Iterable<CSVRecord> orderRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(orderReader);

        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator = jfactory.createGenerator(Paths.get(config.getProjectRoot(), config.getDataDestFolder(), "orderItem.json").toFile(), JsonEncoding.UTF8);
        jGenerator.setCodec(new ObjectMapper());

        jGenerator.writeStartArray();
        for (CSVRecord order : orderRecords) {
            OrderItem orderItemDocument = new OrderItem();
            Triple<String, String, String> orderIdentifier = new ImmutableTriple<>(order.get(O_W_ID), order.get(O_D_ID), order.get(O_ID));
            List<CSVRecord> correspondingOrderlines = orderlines.get(orderIdentifier);

            if (correspondingOrderlines == null) {
                logger.warn("Notice that there is no orderline for order {}", orderIdentifier);
                return;
            }

            orderItemDocument.set_id(order.get(O_W_ID), order.get(O_D_ID), order.get(O_ID));

            String i_carrier_id = order.get(O_CARRIER_ID);
            orderItemDocument.setO_carrier_id("null".equals(i_carrier_id) ? null : Integer.parseInt(i_carrier_id));
            orderItemDocument.setO_c_id(Integer.parseInt(order.get(O_C_ID)));
            orderItemDocument.setO_ol_cnt(Integer.parseInt(order.get(O_OL_CNT)));
            orderItemDocument.setO_ol_cnt(Integer.parseInt(order.get(O_OL_CNT)));

            Boolean allLocal = "0".equals(order.get(O_ALL_LOCAL)) ? Boolean.FALSE : Boolean.TRUE;
            orderItemDocument.setO_all_local(allLocal);

            orderItemDocument.setO_entry_d(TimeUtility.parse(order.get(O_ENTRY_D)));

            CSVRecord correspondingCustomer = customers.get(new ImmutableTriple<>(order.get(O_W_ID), order.get(O_D_ID), order.get(O_C_ID)));
            orderItemDocument.getCustomer().setC_first(correspondingCustomer.get(C_FIRST));
            orderItemDocument.getCustomer().setC_middle(correspondingCustomer.get(C_MIDDLE));
            orderItemDocument.getCustomer().setC_last(correspondingCustomer.get(C_LAST));

            for (CSVRecord correspondingOrderline : correspondingOrderlines) {
                OrderItemOrderLines orderItemOrderLinesDocument = new OrderItemOrderLines();
                orderItemOrderLinesDocument.setOl_number(Integer.parseInt(correspondingOrderline.get(OL_NUMBER)));
                String deliveryD = correspondingOrderline.get(OL_DELIVERY_D);
                orderItemOrderLinesDocument.setOl_delivery_d("null".equals(deliveryD) ? null : TimeUtility.parse(deliveryD));
                orderItemOrderLinesDocument.setOl_amount(Double.parseDouble(correspondingOrderline.get(OL_AMOUNT)));
                orderItemOrderLinesDocument.setOl_supply_w_id(Integer.parseInt(correspondingOrderline.get(OL_SUPPLY_W_ID)));
                orderItemOrderLinesDocument.setOl_quantity(Integer.parseInt(correspondingOrderline.get(OL_QUANTITY)));
                orderItemOrderLinesDocument.setOl_dist_info(correspondingOrderline.get(OL_DIST_INFO));

                CSVRecord currItem = items.get(correspondingOrderline.get(OL_I_ID));
                orderItemOrderLinesDocument.setI_name(currItem.get(I_NAME));
                orderItemOrderLinesDocument.setOl_i_id(Integer.parseInt(correspondingOrderline.get(OL_I_ID)));

                orderItemDocument.getOrderlines().add(orderItemOrderLinesDocument);
            }

            jGenerator.writeObject(orderItemDocument);
        }
        jGenerator.writeEndArray();

        jGenerator.close();
    }
}
