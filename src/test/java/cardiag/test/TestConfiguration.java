/**
 *
 */
package cardiag.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import cardiag.serial.PortConfiguration;


/**
 * @author David Matějček
 */
public class TestConfiguration {

  private Properties properties;


  public TestConfiguration() {
    final InputStream stream = TestConfiguration.class.getClassLoader().getResourceAsStream("test.properties");
    try {
      Properties properties = new Properties();
      properties.load(stream);
      this.properties = properties;
    } catch (IOException e) {
      Assert.fail("Cannot load test.properties file");
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }


  public String getPortName() {
    return properties.getProperty("serialPort");
  }


  public PortConfiguration getPortConfiguration() {
    final PortConfiguration cfg = new PortConfiguration();
    cfg.setPortName(getPortName());
    cfg.setCommandTimeout(10000L);
    return cfg;
  }

}
