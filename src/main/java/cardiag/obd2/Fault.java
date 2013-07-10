/**
 *
 */
package cardiag.obd2;

import cardiag.serial.SerialUtils;


/**
 * @author David Matějček
 *
 */
public class Fault {

  private final String code;


  /**
   * @param string
   */
  public Fault(final String code) {
    this.code = code;
  }


  public String getCode() {
    return this.code;
  }


  @Override
  public String toString() {
    return getCode();
  }


  /**
   * @param a
   * @param b
   * @return
   */
  public static Fault decode(final boolean[] a, final boolean[] b) {
    final StringBuilder parsed = new StringBuilder();
    final char first;
    if (a[0]) {
      if (a[1]) {
        first = 'U';
      } else {
        first = 'B';
      }
    } else {
      if (a[1]) {
        first = 'C';
      } else {
        first = 'P';
      }
    }
    parsed.append(first);

    final char second;
    if (a[2]) {
      if (a[3]) {
        second = '3';
      } else {
        second = '2';
      }
    } else {
      if (a[3]) {
        second = '1';
      } else {
        second = '0';
      }
    }
    parsed.append(second);

    parsed.append(toHex(a[4], a[5], a[6], a[7]));
    parsed.append(toHex(b[0], b[1], b[2], b[3]));
    parsed.append(toHex(b[4], b[5], b[6], b[7]));

    return new Fault(parsed.toString());
  }


  private static String toHex(boolean... bools) {
    return Integer.toHexString(SerialUtils.toInteger(bools));
  }

}
