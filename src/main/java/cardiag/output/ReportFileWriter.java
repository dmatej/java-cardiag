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
 *
 */
public class ReportFileWriter {
  private static final Logger LOG = LoggerFactory.getLogger(ReportFileWriter.class);
  private final File outputFile;

  public ReportFileWriter(final File outputFile) {
    this.outputFile = outputFile;
  }


  public void write(final Report report) {
    final OutputFileWriter writer = new OutputFileWriter(outputFile);
    try {
      writeTemperatures(writer, report);


    } finally {
      IOUtils.closeQuietly(writer);
    }
  }


  private void writeTemperatures(final OutputFileWriter writer, final Report report) {
    LOG.trace("writeTemperatures(writer, report)");
    writer.writeHeader("Engine Temperatures");
  }
}
