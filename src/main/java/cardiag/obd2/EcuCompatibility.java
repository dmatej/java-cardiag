/**
 *
 */
package cardiag.obd2;

/**
 * @author David Matějček
 */
public enum EcuCompatibility {
  CARB(0x01, "OBD-II as defined by the CARB"), EPA(0x02, "OBD as defined by the EPA"), OBD_OBDII(0x03, "OBD and OBD-II"), OBDI(
      0x04, "OBD-I"), INCOMPATIBLE(0x05, "Not meant to comply with any OBD standard"), EOBD_OBDII(0x06,
      "EOBD and OBD-II"), EOBD_OBD(0x08, "EOBD and OBD"), EOBD_OBD_OBDII(0x09, "EOBD, OBD and OBD II"), JOBD(0x0A,
      "JOBD (Japan)"), JOBD_OBDII(0x0B, "JOBD and OBD II"), JOBD_EOBD(0x0C, "JOBD and EOBD"), JOBD_EOBD_OBDII(0x0D,
      "JOBD, EOBD, and OBD II");

  private int code;
  private String description;


  EcuCompatibility(int code, String description) {
    this.code = code;
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


  public static EcuCompatibility parseHex(final String hex) {
    if (hex == null) {
      throw new IllegalArgumentException("Invalid hex code: " + hex);
    }

    int code1 = Integer.parseInt(hex, 16);
    for (EcuCompatibility status : EcuCompatibility.values()) {
      if (status.code == code1) {
        return status;
      }
    }

    throw new IllegalArgumentException("Unknown status: " + hex);
  }

}
