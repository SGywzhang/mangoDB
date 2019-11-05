package massage.cs4224c.converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import massage.cs4224c.document.Customer;
import massage.cs4224c.util.ProjectConfig;
import massage.cs4224c.util.TimeUtility;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class CustomerCollection extends AbstractConverter {

    private static final int C_W_ID = 0;
    private static final int C_D_ID = 1;
    private static final int C_ID = 2;
    private static final int C_FIRST = 3;
    private static final int C_MIDDLE = 4;
    private static final int C_LAST = 5;
    private static final int C_STREET_1 = 6;
    private static final int C_STREET_2 = 7;
    private static final int C_CITY = 8;
    private static final int C_STATE = 9;
    private static final int C_ZIP = 10;
    private static final int C_PHONE = 11;
    private static final int C_SINCE = 12;
    private static final int C_CREDIT = 13;
    private static final int C_CREDIT_LIM = 14;
    private static final int C_DISCOUNT = 15;
    private static final int C_BALANCE = 16;
    private static final int C_YTD_PAYMENT = 17;
    private static final int C_PAYMENT_CNT = 18;
    private static final int C_DELIVERY_CNT = 19;
    private static final int C_DATA = 20;

    private static final int O_W_ID = 0;
    private static final int O_D_ID = 1;
    private static final int O_ID = 2;
    private static final int O_C_ID = 3;

    private static final int W_ID = 0;
    private static final int W_NAME = 1;

    private static final int D_W_ID = 0;
    private static final int D_ID = 1;
    private static final int D_NAME = 2;

    public static void main(String[] args) {
        AbstractConverter customerCollection = new CustomerCollection();
        customerCollection.run();
    }

    @Override
    public void massage() throws Exception {
        ProjectConfig config = ProjectConfig.getInstance();

        Reader customerReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "customer.csv").toFile());
        Iterable<CSVRecord> customerRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(customerReader);

        Reader warehouseReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "warehouse.csv").toFile());
        Iterable<CSVRecord> warehouseRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(warehouseReader);
        Map<String, CSVRecord> warehouses = new HashMap<String, CSVRecord>();
        for (CSVRecord warehouse : warehouseRecords) {
            warehouses.put(warehouse.get(W_ID), warehouse);
        }

        Reader districtReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "district.csv").toFile());
        Iterable<CSVRecord> districtRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(districtReader);
        Map<Pair<String, String>, CSVRecord> districts = new HashMap<Pair<String, String>, CSVRecord>();
        for (CSVRecord district : districtRecords) {
            districts.put(new ImmutablePair<String, String>(district.get(D_W_ID), district.get(D_ID)), district);
        }

        Reader orderReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "order.csv").toFile());
        Iterable<CSVRecord> orderRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(orderReader);

        // pre-process to get customer's last order
        Map<Triple<String, String, String>, Integer> customerLatestOrder = new HashMap<Triple<String, String, String>, Integer>();
        for (CSVRecord order : orderRecords) {
            Triple<String, String, String> identifier = new ImmutableTriple<>(order.get(O_W_ID), order.get(O_D_ID), order.get(O_C_ID));
            if (!customerLatestOrder.containsKey(identifier)) {
                customerLatestOrder.put(identifier, Integer.parseInt(order.get(O_ID)));
            }
            Integer O_C_ID = customerLatestOrder.get(identifier);
            if (O_C_ID.toString().compareTo(order.get(O_ID)) < 0) { // use bigger order id
                customerLatestOrder.put(identifier, Integer.parseInt(order.get(O_ID)));
            }
        }

        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator = jfactory.createGenerator(Paths.get(config.getProjectRoot(), config.getDataDestFolder(), "customer.json").toFile(), JsonEncoding.UTF8);
        jGenerator.setCodec(new ObjectMapper());

        jGenerator.writeStartArray();
        for (CSVRecord customer : customerRecords) {
            Customer customerDocument = new Customer();

            customerDocument.set_id(customer.get(C_W_ID), customer.get(C_D_ID), customer.get(C_ID));
            customerDocument.setC_first(customer.get(C_FIRST));
            customerDocument.setC_middle(customer.get(C_MIDDLE));
            customerDocument.setC_last(customer.get(C_LAST));
            customerDocument.setC_street_1(customer.get(C_STREET_1));
            customerDocument.setC_street_2(customer.get(C_STREET_2));
            customerDocument.setC_city(customer.get(C_CITY));
            customerDocument.setC_state(customer.get(C_STATE));
            customerDocument.setC_zip(customer.get(C_ZIP));
            customerDocument.setC_phone(customer.get(C_PHONE));
            customerDocument.setC_since(TimeUtility.parse(customer.get(C_SINCE)));
            customerDocument.setC_credit(customer.get(C_CREDIT));
            customerDocument.setC_credit_lim(Double.parseDouble(customer.get(C_CREDIT_LIM)));
            customerDocument.setC_discount(Double.parseDouble(customer.get(C_DISCOUNT)));
            customerDocument.setC_data(customer.get(C_DATA));

            Triple<String, String, String> identifier = new ImmutableTriple<>(customer.get(C_W_ID), customer.get(C_D_ID), customer.get(C_ID));
            customerDocument.setC_last_order(customerLatestOrder.getOrDefault(identifier, null));

            customerDocument.setC_balance(Double.parseDouble(customer.get(C_BALANCE)));
            customerDocument.setC_ytd_payment(Double.parseDouble(customer.get(C_YTD_PAYMENT)));
            customerDocument.setC_payment_cnt(Integer.parseInt(customer.get(C_PAYMENT_CNT)));
            customerDocument.setC_delivery_cnt(Integer.parseInt(customer.get(C_DELIVERY_CNT)));

            customerDocument.setC_w_name(warehouses.get(customer.get(C_W_ID)).get(W_NAME));
            customerDocument.setC_d_name(districts.get(new ImmutablePair<String, String>(customer.get(C_W_ID), customer.get(C_D_ID))).get(D_NAME));

            jGenerator.writeObject(customerDocument);
        }
        jGenerator.writeEndArray();

        jGenerator.close();
    }
}
