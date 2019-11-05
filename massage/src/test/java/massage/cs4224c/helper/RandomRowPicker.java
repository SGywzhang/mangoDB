package massage.cs4224c.helper;

import com.google.common.collect.Lists;
import massage.cs4224c.util.ProjectConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// [IMPORTANT] You may not want to execute this class as this class is to generate test data for the App

public class RandomRowPicker {

    private final static Logger logger = LoggerFactory.getLogger(RandomRowPicker.class);

    public static void main(String[] args) throws Exception {
        logger.info("Begin to random pick data");

        ProjectConfig config = ProjectConfig.getInstance();

        logger.info("Begin to random pick data [customer]");
        pickRandom("customer.csv", 0.008);

        // We want all districts
        logger.info("Begin to random pick data [district]");
        pickRandom("district.csv", 1);

        logger.info("Begin to random pick data [item]");
        pickRandom("item.csv", 0.03);

        logger.info("Begin to random pick data [order]");
        pickRandom("order.csv", 0.2);

        logger.info("Begin to random pick data [order-line]");
        pickRandom("order-line.csv", 0.3);

        logger.info("Begin to random pick data [stock]");
        pickRandom("stock.csv", 0.01);

        // We want all warehouses
        logger.info("Begin to random pick data [warehouse]");
        pickRandom("warehouse.csv", 1);

        fixIntegrityConstraint();
    }

    public static void pickRandom(String name, double chanceToPick) throws Exception {
        ProjectConfig config = ProjectConfig.getInstance();
        logger.info("Pick Data [{}] with chance {}", name, chanceToPick);

        Reader districtReader = new FileReader(Paths.get(config.getProjectRoot(), "project-files/data-files", name).toFile());
        Iterable<CSVRecord> csvRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(districtReader);

        FileWriter fileWriter = new FileWriter(Paths.get(config.getProjectRoot(), "test-data/project-files/data-files/temp/", name).toFile());
        CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.INFORMIX_UNLOAD_CSV);

        for (CSVRecord csvRecord : csvRecords) {
            if (RandomUtils.nextDouble(0, 1) < chanceToPick) {
                csvFilePrinter.printRecord(Lists.newArrayList(csvRecord.iterator()));
            }
        }

