/**
 *
 */
package cardiag.obd2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.serial.PortCommunicationException;
import cardiag.test.TestConfiguration;

/**
 * @author David Matějček
 */
public class OBD2StandardITest {

  private static final Logger LOG = LoggerFactory.getLogger(OBD2StandardITest.class);

  private final TestConfiguration testConfig = new TestConfiguration();
  private OBD2Standard obd2;


  @Before
  public void initCommunication() throws PortCommunicationException {
    this.obd2 = new OBD2Standard(testConfig.getPortConfiguration());
  }


  @Test
  public void testSupportedPIDs() throws PortCommunicationException {
    final boolean[] pids = obd2.getSupportedPIDs();
    LOG.debug("pids={}", pids);
    assertNotNull(pids);
    assertEquals(32, pids.length);
  }


  @Test
  public void testFuelStatus() throws PortCommunicationException {
    assertEquals(FuelStatus.CLOSED_USING_OXYGEN, obd2.getFuelStatus(false));
    // assertEquals(FuelStatus.CLOSED_USING_OXYGEN, obd2.getFuelStatus(true));
  }


  @Test
  public void testGetErrorReport() throws PortCommunicationException {
    obd2.getErrorReport();
  }


  @Test
  public void testGetVIN() throws PortCommunicationException {
    obd2.getVIN();
  }
}
