package massage.cs4224c.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConverter implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(AbstractConverter.class);


    public abstract void massage() throws Exception;

    @Override
    public void run() {
        logger.info("{} is running!", this.getClass().getSimpleName());

        try {
            this.massage();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(String.format("[%s] cs4224c.Massage task ends with exception %s", this.getClass().getSimpleName(), e));
            return;
        }

        logger.info("{} Task finish without error! Minions are happy!", this.getClass().getSimpleName());
    }
}
