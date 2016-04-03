/**
 *
 */
package cardiag.output;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cardiag.obd2.Report;

/**
 * @author David Matějček
 */
public class ReportFileWriter {

  private static final Logger LOG = LoggerFactory.getLogger(ReportFileWriter.class);

  private static final String DEGREES = "°";
  private static final String DEGREES_OF_CENTIGRADE = "°C";
  private static final String KILOMETERS = "km";
  private static final String PERCENTS = "%";

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

    final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss.SSS");
    writer.writeData("Time:", sdf.format(report.getTimestamp()), null);
    writer.writeData("ECU compatibility", report.getEcuCompatibility(), null);
    writer.writeData("Supported PIDs", report.getSupportedPIDS(), null);
    writer.writeData("Monitor status", report.getMonitorStatus(), null);
    writer.writeData("Ambient temperature", report.getAmbientAirTemperature(), DEGREES_OF_CENTIGRADE);

    writer.writeHeader("Errors");
    writer.writeData("Distance since errors cleared", report.getDistanceSinceErrorCodesCleared(), KILOMETERS);
    writer.writeData("Distance with malfunction", report.getDistanceWithMalfunction(), KILOMETERS);
    writer.writeData("Reported faults", report.getFaults(), null);

    writer.writeHeader("Engine Temperatures");
    writer.writeData("Engine coolant temperature", report.getEngineCoolantTemperature(), DEGREES_OF_CENTIGRADE);
    writer.writeData("Engine oil temperature", report.getEngineOilTemperature(), DEGREES_OF_CENTIGRADE);
    writer.writeData("Intake air temperature", report.getIntakeAirTemperature(), DEGREES_OF_CENTIGRADE);
    writer.writeData("Intake air temperature sensor", report.getIntakeAirTemperatureSensor(), DEGREES_OF_CENTIGRADE);
    writer.writeData("Catalyst temperature - sensor1", report.getCatalystTemperatureSensor1(), DEGREES_OF_CENTIGRADE);
    writer.writeData("Catalyst temperature - sensor2", report.getCatalystTemperatureSensor2(), DEGREES_OF_CENTIGRADE);
    writer.writeData("Exhaust gas recirculation temperature", report.getExhaustGasRecirculationTemperature(),
        DEGREES_OF_CENTIGRADE);
    writer.writeData("Manifold surface temperature", report.getManifoldSurfaceTemperature(), DEGREES_OF_CENTIGRADE);

    writer.writeHeader("Engine timing and fuel");
    writer.writeData("Engine load", report.getEngineLoad(), PERCENTS);
    writer.writeData("Fuel injection timing", report.getFuelInjectionTiming(), DEGREES);
    writer.writeData("Secondary air status", report.getSecondaryAirStatus(), null);
    writer.writeData("Fuel status", report.getFuelStatus(), null);
    writer.writeData("Fuel rate", report.getFuelRate(), "L/h");
    writer.writeData("Fuel level input", report.getFuelLevelInput(), PERCENTS);
    writer.writeData("Fuel trim pecent in long term", report.getFuelTrimPercentLongTerm(), PERCENTS);
    writer.writeData("Fuel trim pecent in short term", report.getFuelTrimPercentShortTerm(), PERCENTS);
    writer.writeData("Commanded EGR", report.getCommandedEgr(), PERCENTS);
    writer.writeData("EGR error", report.getEgrError(), PERCENTS);
    writer.writeData("Ethanol fuel", report.getEthanolFuel(), PERCENTS);

    // writer.writeData("", report.get, "");
  }
}
