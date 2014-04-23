/**
 *
 */
package cardiag.obd2;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.serial.PortCommunication;
import cardiag.serial.PortCommunicationException;
import cardiag.serial.PortConfiguration;
import cardiag.serial.SerialUtils;

/**
 * Standard OBD-II functions.
 *
 * @author David Matějček
 */
public class OBD2Standard implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(OBD2Standard.class);

  private final PortCommunication comm;


  /**
   * Starts the port communication with some initial conversation.
   *
   * @param cfg
   * @throws PortCommunicationException
   */
  public OBD2Standard(final PortConfiguration cfg) throws PortCommunicationException {
    LOG.debug("OBD2Standard(cfg={})", cfg);
    this.comm = new PortCommunication(cfg);
    this.reset();
  }


  /**
   * Resets the communication.
   *
   * @throws PortCommunicationException
   */
  public void reset() throws PortCommunicationException {
    LOG.debug("reset()");
    // TODO: why is it here twice???
    this.comm.reset();
    this.comm.reset();
    this.comm.setEcho(false);
    this.comm.setLineTermination(false);
  }


  /**
   * Closes the communication and port.
   */
  public void close() {
    LOG.debug("close()");
    this.comm.close();
  }


  /**
   * @return a {@link Report}
   */
  public Report createReport() {
    LOG.trace("createReport()");
    final Report report = new Report();
    report.setSupportedPIDS(getSupportedPIDs());
    report.setEcuCompatibility(getEcuCompatibility());
    report.setMonitorStatus(getMonitorStatus());
    report.setFaults(getErrorReport());

    report.setDistanceSinceErrorCodesCleared(getDistanceSinceCodesCleared(false));
    report.setDistanceWithMalfunction(getDistanceWithMalfunction(false));

    report.setAmbientAirTemperature(getAmbientAirTemperature(false));
    report.setEngineOilTemperature(getEngineOilTemperature(false));
    report.setEngineCoolantTemperature(getEngineCoolantTemperature(false));
    report.setManifoldSurfaceTemperature(getManifoldSurfaceTemperature(false));

    report.setEngineLoad(getEngineLoad(false));
    report.setExhaustGasRecirculationTemperature(getExhaustGasRecirculationTemperature(false));
    report.setFuelInjectionTiming(getFuelInjectionTiming(false));
    report.setFuelLevelInput(getFuelLevelInput(false));
    report.setFuelRate(getFuelRate(false));
    report.setFuelStatus(getFuelStatus(false));
    report.setIntakeAirTemperature(getIntakeAirTemperature(false));
    report.setIntakeAirTemperatureSensor(getIntakeAirTemperatureSensor(false));
    report.setCatalystTemperatureSensor1(getCatalystTemperature(false, 2, 1));
    report.setCatalystTemperatureSensor2(getCatalystTemperature(false, 2, 2));
    report.setSecondaryAirStatus(getSecondaryAirStatus(false));
    report.setCommandedEgr(getCommandedEgr(false));
    report.setEgrError(getEgrError(false));

    // only bank 1
    report.setFuelTrimPercentShortTerm(getFuelTrimPercent(false, false, 1));
    report.setFuelTrimPercentLongTerm(getFuelTrimPercent(false, true, 1));
    report.setEthanolFuel(getEthanolFuel(false));

    return report;
  }


  /**
   * Sends the request to OBD II unit and returns a multiline response.
   *
   * @param mode
   * @param pid
   * @param hexParams
   * @return a list of responses. Never null, but may be empty.
   */
  protected List<Response> ask(final Mode mode, final PID pid, final String... hexParams) {
    LOG.debug("ask(mode={}, pid={}, hexParams={})", mode, pid, hexParams);
    final String[] params;
    if (hexParams == null) {
      params = new String[2];
    } else {
      params = new String[hexParams.length + 2];
    }
    params[0] = mode.hex();
    params[1] = pid.hex();
    if (hexParams != null) {
      for (int i = 0; i < hexParams.length; i++) {
        params[i + 2] = hexParams[i];
      }
    }
    comm.writeln(params);
    final List<String> lines = comm.readResponse();
    final List<Response> responses = new ArrayList<Response>(lines.size());
    for (final String line : lines) {
      // Ford Focus 1.4
      if ("NO DATA".equals(line)) {
        responses.add(new ResponseWithNoData(mode, pid));
        return responses;
      }
      final String[] vals = line.split(" ");
      final String firstWord = vals[0];
      if (firstWord.length() != 2) {
        throw new PortCommunicationException("Invalid response: " + lines);
      }
      if ("7F".equals(firstWord)) {
        LOG.warn("Error response: " + line);
        final Response response = new Response(true, mode, pid, vals);
        responses.add(response);
        return responses;
      }
      final String[] data;
      final PID responsePID;
      final int dataOffset;
      if (mode == Mode.DIAGNOSTIC || mode == Mode.CLEAR_TROUBLE_CODES) {
        dataOffset = 1;
        responsePID = null;
      } else {
        dataOffset = 2;
        responsePID = PID.parseHex(vals[1], mode);
      }
      if (vals.length > dataOffset) {
        data = Arrays.copyOfRange(vals, dataOffset, vals.length);
      } else {
        data = null;
      }
      final Response response = new Response(false, mode, responsePID, data);
      responses.add(response);
    }
    return responses;
  }


  /**
   * Sends the request to OBD II unit and returns a singleline response.
   *
   * @param mode
   * @param pid
   * @param hexParams
   * @return a response, may be null.
   */
  protected Response askOneLine(final Mode mode, final PID pid, final String... hexParams) {
    LOG.trace("askOneLine(mode={}, pid={}, hexParams={})", mode, pid, hexParams);
    final List<Response> responses = ask(mode, pid, hexParams);
    if (responses.size() > 1) {
      // bug or something weird.
      throw new IllegalStateException("Too many responses for this command: "
          + ReflectionToStringBuilder.toString(responses));
    }
    final Response response = responses.get(0);
    return response;
  }


  private Mode getMode(boolean freezed) {
    return freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA;
  }


  /**
   * @return the VIN code of the car.
   */
  public String getVIN() {
    LOG.debug("getVIN()");
    final Response responseBytes = askOneLine(Mode.VEHICLE_INFO, PID.VIN_COUNT_OF_BYTES);
    LOG.info("Count of VIN bytes: {}", Integer.parseInt(responseBytes.getData()[0]), 16);

    // TODO: Kalina did not returned any.
    final List<Response> response = ask(Mode.VEHICLE_INFO, PID.VIN);
    return response.toString();
  }


  public boolean[] getSupportedPIDs() {
    LOG.debug("getSupportedPIDs()");
    final Response line = askOneLine(Mode.CURRENT_DATA, PID.PIDS_SUPPORTED);
    if (line.isError()) {
      return null;
    }
    return SerialUtils.convertHexToBooleanArray(line.getData());
  }


  public FuelStatus getFuelStatus(final boolean freezed) {
    LOG.debug("getFuelStatus(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.FUEL_STATUS);
    if (line.isError()) {
      return null;
    }
    // TODO: two bytes = two systems
    return FuelStatus.parseHex(line.getData()[0]);
  }


  public EcuCompatibility getEcuCompatibility() {
    LOG.debug("getEcuCompatibility()");
    final Response response = askOneLine(Mode.CURRENT_DATA, PID.ECU_COMPATIBILITY);
    if (response.isError()) {
      return null;
    }
    return EcuCompatibility.parseHex(response.getData()[0]);
  }


  public void clearTroubleCodes() {
    LOG.debug("clearTroubleCodes()");
    askOneLine(Mode.CLEAR_TROUBLE_CODES, PID.CLEAR_TROUBLE_CODES);
  }


  public MonitorStatus getMonitorStatus() {
    LOG.debug("getMonitorStatus()");
    final Response response = askOneLine(Mode.CURRENT_DATA, PID.MONITOR_STATUS);
    if (response.isError()) {
      return null;
    }
    final String[] data = response.getData();
    final boolean[] a = SerialUtils.convertHexToBooleanArray(data[0]);
    final boolean[] b = SerialUtils.convertHexToBooleanArray(data[1]);
    final boolean[] c = SerialUtils.convertHexToBooleanArray(data[2]);
    final boolean[] d = SerialUtils.convertHexToBooleanArray(data[3]);
    final MonitorStatus status = new MonitorStatus();
    status.setMIL(a[0]);
    status.setEmissionRelatedDTCs(SerialUtils.toInteger(Arrays.copyOfRange(a, 1, 8)));
    status.setMissfire(b[7], b[3]);
    status.setFuel(b[6], b[2]);
    status.setComponents(b[5], b[1]);
    status.setIgnition(b[4]);
    status.setReservedBitB7(b[0]);
    // TODO: C and D
    return status;
  }


  public List<Fault> getErrorReport() {
    LOG.debug("getErrorReport()");
    final List<Response> responses = ask(Mode.DIAGNOSTIC, PID.DIAGNOSTIC_CODES);
    if (responses.isEmpty()) {
      return Collections.emptyList();
    }
    if (responses.get(0).isError()) {
      return null;
    }
    final List<Fault> faults = new ArrayList<Fault>();
    if (responses.get(0) instanceof ResponseWithNoData) {
      return faults;
    }
    for (final Response response : responses) {
      final String[] data = response.getData();
      final boolean[] a1 = SerialUtils.convertHexToBooleanArray(data[0]);
      final boolean[] b1 = SerialUtils.convertHexToBooleanArray(data[1]);
      final Fault fault1 = Fault.decode(a1, b1);
      LOG.info("Parsed fault code1: {}", fault1.getCode());
      if (!"P0000".equals(fault1.getCode())) {
        faults.add(fault1);
      }
      final boolean[] a2 = SerialUtils.convertHexToBooleanArray(data[2]);
      final boolean[] b2 = SerialUtils.convertHexToBooleanArray(data[3]);
      final Fault fault2 = Fault.decode(a2, b2);
      LOG.info("Parsed fault code2: {}", fault2.getCode());
      if (!"P0000".equals(fault2.getCode())) {
        faults.add(fault2);
      }
      final boolean[] a3 = SerialUtils.convertHexToBooleanArray(data[4]);
      final boolean[] b3 = SerialUtils.convertHexToBooleanArray(data[5]);
      final Fault fault3 = Fault.decode(a3, b3);
      LOG.info("Parsed fault code3: {}", fault3.getCode());
      if (!"P0000".equals(fault3.getCode())) {
        faults.add(fault3);
      }
    }

    return faults;
  }


  /**
   * @param freezed
   * @return engine load in percents.
   * @throws OBD2Exception
   */
  public Double getEngineLoad(final boolean freezed) {
    LOG.debug("getEngineLoad(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.ENGINE_LOAD);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100.0) / 255.0;
  }


  public Integer getEngineCoolantTemperature(final boolean freezed) {
    LOG.debug("getEngineCoolantTemperature(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.ENGINE_COOLANT_TEMPERATURE);
    if (line.isError()) {
      return null;
    }
    // TODO: three bytes?!
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Integer getEngineOilTemperature(final boolean freezed) {
    LOG.debug("getEngineOilTemperature(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.ENGINE_OIL_TEMPERATURE);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Integer getIntakeAirTemperature(final boolean freezed) {
    LOG.debug("getIntakeAirTemperature(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.INTAKE_AIR_TEMPERATURE);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Integer getIntakeAirTemperatureSensor(final boolean freezed) {
    LOG.debug("getIntakeAirTemperatureSensor(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.INTAKE_AIR_TEMPERATURE_SENSOR);
    if (line.isError()) {
      return null;
    }
    // TODO: what is it?
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    // final int encodedB = Integer.parseInt(line.getData()[0], 16);
    // final int encodedC = Integer.parseInt(line.getData()[0], 16);
    // final int encodedD = Integer.parseInt(line.getData()[0], 16);
    // final int encodedE = Integer.parseInt(line.getData()[0], 16);
    // final int encodedF = Integer.parseInt(line.getData()[0], 16);
    // final int encodedG = Integer.parseInt(line.getData()[0], 16);
    return encodedA - 40;
  }


  public Integer getExhaustGasRecirculationTemperature(final boolean freezed) {
    LOG.debug("getExhaustGasRecirculationTemperature(freezed={})", freezed);

    final Response line = askOneLine(getMode(freezed), PID.EXHAUST_GAS_RECIRCULATION_TEMPERATURE);
    if (line.isError()) {
      return null;
    }
    // TODO: what is it?
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    // final int encodedB = Integer.parseInt(line.getData()[0], 16);
    // final int encodedC = Integer.parseInt(line.getData()[0], 16);
    // final int encodedD = Integer.parseInt(line.getData()[0], 16);
    // final int encodedE = Integer.parseInt(line.getData()[0], 16);
    return encodedA - 40;

  }


  public Integer getAmbientAirTemperature(final boolean freezed) {
    LOG.debug("getAmbientAirTemperature(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.AMBIENT_AIR_TEMPERATURE);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Integer getManifoldSurfaceTemperature(final boolean freezed) {
    LOG.debug("getManifoldSurfaceTemperature(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.MANIFOLD_SURFACE_TEMPERATURE);
    if (line.isError()) {
      return null;
    }
    // TODO: how many bytes?
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Double getFuelTrimPercent(final boolean freezed, final boolean longTerm, final int bank) {
    LOG.debug("getFuelTrimPercent(freezed={}, longTerm={}, bank={})", freezed, longTerm, bank);
    final PID pid;
    if (bank == 1) {
      pid = longTerm ? PID.FUEL_TRIM_PERCENT_LONG_BANK1 : PID.FUEL_TRIM_PERCENT_SHORT_BANK1;
    } else if (bank == 2) {
      pid = longTerm ? PID.FUEL_TRIM_PERCENT_LONG_BANK2 : PID.FUEL_TRIM_PERCENT_SHORT_BANK2;
    } else {
      throw new IllegalArgumentException("Invalid bank: " + bank);
    }
    final Response line = askOneLine(getMode(freezed), pid);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return ((encoded - 128.0) * 100.0) / 128.0;
  }


  public Double getCatalystTemperature(final boolean freezed, final int bank, final int sensor) {
    LOG.debug("getCatalystTemperature(freezed={}, bank={}, sensor={})", freezed, bank, sensor);
    final PID pid;
    if (sensor != 1 && sensor != 2) {
      throw new IllegalArgumentException("Invalid sensor: " + sensor);
    }
    if (bank == 1) {
      if (sensor == 1) {
        pid = PID.CATALYST_TEMPERATURE_BANK1_SENSOR1;
      } else {
        pid = PID.CATALYST_TEMPERATURE_BANK1_SENSOR2;
      }
    } else if (bank == 2) {
      if (sensor == 1) {
        pid = PID.CATALYST_TEMPERATURE_BANK2_SENSOR1;
      } else {
        pid = PID.CATALYST_TEMPERATURE_BANK2_SENSOR2;
      }
    } else {
      throw new IllegalArgumentException("Invalid bank: " + bank);
    }

    final Response response = askOneLine(getMode(freezed), pid);
    if (response.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(response.getData()[0], 16);
    final int encodedB = Integer.parseInt(response.getData()[1], 16);
    return ((encodedA * 256.0 + encodedB) / 10.0) - 40.0;
  }


  public Integer getDistanceWithMalfunction(final boolean freezed) {
    LOG.debug("getDistanceWithMalfunction(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.DISTANCE_WITH_MALFUNCTION);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return encodedA * 256 + encodedB;
  }


  public Integer getDistanceSinceCodesCleared(final boolean freezed) {
    LOG.debug("getDistanceSinceCodesCleared(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.DISTANCE_FROM_CODES_CLEARED);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return encodedA * 256 + encodedB;
  }


  public Double getEthanolFuel(final boolean freezed) {
    LOG.debug("getEthanolFuel(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.ETHANOL_FUEL);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100.0) / 255.0;
  }


  public Double getFuelLevelInput(final boolean freezed) {
    LOG.debug("getFuelLevelInput(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.FUEL_LEVEL_INPUT);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100.0) / 255.0;
  }


  public Double getFuelInjectionTiming(final boolean freezed) {
    LOG.debug("getFuelInjectionTiming(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.FUEL_INJECTION_TIMING);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return (encodedA * 256.0 + encodedB - 26880.0) / 128.0;
  }


  public Double getFuelRate(final boolean freezed) {
    LOG.debug("getFuelRate(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.FUEL_RATE);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return (encodedA * 256.0 + encodedB) * 0.05;
  }


  public AirStatus getSecondaryAirStatus(final boolean freezed) {
    LOG.debug("getSecondaryAirStatus(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.SECONDARY_AIR_STATUS);
    if (line.isError()) {
      return null;
    }
    return AirStatus.parseHex(line.getData()[0]);
  }


  public Double getCommandedEgr(final boolean freezed) {
    LOG.debug("getCommandedEgr(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.COMMANDED_EGR);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100.0) / 255.0;
  }


  public Double getEgrError(final boolean freezed) {
    LOG.debug("getEgrError(freezed={})", freezed);
    final Response line = askOneLine(getMode(freezed), PID.EGR_ERROR);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return ((encoded - 128.0) * 100.0)/128.0;
  }
}
