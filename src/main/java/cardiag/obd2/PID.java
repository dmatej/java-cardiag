/**
 *
 */
package cardiag.obd2;

import static cardiag.obd2.Mode.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Matějček
 */
public class PID {

  private static final Logger LOG = LoggerFactory.getLogger(PID.class);

  public static final PID DIAGNOSTIC_CODES = new PID(0, DIAGNOSTIC);
  public static final PID CLEAR_TROUBLE_CODES = new PID(0, Mode.CLEAR_TROUBLE_CODES);
  public static final PID VIN_COUNT_OF_BYTES = new PID(1, VEHICLE_INFO);
  public static final PID VIN = new PID(2, VEHICLE_INFO);

  public static final PID PIDS_SUPPORTED = new PID(0, CURRENT_DATA);
  public static final PID MONITOR_STATUS = new PID(1, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_STATUS = new PID(3, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID ENGINE_LOAD = new PID(4, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID ENGINE_COOLANT_TEMPERATURE = new PID(5, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_TRIM_PERCENT_SHORT_BANK1 = new PID(6, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_TRIM_PERCENT_LONG_BANK1 = new PID(7, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_TRIM_PERCENT_SHORT_BANK2 = new PID(8, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_TRIM_PERCENT_LONG_BANK2 = new PID(9, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID INTAKE_AIR_TEMPERATURE = new PID(0x0F, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID INTAKE_AIR_TEMPERATURE_SENSOR = new PID(0x68, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID SECONDARY_AIR_STATUS = new PID(0x12, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID ECU_COMPATIBILITY = new PID(0x1c, CURRENT_DATA);

  public static final PID DISTANCE_WITH_MALFUNCTION = new PID(0x21, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID COMMANDED_EGR = new PID(0x2c, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID EGR_ERROR = new PID(0x2d, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_LEVEL_INPUT = new PID(0x2F, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID DISTANCE_FROM_CODES_CLEARED = new PID(0x31, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID CATALYST_TEMPERATURE_BANK1_SENSOR1 = new PID(0x3c, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID CATALYST_TEMPERATURE_BANK2_SENSOR1 = new PID(0x3d, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID CATALYST_TEMPERATURE_BANK1_SENSOR2 = new PID(0x3e, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID CATALYST_TEMPERATURE_BANK2_SENSOR2 = new PID(0x3f, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID AMBIENT_AIR_TEMPERATURE = new PID(0x46, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID ETHANOL_FUEL = new PID(0x51, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID ENGINE_OIL_TEMPERATURE = new PID(0x5c, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_INJECTION_TIMING = new PID(0x5d, FREEZE_FRAME_DATA, CURRENT_DATA);
  public static final PID FUEL_RATE = new PID(0x5e, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID EXHAUST_GAS_RECIRCULATION_TEMPERATURE = new PID(0x6b, FREEZE_FRAME_DATA, CURRENT_DATA);

  public static final PID MANIFOLD_SURFACE_TEMPERATURE = new PID(0x84, FREEZE_FRAME_DATA, CURRENT_DATA);


  private final Mode[] allowedModes;
  private final int code;


  protected PID(final int pidCode, final Mode... modesAllowed) {
    this.code = pidCode;
    this.allowedModes = modesAllowed;
  }


  public String hex() {
    return StringUtils.leftPad(Integer.toHexString(code), 2, '0');
  }


  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }


  // the problem is that some pids have very different meaning in different modes.
  public static PID parseHex(final String hex, final Mode mode) {
    LOG.debug("parseHex(hex={}, mode={})", hex, mode);
    if (hex == null) {
      throw new IllegalArgumentException("Invalid PID: " + hex);
    }
    final int code = Integer.parseInt(hex, 16);
    switch (code) {
      case 0:
        switch (mode) {
          case CURRENT_DATA:
            return valid(mode, PIDS_SUPPORTED);
          case DIAGNOSTIC:
            return valid(mode, DIAGNOSTIC_CODES);
          case CLEAR_TROUBLE_CODES:
            return valid(mode, CLEAR_TROUBLE_CODES);
          default:
            break;
        }
      case 1:
        switch (mode) {
          case VEHICLE_INFO:
            return valid(mode, VIN_COUNT_OF_BYTES);
          default:
            return valid(mode, MONITOR_STATUS);
        }
      case 2:
        return valid(mode, VIN);
      case 3:
        return valid(mode, FUEL_STATUS);
      case 4:
        return valid(mode, ENGINE_LOAD);
      case 5:
        return valid(mode, ENGINE_COOLANT_TEMPERATURE);
      case 6:
        return valid(mode, FUEL_TRIM_PERCENT_SHORT_BANK1);
      case 7:
        return valid(mode, FUEL_TRIM_PERCENT_LONG_BANK2);
      case 8:
        return valid(mode, FUEL_TRIM_PERCENT_SHORT_BANK1);
      case 9:
        return valid(mode, FUEL_TRIM_PERCENT_LONG_BANK2);
      case 0x0F:
        return valid(mode, INTAKE_AIR_TEMPERATURE);
      case 0x12:
        return valid(mode, SECONDARY_AIR_STATUS);
      case 0x1C:
        return valid(mode, ECU_COMPATIBILITY);
      case 0x21:
        return valid(mode, DISTANCE_WITH_MALFUNCTION);
      case 0x2c:
        return valid(mode, COMMANDED_EGR);
      case 0x2d:
        return valid(mode, EGR_ERROR);
      case 0x2F:
        return valid(mode, FUEL_LEVEL_INPUT);
      case 0x31:
        return valid(mode, DISTANCE_FROM_CODES_CLEARED);
      case 0x3c:
        return valid(mode, CATALYST_TEMPERATURE_BANK1_SENSOR1);
      case 0x3d:
        return valid(mode, CATALYST_TEMPERATURE_BANK2_SENSOR1);
      case 0x3e:
        return valid(mode, CATALYST_TEMPERATURE_BANK1_SENSOR2);
      case 0x3f:
        return valid(mode, CATALYST_TEMPERATURE_BANK2_SENSOR2);
      case 0x46:
        return valid(mode, AMBIENT_AIR_TEMPERATURE);
      case 0x51:
        return valid(mode, ETHANOL_FUEL);
      case 0x5c:
        return valid(mode, ENGINE_OIL_TEMPERATURE);
      case 0x5d:
        return valid(mode, FUEL_INJECTION_TIMING);
      case 0x5e:
        return valid(mode, FUEL_RATE);
      case 0x68:
        return valid(mode, INTAKE_AIR_TEMPERATURE_SENSOR);
      case 0x6b:
        return valid(mode, EXHAUST_GAS_RECIRCULATION_TEMPERATURE);
      case 0x84:
        return valid(mode, MANIFOLD_SURFACE_TEMPERATURE);
      default:
        throw new IllegalArgumentException("Invalid PID: " + hex);
    }
  }


  /**
   * @param mode
   * @param pidsSupported
   * @return
   */
  private static PID valid(Mode mode, PID pid) {
    for (Mode allowed : pid.allowedModes) {
      if (allowed == mode) {
        return pid;
      }
    }
    throw new IllegalArgumentException(String.format("The pid %s is not allowed to run in mode %s.", pid, mode));
  }
}
