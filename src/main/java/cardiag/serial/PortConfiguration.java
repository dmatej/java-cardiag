/**
 *
 */
package cardiag.serial;

/**
 * @author David Matějček
 */
public class PortConfiguration {

  private String portName;
  private Long commandTimeout;


  public String getPortName() {
    return portName;
  }


  public void setPortName(final String portName) {
    this.portName = portName;
  }


  public Long getCommandTimeout() {
    return commandTimeout;
  }


  /**
   * @param commandTimeout - time in millis, must not be null.
   */
  public void setCommandTimeout(final Long commandTimeout) {
    this.commandTimeout = commandTimeout;
  }

}
