package massage.cs4224c.converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import massage.cs4224c.document.District;
import massage.cs4224c.util.ProjectConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DistrictCollection extends AbstractConverter {

    private static final int D_W_ID = 0;
    private static final int D_ID = 1;
    private static final int D_NAME = 2;
    private static final int D_STREET_1 = 3;
    private static final int D_STREET_2 = 4;
    private static final int D_CITY = 5;
    private static final int D_STATE = 6;
    private static final int D_ZIP = 7;
    private static final int D_TAX = 8;
    private static final int D_YTD = 9;
    private static final int D_NEXT_O_ID = 10;

    private static final int W_ID = 0;
    private static final int W_NAME = 1;
    private static final int W_STREET_1 = 2;
    private static final int W_STREET_2 = 3;
    private static final int W_CITY = 4;
    private static final int W_STATE = 5;
    private static final int W_ZIP = 6;
    private static final int W_TAX = 7;
    private static final int W_YTD = 8;

    private static final int O_W_ID = 0;
    private static final int O_D_ID = 1;
    private static final int O_ID = 2;
    private static final int O_C_ID = 3;
    private static final int O_CARRIER_ID = 4;
    private static final int O_OL_CNT = 5;
    private static final int O_ALL_LOCAL = 6;
    private static final int O_ENTRY_D = 7;

    public static void main(String[] args) throws Exception {
        AbstractConverter districtCollection = new DistrictCollection();
        districtCollection.run();
    }

    @Override
    public void massage() throws Exception {
        ProjectConfig config = ProjectConfig.getInstance();

        Reader warehouseReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "warehouse.csv").toFile());
        Iterable<CSVRecord> warehouseRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(warehouseReader);
        Map<String, CSVRecord> warehouses = new HashMap<String, CSVRecord>();
        for (CSVRecord warehouse : warehouseRecords) {
            warehouses.put(warehouse.get(W_ID), warehouse);
        }

        Reader districtReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "district.csv").toFile());
        Iterable<CSVRecord> districtRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(districtReader);

        Reader orderReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "order.csv").toFile());
        Iterable<CSVRecord> orderRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(orderReader);

        // find the minimum undelivered order
        Map<Pair<String, String>, Integer> minTable = new HashMap<Pair<String, String>, Integer>();
        for (CSVRecord order : orderRecords) {
            Pair<String, String> tuple = new ImmutablePair<>(order.get(O_W_ID), order.get(O_D_ID));
            if (!"null".equalsIgnoreCase(order.get(O_CARRIER_ID))) {
                // we only care about null value
                continue;
            }
            if (!minTable.containsKey(tuple)) {
                minTable.put(tuple, Integer.parseInt(order.get(O_ID)));
                continue;
            }
            Integer oldValue = minTable.get(tuple);
            // bigger than the one in the table
            if (oldValue.toString().compareTo(order.get(O_ID)) > 0) {
                minTable.put(tuple, Integer.parseInt(order.get(O_ID)));
            }
        }

        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator = jfactory.createGenerator(Paths.get(config.getProjectRoot(), config.getDataDestFolder(), "district.json").toFile(), JsonEncoding.UTF8);
        jGenerator.setCodec(new ObjectMapper());

        jGenerator.writeStartArray();
        for (CSVRecord district : districtRecords) {
            District districtDocument = new District();

            districtDocument.set_id(district.get(D_W_ID), district.get(D_ID));

            districtDocument.setD_name(district.get(D_NAME));
            districtDocument.setD_street_1(district.get(D_STREET_1));
            districtDocument.setD_street_2(district.get(D_STREET_2));
            districtDocument.setD_city(district.get(D_CITY));
            districtDocument.setD_state(district.get(D_STATE));
            districtDocument.setD_zip(district.get(D_ZIP));
            districtDocument.setD_tax(Double.parseDouble(district.get(D_TAX)));

            districtDocument.setD_ytd(Double.parseDouble(district.get(D_YTD)));
            districtDocument.setD_next_o_id(Integer.parseInt(district.get(D_NEXT_O_ID)));
            districtDocument.setDt_min_ud_o_id(minTable.getOrDefault(new ImmutablePair<>(district.get(D_W_ID), district.get(D_ID)), null));

            CSVRecord correspondingWarehouse = warehouses.get(district.get(D_W_ID));

            districtDocument.getWarehouse().setW_name(correspondingWarehouse.get(W_NAME));
            districtDocument.getWarehouse().setW_street_1(correspondingWarehouse.get(W_STREET_1));
            districtDocument.getWarehouse().setW_street_2(correspondingWarehouse.get(W_STREET_2));
            districtDocument.getWarehouse().setW_city(correspondingWarehouse.get(W_CITY));
            districtDocument.getWarehouse().setW_state(correspondingWarehouse.get(W_STATE));
            districtDocument.getWarehouse().setW_zip(correspondingWarehouse.get(W_ZIP));
            districtDocument.getWarehouse().setW_tax(Double.parseDouble(correspondingWarehouse.get(W_TAX)));

            jGenerator.writeObject(districtDocument);
        }
        jGenerator.writeEndArray();

        jGenerator.close();
    }
}