        fileWriter.flush();
        fileWriter.close();
        csvFilePrinter.close();
    }

    public static void fixIntegrityConstraint() throws Exception {
        logger.info("Begin to fix integrity constraint.");

        String DELIMITER  = ",";

        int INDEX_W_ID = 0;

        int INDEX_D_W_ID = 0;
        int INDEX_D_ID = 1;

        int INDEX_C_W_ID = 0;
        int INDEX_C_D_ID = 1;
        int INDEX_C_ID = 2;

        int INDEX_O_W_ID = 0;
        int INDEX_O_D_ID = 1;
        int INDEX_O_ID = 2;
        int INDEX_O_C_ID = 3;

        int INDEX_I_ID = 0;

        int INDEX_OL_W_ID = 0;
        int INDEX_OL_D_ID = 1;
        int INDEX_OL_O_ID = 2;
        int INDEX_OL_I_ID = 4;

        int INDEX_S_W_ID = 0;
        int INDEX_S_I_ID = 1;

        List<CSVRecord> customer = readTempRecord("customer.csv");
        List<CSVRecord> district = readTempRecord("district.csv");
        List<CSVRecord> item = readTempRecord("item.csv");
        List<CSVRecord> order = readTempRecord("order.csv");
        List<CSVRecord> orderLine = readTempRecord("order-line.csv");
        List<CSVRecord> stock = readTempRecord("stock.csv");
        List<CSVRecord> warehouse = readTempRecord("warehouse.csv");

        logger.info("[warehouse] don't need to care.");

        logger.info("[district] drop all that don't have warehouse id.");
        Set<String> warehouseIdSet = warehouse.stream()
                .map(csvRecord -> csvRecord.get(INDEX_W_ID))
                .collect(Collectors.toCollection(HashSet::new));
        district = district.stream()
                .filter(csvRecord -> warehouseIdSet.contains(csvRecord.get(INDEX_D_W_ID)))
                .collect(Collectors.toList());

        logger.info("[customer] drop all that don't have warehouse id and district id.");
        Set<String> districtSet = district.stream()
                .map(csvRecord -> String.join(DELIMITER, csvRecord.get(INDEX_D_W_ID), csvRecord.get(INDEX_D_ID)))
                .collect(Collectors.toCollection(HashSet::new));
        customer = customer.stream()
                .filter(csvRecord -> districtSet.contains(String.join(DELIMITER, csvRecord.get(INDEX_C_W_ID), csvRecord.get(INDEX_C_D_ID))))
                .collect(Collectors.toList());

        logger.info("[order] drop all that don't have warehouse id and district id and customer id.");
        Set<String> customerSet = customer.stream()
                .map(csvRecord -> String.join(DELIMITER, csvRecord.get(INDEX_C_W_ID), csvRecord.get(INDEX_C_D_ID), csvRecord.get(INDEX_C_ID)))
                .collect(Collectors.toCollection(HashSet::new));
        order = order.stream()
                .filter(csvRecord -> customerSet.contains(String.join(DELIMITER, csvRecord.get(INDEX_O_W_ID), csvRecord.get(INDEX_O_D_ID), csvRecord.get(INDEX_O_C_ID))))
                .collect(Collectors.toList());

        logger.info("[item] don't need to do anything.");
        Set<String> itemIdSet = item.stream()
                .map(csvRecord -> csvRecord.get(INDEX_I_ID))
                .collect(Collectors.toCollection(HashSet::new));

        logger.info("[order-line] drop all that don't have warehouse id and district id and order id.");
        Set<String> orderSet = order.stream()
                .map(csvRecord -> String.join(DELIMITER, csvRecord.get(INDEX_O_W_ID), csvRecord.get(INDEX_O_D_ID), csvRecord.get(INDEX_O_ID)))
                .collect(Collectors.toCollection(HashSet::new));
        orderLine = orderLine.stream()
                .filter(csvRecord -> orderSet.contains(String.join(DELIMITER, csvRecord.get(INDEX_OL_W_ID), csvRecord.get(INDEX_OL_D_ID), csvRecord.get(INDEX_OL_O_ID)))
                        && itemIdSet.contains(csvRecord.get(INDEX_OL_I_ID)))
                .collect(Collectors.toList());

        logger.info("[order-with-order-line] drop all that don't have order id in orderLine.");
        Set<String> orderLineOrderNumber = orderLine.stream()
                .map(csvRecord -> String.join(DELIMITER, csvRecord.get(INDEX_OL_W_ID), csvRecord.get(INDEX_OL_D_ID), csvRecord.get(INDEX_OL_O_ID)))
                .collect(Collectors.toCollection(HashSet::new));
        order = order.stream()
                .filter(csvRecord -> orderLineOrderNumber.contains(String.join(DELIMITER, csvRecord.get(INDEX_O_W_ID), csvRecord.get(INDEX_O_D_ID), csvRecord.get(INDEX_O_ID))))
                .collect(Collectors.toList());

        logger.info("[stock] drop all that don't have warehouse id and item id.");
        stock = stock.stream()
                .filter(csvRecord -> warehouseIdSet.contains(csvRecord.get(INDEX_S_W_ID))
                        && itemIdSet.contains(csvRecord.get(INDEX_S_I_ID)))
                .collect(Collectors.toList());

        writeFinalRecord(customer, "customer.csv");
        writeFinalRecord(district, "district.csv");
        writeFinalRecord(item, "item.csv");
        writeFinalRecord(order, "order.csv");
        writeFinalRecord(orderLine, "order-line.csv");
        writeFinalRecord(stock, "stock.csv");
        writeFinalRecord(warehouse, "warehouse.csv");
    }

    public static List<CSVRecord> readTempRecord(String name) throws IOException {
        logger.info("Read temp record file from {}", name);

        ProjectConfig config = ProjectConfig.getInstance();
        return Lists.newLinkedList(CSVFormat.INFORMIX_UNLOAD_CSV.parse(new FileReader(Paths.get(config.getProjectRoot(), "test-data/project-files/data-files/temp", name).toFile())));
    }

    public static void writeFinalRecord(List<CSVRecord> records, String name) throws IOException {
        logger.info("Write final record file to {}", name);

        ProjectConfig config = ProjectConfig.getInstance();
        FileWriter fileWriter = new FileWriter(Paths.get(config.getProjectRoot(), "test-data/project-files/data-files/", name).toString());
        CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.INFORMIX_UNLOAD_CSV);

        for (CSVRecord csvRecord : records) {
            csvFilePrinter.printRecord(Lists.newArrayList(csvRecord.iterator()));
        }

        fileWriter.flush();
        fileWriter.close();
        csvFilePrinter.close();
    }
}
