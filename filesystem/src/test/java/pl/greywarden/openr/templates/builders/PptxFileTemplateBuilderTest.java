package pl.greywarden.openr.templates.builders;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class PptxFileTemplateBuilderTest {

    private String testDirectory;

    @Before
    public void setUp() throws Exception {
        testDirectory = SystemUtils.USER_HOME + File.separator + "test-dir";
        Files.createDirectory(new File(testDirectory).toPath());
    }

    @Test
    public void build() throws Exception {
        // given
        AbstractTemplateFileBuilder builder = new PptxFileTemplateBuilder(getTestFileName());
        // when
        builder.build();
        // then
        Assert.assertTrue(new File(getTestFileName() + ".pptx").exists());
    }

    private String getTestFileName() {
        return testDirectory + File.separator + "test";
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(testDirectory));
    }

}