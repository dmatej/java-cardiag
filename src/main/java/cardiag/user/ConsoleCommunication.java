/**
 *
 */
package cardiag.user;


/**
 * @author David Matějček
 *
 */
public interface ConsoleCommunication {

  void format(String fmt, Object... args);
  void flush();
  String readLine();
  String readLine(String fmt, Object... args);

}
