/**
 *
 */
package cardiag.obd2;


/**
 * Port communication exception. Command was not successfully executed.
 *
 * @author David Matějček
 */
public class OBD2Exception extends Exception {
  private static final long serialVersionUID = -3794508502469587712L;


  /**
   * @param message - a short description of exception
   */
  public OBD2Exception(final String message) {
    super(message);
  }
}
