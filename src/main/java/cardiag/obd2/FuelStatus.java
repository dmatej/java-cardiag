/**
 *
 */
package cardiag.obd2;

/**
 * @author David Matějček
 */
public enum FuelStatus {

  OPEN_LOW_TEMP(1, "Open loop due to insufficient engine temperature"), //
  CLOSED_USING_OXYGEN(2, "Closed loop, using oxygen sensor feedback to determine fuel mix"), //
  OPEN_LOAD(4, "Open loop due to engine load OR fuel cut due to deceleration"), //
  OPEN_FAILURE(8, "Open loop due to system failure"), //
  CLOSED_FAILURE_FEEDBACK(16,
      "Closed loop, using at least one oxygen sensor but there is a fault in the feedback system"), //
  ZERO5(32, "Always zero"), ZERO6(0x06, "Always zero"), ZERO7(0x07, "Always zero"), ;

  final int code;
  final String description;


  FuelStatus(int value, String description) {
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


  public static FuelStatus parseHex(final String hex) {
    if (hex == null) {
      throw new IllegalArgumentException("Invalid hex code: " + hex);
    }

    int code1 = Integer.parseInt(hex, 16);
    for (FuelStatus status : FuelStatus.values()) {
      if (status.code == code1) {
        return status;
      }
    }

    throw new IllegalArgumentException("Unknown status: " + hex);
  }
}
