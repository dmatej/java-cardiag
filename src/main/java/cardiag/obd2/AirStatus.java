/**
 *
 */
package cardiag.obd2;

/**
 * @author David Matějček
 */
public enum AirStatus {
  /***/
  UPSTREAM(0x00, "Upstream of catalytic converter"),
  /***/
  DOWNSTREAM(0x01, "Downstream of catalytic converter"),
  /***/
  OFF_OR_OUTSIDE(0x02, "From the outside atmosphere or off");

  final int code;
  final String description;


  AirStatus(int value, String description) {
    this.code = value;
    this.description = description;
  }


  /**
   * @return the code.
   */
  public int getCode() {
    return code;
  }


  /**
   * @return the description.
   */
  public String getDescription() {
    return description;
  }


  /**
   * @return code and description
   */
  @Override
  public String toString() {
    return this.description;
  }


  public static AirStatus parseHex(final String hex) {
    if (hex == null) {
      throw new IllegalArgumentException("Invalid hex code: " + hex);
    }

    int code1 = Integer.parseInt(hex, 16);
    for (AirStatus status : AirStatus.values()) {
      if (status.code == code1) {
        return status;
      }
    }

    throw new IllegalArgumentException("Unknown status: " + hex);
  }

}
