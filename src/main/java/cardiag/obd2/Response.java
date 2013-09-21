/**
 *
 */
package cardiag.obd2;



/**
 * OBD2 protocol response.
 *
 * @author David Matějček
 */
public class Response {

  private final Mode mode;
  private final PID pid;
  private final String[] data;


  /**
   * Only a simple constructor.
   *
   * @param mode - same as the mode of the request.
   * @param pid - same as the pid of the request.
   * @param data - returned data.
   */
  public Response(final Mode mode, final PID pid, final String... data) {
    this.mode = mode;
    this.pid = pid;
    this.data = data;
  }


  /**
   * @return a data part of the response - several lines of hex numbers separated by a space.
   */
  public String[] getData() {
    return data;
  }
}
