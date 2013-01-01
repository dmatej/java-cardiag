/**
 *
 */
package cardiag.serial;

import static org.junit.Assert.*;
import jssc.SerialPortException;

import org.junit.Before;
import org.junit.Test;

import cardiag.test.TestConfiguration;


/**
 * @author David Matějček
 *
 */
public class PortCommunicationIT {

  private final TestConfiguration testConfig = new TestConfiguration();
  private PortCommunication comm;

  @Before
  public void initCommunication() throws SerialPortException, CommunicationException {
    this.comm = new PortCommunication(testConfig.getPortConfiguration());
    this.comm.reset();
    this.comm.setEcho(false);
    this.comm.setLineTermination(false);
  }

  @Test
  public void testAt() throws SerialPortException {
    // FIXME: should be parametrized - other tester can have another hardware.
    assertEquals("ELM327 v1.5", comm.at("I"));
  }

}
