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

    final Action action = parseAction(args);
    final File homeDir = parseHomeDir(args);

    final OBD2Standard obd2 = new OBD2Standard(cfg);
    try {
      if (action == Action.WATCH) {
        watch(obd2, homeDir, 1000);
      } else if (action == Action.REPORT) {
        watch(obd2, homeDir, 1);
      } else if (action == Action.CLEAR_TROUBLE_CODES) {
        obd2.clearTroubleCodes();
      }
    } finally {
      obd2.close();
    }
  }


  private static void watch(final OBD2Standard obd2, final File homeDir, final int iterations) {
    LOG.info("watch(obd2={}, homeDir={}, iterations={})", obd2, homeDir, iterations);
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss.SSS");
    if (!homeDir.exists()) {
      homeDir.mkdirs();
    }
    final File file = new File(homeDir, "report" + sdf.format(new Date()) + ".txt");
    final ReportFileWriter writer = new ReportFileWriter(file);
    int i = iterations;
    while (i-- > 0) {
      final Report report = obd2.createReport();
      writer.write(report);
    }
  }


  private static Action parseAction(final String... args) {
    LOG.trace("parseAction(args={})", (Object[]) args);

    if (args == null || args.length == 0) {
      return Action.REPORT;
    }
    return Action.parse(args[0]);
  }


  private static File parseHomeDir(final String... args) {
    LOG.trace("parseHomeDir(args={})", (Object[]) args);

    if (args == null || args.length < 2) {
      return new File(System.getProperty("java.io.tmpdir"), "java-cardiag");
    }
    return new File(args[1]);
  }
}
