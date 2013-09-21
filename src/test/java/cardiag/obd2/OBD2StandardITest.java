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

import cardiag.test.TestConfiguration;

/**
 * @author David Matějček
 */
public class OBD2StandardITest {

  private static final Logger LOG = LoggerFactory.getLogger(OBD2StandardITest.class);

  private static final TestConfiguration testConfig = new TestConfiguration();
  private static OBD2Standard obd2;


  @BeforeClass
  public static void initCommunication() throws OBD2Exception {
    obd2 = new OBD2Standard(testConfig.getPortConfiguration());
  }


  @AfterClass
  public static void close() {
    if (obd2 != null) {
      obd2.close();
    }
  }


  /**
   * Received response: [41 03 01 00]
   *
   * @throws OBD2Exception
   */
  @Test
  public void testSupportedPIDs() throws OBD2Exception {
    final boolean[] pids = obd2.getSupportedPIDs();
    LOG.debug("pids={}", pids);
    assertNotNull(pids);
    assertEquals(32, pids.length);
  }


  @Test
  public void testFuelStatus() throws OBD2Exception {
    assertEquals(FuelStatus.CLOSED_USING_OXYGEN, obd2.getFuelStatus(false));
    // assertEquals(FuelStatus.CLOSED_USING_OXYGEN, obd2.getFuelStatus(true));
  }


  @Test
  public void testGetErrorReport() throws OBD2Exception {
    LOG.info("Error report: {}", obd2.getErrorReport());
  }


  // @Test
  // public void testClearTroubleCodes() throws OBD2Exception {
  // obd2.clearTroubleCodes();
  // LOG.warn("Trouble codes cleared!");
  // }

  /**
   * [49 02 01 00 00 00 FF, 49 02 02 FF FF FF FF, 49 02 03 FF FF FF FF, 49 02 04 FF FF FF FF, 49 02
   * 05 FF FF FF FF]
   *
   * @throws OBD2Exception
   */
  @Test
  public void testGetVIN() throws OBD2Exception {
    LOG.info("VIN={}", obd2.getVIN());
  }


  /**
   * [41 04 00] = 0.0%
   *
   * @throws OBD2Exception
   */
  @Test
  public void testGetEngineLoad() throws OBD2Exception {
    LOG.info("engine load: {}%", obd2.getEngineLoad(false));
    // obd2.getEngineLoad(true);
  }


  /**
   * [41 05 37] = 15°C
   *
   * @throws OBD2Exception
   */
  @Test
  public void testGetEngineCoolantTemp() throws OBD2Exception {
    LOG.info("engine coolant temperature: {}°C", obd2.getEngineCoolantTemperature(false));
    // obd2.getEngineCoolantTemperature(true);
  }


  /**
   * [41 0F 36] = 14°C
   *
   * @throws OBD2Exception
   */
  @Test
  public void testGetIntakeAirTemp() throws OBD2Exception {
    LOG.info("intake air temperature: {}°C", obd2.getIntakeAirTemperature(false));
  }


  /**
   * [41 06 80] = 0.0%
   * [41 07 80] = 0.0%
   *
   * @throws OBD2Exception
   */
  @Test
  public void testGetFuelTrimPercent() throws OBD2Exception {
    LOG.info("fuel trim bank1, short term: {}%", obd2.getFuelTrimPercent(false, false, 1));
    LOG.info("fuel trim bank1, long term: {}%", obd2.getFuelTrimPercent(false, true, 1));
    // LOG.info("fuel trim bank2, short term: {}%", obd2.getFuelTrimPercent(false, false, 2));
    // LOG.info("fuel trim bank2, long term: {}%", obd2.getFuelTrimPercent(false, true, 2));
  }


  /**
   * [41 21 00 00]
   *
   * @throws OBD2Exception
   */
  @Test
  public void testGetDistanceWithMalfunction() throws OBD2Exception {
    LOG.info("distance with malfunction: {}", obd2.getDistanceWithMalfunction(false));
  }


  @Test
  @Ignore("For Kalina causes error response 7F 01 12")
  public void testGetDistanceSinceCodesCleared() throws OBD2Exception {
    LOG.info("distance since codes cleared: {}", obd2.getDistanceSinceCodesCleared(false));
  }


  @Test
  @Ignore("For Kalina causes error response 7F 01 12")
  public void testGetFuelLevelInput() throws OBD2Exception {
    LOG.info("fuel level input: {}%", obd2.getFuelLevelInput(false));
  }


  @Test
  @Ignore("For Kalina causes error response 7F 01 12")
  public void testGetFuelInjectionTiming() throws OBD2Exception {
    LOG.info("fuel injection timing: {}°", obd2.getFuelInjectionTiming(false));
  }


  @Test
  // @Ignore("For Kalina causes error response 7F 01 12")
  public void testGetFuelRate() throws OBD2Exception {
    LOG.info("fuel rate: {} L/h", obd2.getFuelRate(false));
  }


  @Test
  /**
   * [41 01 03 07 65 00]
   * [41 01 00 07 65 65]
   * @throws OBD2Exception
   */
  public void testGetMonitorStatus() throws OBD2Exception {
    LOG.info("monitor status: {}", obd2.getMonitorStatus());
  }
}
