/**
 *
 */
package cardiag.obd2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

  private static final TestConfiguration testConfig = new TestConfiguration();
  private static OBD2Standard obd2;


  @BeforeClass
  public static void initCommunication() throws PortCommunicationException {
    obd2 = new OBD2Standard(testConfig.getPortConfiguration());
  }


  @AfterClass
  public static void close() {
    obd2.close();
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
    LOG.info("Error report: {}", obd2.getErrorReport());
  }


  // @Test
  // public void testClearTroubleCodes() throws PortCommunicationException {
  // obd2.clearTroubleCodes();
  // LOG.warn("Trouble codes cleared!");
  // }

  @Test
  public void testGetVIN() throws PortCommunicationException {
    LOG.info("VIN={}", obd2.getVIN());
  }


  @Test
  public void testGetEngineLoad() throws PortCommunicationException {
    LOG.info("engine load: {}%", obd2.getEngineLoad(false));
    // obd2.getEngineLoad(true);
  }


  @Test
  public void testGetEngineCoolantTemp() throws PortCommunicationException {
    LOG.info("engine coolant temperature: {}°C", obd2.getEngineCoolantTemperature(false));
    // obd2.getEngineCoolantTemperature(true);
  }


  @Test
  public void testGetIntakeAirTemp() throws PortCommunicationException {
    LOG.info("intake air temperature: {}°C", obd2.getIntakeAirTemperature(false));
  }


  @Test
  public void testGetFuelTrimPercent() throws PortCommunicationException {
    LOG.info("fuel trim bank1, short term: {}%", obd2.getFuelTrimPercent(false, false, 1));
    LOG.info("fuel trim bank1, long term: {}%", obd2.getFuelTrimPercent(false, true, 1));
    // LOG.info("fuel trim bank2, short term: {}%", obd2.getFuelTrimPercent(false, false, 2));
    // LOG.info("fuel trim bank2, long term: {}%", obd2.getFuelTrimPercent(false, true, 2));
  }


  @Test
  public void testGetDistanceWithMalfunction() throws PortCommunicationException {
    LOG.info("distance with malfunction: {}", obd2.getDistanceWithMalfunction(false));
  }


  @Test
  @Ignore("For Kalina causes error response 7F 01 12")
  public void testGetDistanceSinceCodesCleared() throws PortCommunicationException {
    LOG.info("distance since codes cleared: {}", obd2.getDistanceSinceCodesCleared(false));
  }


  @Test
  @Ignore("For Kalina causes error response 7F 01 12")
  public void testGetFuelLevelInput() throws PortCommunicationException {
    LOG.info("fuel level input: {}", obd2.getFuelLevelInput(false));
  }


  @Test
  // [41 01 00 07 65 65]
  public void testGetMonitorStatus() throws PortCommunicationException {
    LOG.info("monitor status: {}", obd2.getMonitorStatus());
  }
}
