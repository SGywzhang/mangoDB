package massage.cs4224c.converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import massage.cs4224c.document.Stock;
import massage.cs4224c.util.ProjectConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StockCollection extends AbstractConverter {
    private static final int S_W_ID = 0;
    private static final int S_I_ID = 1;
    private static final int S_QUANTITY = 2;
    private static final int S_YTD = 3;
    private static final int S_ORDER_CNT = 4;
    private static final int S_REMOTE_CNT = 5;
    private static final int S_DIST_01 = 6;
    private static final int S_DIST_02 = 7;
    private static final int S_DIST_03 = 8;
    private static final int S_DIST_04 = 9;
    private static final int S_DIST_05 = 10;
    private static final int S_DIST_06 = 11;
    private static final int S_DIST_07 = 12;
    private static final int S_DIST_08 = 13;
    private static final int S_DIST_09 = 14;
    private static final int S_DIST_10 = 15;
    private static final int S_DATA = 16;

    private static final int I_ID = 0;
    private static final int I_NAME = 1;
    private static final int I_PRICE = 2;
    private static final int I_IM_ID = 3;
    private static final int I_DATA = 4;

    public static void main(String[] args) {
        AbstractConverter stockItem = new StockCollection();
        stockItem.run();
    }

    @Override
    public void massage() throws Exception {
        ProjectConfig config = ProjectConfig.getInstance();

        Reader stockReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "stock.csv").toFile());
        Iterable<CSVRecord> stockRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(stockReader);

        Reader itemReader = new FileReader(Paths.get(config.getProjectRoot(), config.getDataSourceFolder(), "data-files", "item.csv").toFile());
        Iterable<CSVRecord> itemRecords = CSVFormat.INFORMIX_UNLOAD_CSV.parse(itemReader);
        Map<String, CSVRecord> items = new HashMap<String, CSVRecord>();
        for (CSVRecord item : itemRecords) {
            items.put(item.get(I_ID), item);
        }

        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator = jfactory.createGenerator(Paths.get(config.getProjectRoot(), config.getDataDestFolder(), "stock.json").toFile(), JsonEncoding.UTF8);
        jGenerator.setCodec(new ObjectMapper());

        jGenerator.writeStartArray();
        for (CSVRecord stock : stockRecords) {
            Stock stockDocument = new Stock();
            CSVRecord currItem = items.get(stock.get(S_I_ID));

            stockDocument.set_id(stock.get(S_W_ID), stock.get(S_I_ID));

            stockDocument.setS_dist_01(stock.get(S_DIST_01));
            stockDocument.setS_dist_02(stock.get(S_DIST_02));
            stockDocument.setS_dist_03(stock.get(S_DIST_03));
            stockDocument.setS_dist_04(stock.get(S_DIST_04));
            stockDocument.setS_dist_05(stock.get(S_DIST_05));
            stockDocument.setS_dist_06(stock.get(S_DIST_06));
            stockDocument.setS_dist_07(stock.get(S_DIST_07));
            stockDocument.setS_dist_08(stock.get(S_DIST_08));
            stockDocument.setS_dist_09(stock.get(S_DIST_09));
            stockDocument.setS_dist_10(stock.get(S_DIST_10));
            stockDocument.setS_data(stock.get(S_DATA));

            stockDocument.setS_quantity(Integer.parseInt(stock.get(S_QUANTITY)));
            stockDocument.setS_ytd(Double.parseDouble(stock.get(S_YTD)));
            stockDocument.setS_order_cnt(Integer.parseInt(stock.get(S_ORDER_CNT)));
            stockDocument.setS_remote_cnt(Integer.parseInt(stock.get(S_REMOTE_CNT)));

            stockDocument.getItem().setI_name(currItem.get(I_NAME));
            stockDocument.getItem().setI_price(Double.parseDouble(currItem.get(I_PRICE)));
            stockDocument.getItem().setI_im_id(Integer.parseInt(currItem.get(I_IM_ID)));
            stockDocument.getItem().setI_data(currItem.get(I_DATA));

            jGenerator.writeObject(stockDocument);
        }
        jGenerator.writeEndArray();

        jGenerator.close();
    }
}
