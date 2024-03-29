package builddb.cs4224c;


import com.google.common.collect.Lists;
import builddb.cs4224c.converter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Builddb {

    private final static Logger logger = LoggerFactory.getLogger(Builddb.class);

    public static void main(String[] args) {
        logger.info("Build DB begins.");

        ArrayList<AbstractConverter> abstractConverterArrayList = Lists.newArrayList(
                new CustomerCollection(),
                new DistrictCollection(),
                new OrderItemCollection(),
                new StockCollection()
        );

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        for (AbstractConverter converter : abstractConverterArrayList) {
            logger.info("Submit Converter Task {}", converter.getClass().getSimpleName());
            try {
                executorService.execute(converter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.info("Waiting for tasks to finish. The timeout is infinity, if these tasks hang, you may exit with Ctrl+C");
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("Converter tasks may not be finished, encounter interrupted exception:" + e);
        }


        logger.info("Build DB ends.");
    }
}
