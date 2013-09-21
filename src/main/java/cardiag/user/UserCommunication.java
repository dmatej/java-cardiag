/**
 *
 */
package cardiag.user;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.serial.PortConfiguration;

/**
 * @author David Matějček
 */
public class UserCommunication {

  private static final Logger LOG = LoggerFactory.getLogger(UserCommunication.class);
  private static final String EXIT = "X";

  private final ConsoleCommunication console;


  /**
   * @param console
   */
  public UserCommunication(final ConsoleCommunication console) {
    LOG.trace("UserCommunication(console={})", console);
    if (console == null) {
      throw new IllegalArgumentException("Console not present, program cannot interact with user!");
    }
    this.console = console;
  }


  /**
   * @param portNames
   * @return
   */
  public PortConfiguration readPortConfiguration(final List<String> portNames) {
    LOG.debug("readPortConfiguration(portNames={})", portNames);
    final PortConfiguration cfg = new PortConfiguration();
    final String selectedPortName = selectPort(portNames);
    cfg.setPortName(selectedPortName);
    cfg.setCommandTimeout(10000L);
    return cfg;
  }


  protected String selectPort(final List<String> portNames) {
    LOG.debug("selectPort(portNames={})", portNames);

    // FIXME: program should not end in the future. User maybe forgot to connect the cable.
    if (portNames.isEmpty()) {
      throw new IllegalStateException("No serial port to select.");
    }
    while (true) {
      console.format("----\nSelect the port (type number):\n");
      for (int i = 0; i < portNames.size(); i++) {
        final String portName = portNames.get(i);
        console.format("%d: %s\n", i, portName);
      }
      console.format("X: Exit\n");
      console.flush();
      final String selectedPort = StringUtils.trimToNull(console.readLine("Port: "));
      if (StringUtils.isNumeric(selectedPort)) {
        final String portName = portNames.get(Integer.valueOf(selectedPort));
        if (portName != null) {
          return portName;
        }
      }
      if (EXIT.equalsIgnoreCase(selectedPort)) {
        return null;
      }
    }
  }

}
