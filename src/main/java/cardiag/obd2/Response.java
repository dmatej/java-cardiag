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
  private boolean error;


  /**
   * Only a simple constructor.
   *
   * @param error - if true, data are an error message
   * @param mode - same as the mode of the request.
   * @param pid - same as the pid of the request.
   * @param data - returned data.
   */
  public Response(final boolean error, final Mode mode, final PID pid, final String... data) {
    this.mode = mode;
    this.pid = pid;
    this.data = data == null ? new String[1] : data;
    this.error = error;
  }


  /**
   * @return a data part of the response - several lines of hex numbers separated by a space.
   */
  public String[] getData() {
    return data;
  }


  /**
   * @return true if the data contains only error message.
   */
  public boolean isError() {
    return error;
  }
}
