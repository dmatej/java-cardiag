/**
 *
 */
package cardiag.output;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.obd2.Report;

/**
 * @author David Matějček
 */
public class ReportFileWriter {

  private static final Logger LOG = LoggerFactory.getLogger(ReportFileWriter.class);
  private final File outputFile;


  /**
   * @param outputFile
   */
  public ReportFileWriter(final File outputFile) {
    this.outputFile = outputFile;
  }


  /**
   * Opens the file, writes the report and closes the file.
   *
   * @param report
   */
  public void write(final Report report) {
    final OutputFileWriter writer = new OutputFileWriter(outputFile);
    try {
      write(writer, report);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }


  private void write(final OutputFileWriter writer, final Report report) {
    LOG.trace("write(writer, report)");

    writer.writeHeader("Basic info");
    writer.writeData("ECU compatibility", report.getEcuCompatibility(), null);
    writer.writeData("Supported PIDs", report.getSupportedPIDS(), "");
    writer.writeData("Monitor status", report.getMonitorStatus(), null);

    writer.writeHeader("Errors");
    writer.writeData("Distance since errors cleared", report.getDistanceSinceErrorCodesCleared(), "km");
    writer.writeData("Distance with malfunction", report.getDistanceWithMalfunction(), "km");
    writer.writeData("Reported faults", report.getFaults(), null);

    writer.writeHeader("Engine Temperatures");
    writer.writeData("Engine coolant temperature", report.getEngineCoolantTemperature(), "°C");
    writer.writeData("Intake air temperature", report.getIntakeAirTemperature(), "°C");

    writer.writeHeader("Engine timing and fuel");
    writer.writeData("Engine load", report.getEngineLoad(), "%");
    writer.writeData("Fuel injection timing", report.getFuelInjectionTiming(), "°");
    writer.writeData("Secondary air status", report.getSecondaryAirStatus(), null);
    writer.writeData("Fuel status", report.getFuelStatus(), null);
    writer.writeData("Fuel rate", report.getFuelRate(), "L/h");
    writer.writeData("Fuel level input", report.getFuelLevelInput(), "%");
    writer.writeData("Fuel trim pecent in long term", report.getFuelTrimPercentLongTerm(), "%");
    writer.writeData("Fuel trim pecent in short term", report.getFuelTrimPercentShortTerm(), "%");
    // writer.writeData("", report.get, "");
  }
}
