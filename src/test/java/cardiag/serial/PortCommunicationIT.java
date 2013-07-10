/**
 *
 */
package cardiag.serial;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cardiag.test.TestConfiguration;

/**
 * @author David Matějček
 */
public class PortCommunicationIT {

  private final TestConfiguration testConfig = new TestConfiguration();
  private PortCommunication comm;


  @Before
  public void initCommunication() throws PortCommunicationException {
    this.comm = new PortCommunication(testConfig.getPortConfiguration());
    this.comm.reset();
    this.comm.setEcho(false);
    this.comm.setLineTermination(false);
  }


  @After
  public void close() {
    this.comm.close();
  }


  @Test
  public void testAt() throws PortCommunicationException {
    // FIXME: should be parametrized - other tester can have another hardware.
    assertEquals("ELM327 v1.5", comm.at("I"));
  }
}
