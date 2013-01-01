/**
 *
 */
package cardiag.user;

import java.io.Console;

/**
 * @author David Matějček
 */
public class ConsoleWrapper implements ConsoleCommunication {

  private final Console console;


  public ConsoleWrapper(final Console console) {
    if (console == null) {
      throw new IllegalArgumentException("Console not present, program cannot interact with user!");
    }
    this.console = console;
  }


  @Override
  public void format(String fmt, Object... args) {
    this.console.format(fmt, args);
  }


  @Override
  public void flush() {
    this.console.flush();
  }


  @Override
  public String readLine() {
    return this.console.readLine();
  }


  @Override
  public String readLine(String fmt, Object... args) {
    return this.console.readLine(fmt, args);
  }

}
