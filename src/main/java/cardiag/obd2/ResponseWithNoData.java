/**
 *
 */
package cardiag.obd2;


/**
 * @author David Matějček
 *
 */
public class ResponseWithNoData extends Response {

  /**
   * @param mode
   * @param pid
   */
  public ResponseWithNoData(Mode mode, PID pid) {
    super(true, mode, pid);
  }

}
