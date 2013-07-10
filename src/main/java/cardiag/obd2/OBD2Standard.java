/**
 *
 */
package cardiag.obd2;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.serial.PortCommunication;
import cardiag.serial.PortCommunicationException;
import cardiag.serial.PortConfiguration;
import cardiag.serial.SerialUtils;

/**
 * @author David Matějček
 */
public class OBD2Standard implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(OBD2Standard.class);

  private final PortCommunication comm;


  public OBD2Standard(final PortConfiguration cfg) throws PortCommunicationException {
    this.comm = new PortCommunication(cfg);
    this.reset();
  }


  public void reset() throws PortCommunicationException {
    this.comm.reset();
    this.comm.reset();
    this.comm.setEcho(false);
    this.comm.setLineTermination(false);
  }


  public void close() {
    this.comm.close();
  }


  public List<Response> ask(final Mode mode, final PID pid, final String... hexParams)
    throws PortCommunicationException {
    LOG.info("ask(mode={}, pid={}, hexParams={})", mode, pid, hexParams);
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
        throw new PortCommunicationException("Error response: " + line);
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
      final Response response = new Response(mode, responsePID, data);
      responses.add(response);
    }
    return responses;
  }


  public Response askOneLine(final Mode mode, final PID pid, final String... hexParams)
    throws PortCommunicationException {
    LOG.trace("askOneLine(mode={}, pid={}, hexParams={})", mode, pid, hexParams);
    final List<Response> responses = ask(mode, pid, hexParams);
    if (responses.isEmpty()) {
      return null;
    }
    if (responses.size() > 1) {
      throw new PortCommunicationException("Too many responses for this command.");
    }
    final Response response = responses.get(0);
    return response;
  }


  public String getVIN() throws PortCommunicationException {
    final List<Response> response = ask(Mode.VEHICLE_INFO, PID.VIN);
    return response.toString();
  }


  public boolean[] getSupportedPIDs() throws PortCommunicationException {
    final Response line = askOneLine(Mode.CURRENT_DATA, PID.PIDS_SUPPORTED);
    return SerialUtils.convertHexToBooleanArray(line.getData());
  }


  public FuelStatus getFuelStatus(final boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.FUEL_STATUS);
    return FuelStatus.parseHex(line.getData()[0]);
  }


  public void clearTroubleCodes() throws PortCommunicationException {
    askOneLine(Mode.CLEAR_TROUBLE_CODES, PID.CLEAR_TROUBLE_CODES);
  }


  public MonitorStatus getMonitorStatus() throws PortCommunicationException {
    Response response = askOneLine(Mode.CURRENT_DATA, PID.MONITOR_STATUS);
    String[] data = response.getData();
    boolean[] a = SerialUtils.convertHexToBooleanArray(data[0]);
    boolean[] b = SerialUtils.convertHexToBooleanArray(data[1]);
    boolean[] c = SerialUtils.convertHexToBooleanArray(data[2]);
    boolean[] d = SerialUtils.convertHexToBooleanArray(data[3]);
    MonitorStatus status = new MonitorStatus();
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


  public List<Fault> getErrorReport() throws PortCommunicationException {
    final List<Response> responses = ask(Mode.DIAGNOSTIC, PID.DIAGNOSTIC_CODES);
    if (responses.isEmpty()) {
      return Collections.emptyList();
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


  public double getEngineLoad(boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.ENGINE_LOAD);
    int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100) / 255;
  }


  public int getEngineCoolantTemperature(boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.ENGINE_COOLANT_TEMP);
    int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public int getIntakeAirTemperature(boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.AIR_TEMP_INTAKE);
    int encoded = Integer.parseInt(line.getData()[0], 16);
    return encoded - 40;
  }


  public double getFuelTrimPercent(boolean freezed, boolean longTerm, int bank) throws PortCommunicationException {
    final PID pid;
    if (bank == 1) {
      pid = longTerm ? PID.FUEL_TRIM_PERCENT_LONG_BANK1 : PID.FUEL_TRIM_PERCENT_SHORT_BANK1;
    } else if (bank == 2) {
      pid = longTerm ? PID.FUEL_TRIM_PERCENT_LONG_BANK2 : PID.FUEL_TRIM_PERCENT_SHORT_BANK2;
    } else {
      throw new IllegalArgumentException("Invalid bank: " + bank);
    }
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, pid);
    int encoded = Integer.parseInt(line.getData()[0], 16);
    return ((encoded - 128) * 100) / 128;
  }

  public int getDistanceWithMalfunction(boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.DISTANCE_WITH_MALFUNCTION);
    int encodedA = Integer.parseInt(line.getData()[0], 16);
    int encodedB = Integer.parseInt(line.getData()[1], 16);
    return encodedA * 256 + encodedB;
  }


  public int getDistanceSinceCodesCleared(boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.DISTANCE_FROM_CODES_CLEARED);
    int encodedA = Integer.parseInt(line.getData()[0], 16);
    int encodedB = Integer.parseInt(line.getData()[1], 16);
    return encodedA * 256 + encodedB;
  }


  public double getFuelLevelInput(boolean freezed) throws PortCommunicationException {
    final Response line = askOneLine(freezed ? Mode.FREEZE_FRAME_DATA : Mode.CURRENT_DATA, PID.FUEL_LEVEL_INPUT);
    int encoded = Integer.parseInt(line.getData()[0], 16);
    return (encoded * 100) / 255;
  }

}
