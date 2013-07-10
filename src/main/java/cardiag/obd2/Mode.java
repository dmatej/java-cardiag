/**
 *
 */
package cardiag.obd2;

import org.apache.commons.lang.StringUtils;

/**
 * @author David Matějček
 */
public enum Mode {
  CURRENT_DATA(1), FREEZE_FRAME_DATA(2), DIAGNOSTIC(3), CLEAR_TROUBLE_CODES(4), VEHICLE_INFO(9);

  private int code;


  Mode(int code) {
    this.code = code;
  }


  public String hex() {
    return StringUtils.leftPad(Integer.toHexString(code), 2, '0');
  }


  public static Mode parseHex(final String hexMode) {
    if (hexMode == null) {
      throw new IllegalArgumentException("Invalid mode: " + hexMode);
    }
    int code = Integer.parseInt(hexMode, 16);
    for (Mode mode : Mode.values()) {
      if (mode.code == code) {
        return mode;
      }
    }
    throw new IllegalArgumentException("Invalid mode: " + hexMode);
  }

}
