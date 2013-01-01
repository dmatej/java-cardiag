/**
 *
 */
package cardiag.user;

import java.util.LinkedList;

/**
 * @author David Matějček
 */
public class ConsoleMock implements ConsoleCommunication {

  private LinkedList<String> lines = new LinkedList<String>();


  @Override
  public void format(String fmt, Object... args) {
    System.out.format(fmt, args);
  }


  @Override
  public void flush() {
    System.out.flush();
  }


  public void appendLine(final String line) {
    this.lines.add(line);
  }


  @Override
  public String readLine() {
    final String line = this.lines.pollFirst();
    System.out.println(line);
    return line;
  }


  @Override
  public String readLine(final String fmt, final Object... args) {
    format(fmt, args);
    return readLine();
  }
}
