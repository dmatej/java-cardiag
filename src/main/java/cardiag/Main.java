/**
 *
 */
package cardiag;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.obd2.OBD2Standard;
import cardiag.obd2.Report;
import cardiag.output.ReportFileWriter;
import cardiag.serial.PortConfiguration;
import cardiag.serial.SerialUtils;
import cardiag.user.Action;
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

    final OBD2Standard obd2 = new OBD2Standard(cfg);
    final Action action = parseAction(args);

    if (action == Action.REPORT) {
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
      final File file = new File("report" + sdf.format(new Date()) + ".txt");
      final Report report = obd2.createReport();
      final ReportFileWriter writer = new ReportFileWriter(file);
      writer.write(report);
    }

    // TODO: while(true) cycle to read user commands, run them and to process responses until user
    // calls exit.
  }


  private static Action parseAction(final String... args) {
    LOG.trace("parseAction(args={})", (Object[]) args);

    if (args == null || args.length == 0) {
      return Action.REPORT;
    }
    return Action.parse(args[0]);
  }
}
