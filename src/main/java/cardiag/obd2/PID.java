/**
 *
 */
package cardiag.obd2;

import static cardiag.obd2.Mode.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Matějček
 */
public class PID {

  private static final Logger LOG = LoggerFactory.getLogger(PID.class);

  public static final PID PIDS_SUPPORTED = new PID(0, CURRENT_DATA);
  public static final PID DIAGNOSTIC_CODES = new PID(0, DIAGNOSTIC);
  public static final PID VIN = new PID(2, VEHICLE_INFO);
  public static final PID FUEL_STATUS = new PID(3, FREEZE_FRAME_DATA, CURRENT_DATA);


  private final Mode[] allowedModes;
  private final int code;



  protected PID(final int pidCode, final Mode... modesAllowed) {
    this.code = pidCode;
    this.allowedModes = modesAllowed;
  }


  public String hex() {
    return StringUtils.leftPad(Integer.toHexString(code), 2, '0');
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
        return valid(mode, PIDS_SUPPORTED);
      case 2:
        return valid(mode, VIN);
      case 3:
        return valid(mode, FUEL_STATUS);
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
