/**
 *
 */
package cardiag.obd2;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * @author David Matějček
 *
 */
public class MonitorStatus {

  private boolean mil;
  private int emmissionRelatedDTCs;
  private boolean comporessionIgnited;
  private boolean missfireAvailable;
  private boolean missfireIncomplete;
  private boolean fuelAvailable;
  private boolean fuelIncomplete;
  private boolean componentsAvailable;
  private boolean componentsIncomplete;
  private boolean reservedBitB7;

  public void setMIL(boolean mil) {
    this.mil = mil;
  }

  public void setEmissionRelatedDTCs(int dtcs) {
    this.emmissionRelatedDTCs = dtcs;
  }

  /**
   * @param compression (or spark)
   */
  public void setIgnition(boolean compression) {
    this.comporessionIgnited = compression;
  }

  public void setMissfire(boolean available, boolean incomplete) {
    this.missfireAvailable = available;
    this.missfireIncomplete = incomplete;
  }

  public void setFuel(boolean available, boolean incomplete) {
    this.fuelAvailable = available;
    this.fuelIncomplete = incomplete;
  }


  public void setComponents(boolean available, boolean incomplete) {
    this.componentsAvailable = available;
    this.componentsIncomplete = incomplete;
  }

  public void setReservedBitB7(boolean bit) {
    this.reservedBitB7 = bit;
  }



  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
