/**
 *
 */
package cardiag.obd2;



/**
 * @author David Matějček
 *
 */
public class Response {

  private final Mode mode;
  private final PID pid;
  private final String[] data;


  public Response(final Mode mode, final PID pid, final String... data) {
    this.mode = mode;
    this.pid = pid;
    this.data = data;
  }


  public String[] getData() {
    return data;
  }
}
