/**
 *
 */
package cardiag.serial;

import jssc.SerialPortException;

/**
 * Port communication exception. Command was not successfully executed.
 *
 * @author David Matějček
 */
public class PortCommunicationException extends RuntimeException {

  private static final long serialVersionUID = 5530145486386126281L;


  /**
   * @param message - a short description of exception
   */
  public PortCommunicationException(final String message) {
    super(message);
  }


  /**
   * @param cause - a cause of the exception.
   */
  public PortCommunicationException(final SerialPortException cause) {
    super(cause);
  }

}
