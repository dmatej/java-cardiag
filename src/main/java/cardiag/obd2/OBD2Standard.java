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
    this.comm.close();
  }


  /**
   * @return a {@link Report}
   * @throws OBD2Exception
   */
  public Report createReport() throws OBD2Exception {
    final Report report = new Report();
    report.setSupportedPIDS(getSupportedPIDs());
    report.setMonitorStatus(getMonitorStatus());
    report.setFaults(getErrorReport());

    report.setDistanceSinceErrorCodesCleared(getDistanceSinceCodesCleared(false));
    report.setDistanceWithMalfunction(getDistanceWithMalfunction(false));

    report.setEngineCoolantTemperature(getEngineCoolantTemperature(false));
    report.setEngineLoad(getEngineLoad(false));
    report.setFuelInjectionTiming(getFuelInjectionTiming(false));
    report.setFuelLevelInput(getFuelLevelInput(false));
    report.setFuelRate(getFuelRate(false));
    report.setFuelStatus(getFuelStatus(false));
    report.setFuelTrimPercentShortTerm(getFuelTrimPercent(false, false, 0));
    report.setFuelTrimPercentLongTerm(getFuelTrimPercent(false, true, 0));
    report.setIntakeAirTemperature(getIntakeAirTemperature(false));

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
      final String[] vals = line.split(" ");
      if ("7F".equals(vals[0])) {
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
    if (responses.isEmpty()) {
      return null;
    }
    if (responses.size() > 1) {
      // bug or something weird.
      throw new IllegalStateException("Too many responses for this command: "
          + ReflectionToStringBuilder.toString(responses));
    }
    final Response response = responses.get(0);
    return response;
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


  public boolean[] getSupportedPIDs() throws OBD2Exception {
    final Response line = askOneLine(Mode.CURRENT_DATA, PID.PIDS_SUPPORTED);
    if (line.isError()) {
      return null;
    }
    return SerialUtils.convertHexToBooleanArray(line.getData());
  }


  public FuelStatus getFuelStatus(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.FUEL_STATUS);
    if (line.isError()) {
      return null;
    }
    return FuelStatus.parseHex(line.getData()[0]);
  }


  public void clearTroubleCodes() throws OBD2Exception {
    askOneLine(Mode.CLEAR_TROUBLE_CODES, PID.CLEAR_TROUBLE_CODES);
  }


  public MonitorStatus getMonitorStatus() throws OBD2Exception {
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


  public List<Fault> getErrorReport() throws OBD2Exception {
    final List<Response> responses = ask(Mode.DIAGNOSTIC, PID.DIAGNOSTIC_CODES);
    if (responses.isEmpty()) {
      return Collections.emptyList();
    }
    if (responses.get(0).isError()) {
      return null;
    }
    final List<Fault> faults = new ArrayList<Fault>();
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


  public Double getEngineLoad(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.ENGINE_LOAD);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100.0) / 255.0;
  }


  public Integer getEngineCoolantTemperature(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.ENGINE_COOLANT_TEMP);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Integer getIntakeAirTemperature(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.AIR_TEMP_INTAKE);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public Double getFuelTrimPercent(final boolean freezed, final boolean longTerm, final int bank) throws OBD2Exception {
    final PID pid;
    if (bank == 1) {
      pid = longTerm ? PID.FUEL_TRIM_PERCENT_LONG_BANK1 : PID.FUEL_TRIM_PERCENT_SHORT_BANK1;
    } else if (bank == 2) {
      pid = longTerm ? PID.FUEL_TRIM_PERCENT_LONG_BANK2 : PID.FUEL_TRIM_PERCENT_SHORT_BANK2;
    } else {
      throw new IllegalArgumentException("Invalid bank: " + bank);
    }
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, pid);
    if (line.isError()) {
      return null;
    }
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return ((encoded - 128.0) * 100.0) / 128.0;
  }


  public Integer getDistanceWithMalfunction(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA,
        PID.DISTANCE_WITH_MALFUNCTION);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return encodedA * 256 + encodedB;
  }


  public Integer getDistanceSinceCodesCleared(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA,
        PID.DISTANCE_FROM_CODES_CLEARED);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return encodedA * 256 + encodedB;
  }


  public Double getFuelLevelInput(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.FUEL_LEVEL_INPUT);
    final int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100.0) / 255.0;
  }


  public Double getFuelInjectionTiming(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.FUEL_INJECTION_TIMING);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return (encodedA * 256.0 + encodedB - 26880.0) / 128.0;
  }


  public Double getFuelRate(final boolean freezed) throws OBD2Exception {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.FUEL_RATE);
    if (line.isError()) {
      return null;
    }
    final int encodedA = Integer.parseInt(line.getData()[0], 16);
    final int encodedB = Integer.parseInt(line.getData()[1], 16);
    return (encodedA * 256.0 + encodedB) * 0.05;
  }
}
