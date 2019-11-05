package massage.cs4224c.converter;


import massage.cs4224c.util.Constant;
import massage.cs4224c.util.ProjectConfig;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public abstract class AbstractConverterTest {

    private final static Logger logger = LoggerFactory.getLogger(ProjectConfig.class);

    private boolean isGodMod;

    private AbstractConverter abstractConverter;
    private ProjectConfig config;

    private String fileName;

    protected AbstractConverterTest(AbstractConverter abstractConverter, String fileName) {
        this.abstractConverter = abstractConverter;
        this.fileName = fileName;
    }

    protected void setGodMod(boolean isGodMod) {
        this.isGodMod = isGodMod;
    }

    @Before
    public void before() {
        System.setProperty(Constant.PROPERTY_KEY_ENV, Constant.ENV_TEST);
        config = ProjectConfig.getInstance().reload();
    }

    public void testConverter() throws Exception {
        logger.info("Is God Mod enabled : {}", isGodMod);

        logger.info("Running {} Task", abstractConverter.getClass().getSimpleName());
        abstractConverter.run();

        Path actualFilePath = Paths.get(config.getProjectRoot(), config.getDataDestFolder(), fileName);
        Path expectedFilePath = Paths.get(config.getProjectRoot(), "test-data/expected-database-data", fileName);
        if (isGodMod) {

            logger.info("Copy file from {} to {} and REPLACE_EXISTING", actualFilePath.toString(), expectedFilePath.toString());
            Files.copy(actualFilePath, expectedFilePath, REPLACE_EXISTING);
            logger.info("Updated the expected file {}", abstractConverter.getClass().getSimpleName());
            return;
        }
        logger.info("Expected file path is {}, actual file path is {}", expectedFilePath.toString(), actualFilePath.toString());
        
        String expected = IOUtils.toString(new FileReader(expectedFilePath.toFile()));
        String actual = IOUtils.toString(new FileReader(actualFilePath.toFile()));

        Assert.assertEquals("The converter produce different data, if you are confident, use God Mod", expected, actual);
    }
}
