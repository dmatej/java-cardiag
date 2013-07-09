/**
 *
 */
package cardiag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.serial.PortCommunication;
import cardiag.serial.PortConfiguration;
import cardiag.serial.SerialUtils;
import cardiag.user.ConsoleCommunication;
import cardiag.user.ConsoleWrapper;
import cardiag.user.UserCommunication;


/**
 * The main program. Initiates the user interface.
 *
 * @author David Matějček
 */
public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);


  /**
   * The main method.
   *
   * @param args
   * @throws Exception
   */
  public static void main(final String... args) throws Exception {
    LOG.debug("main(args={})", (Object) args);

    final ConsoleCommunication console = new ConsoleWrapper(System.console());
    final UserCommunication user = new UserCommunication(console);

    final List<String> portNames = SerialUtils.getPortNames();
    final PortConfiguration cfg = user.readPortConfiguration(portNames);

    final PortCommunication port = new PortCommunication(cfg);

    // TODO: while(true) cycle to read user commands, run them and to process responses until user calls exit.
  }
}
