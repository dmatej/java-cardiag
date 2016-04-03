/**
 *
 */
package cardiag.output;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Matějček
 */
public class OutputFileWriter implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(OutputFileWriter.class);

  private final OutputStreamWriter writer;
  private final File outputFile;


  /**
   * Initializes the output.
   *
   * @param outputFile
   */
  public OutputFileWriter(final File outputFile) {
    this.outputFile = outputFile;
    this.writer = openWriter(outputFile);
  }


  private OutputStreamWriter openWriter(final File file) {
    LOG.trace("openWriter(outputFile={})", file);

    FileOutputStream stream = null;
    try {
      stream = new FileOutputStream(file, true);
      return new OutputStreamWriter(stream, Charset.forName("UTF-8"));
    } catch (final IOException e) {
      IOUtils.closeQuietly(stream);
      throw new IllegalArgumentException("Cannot write the report to the outputFile: " + file, e);
    }
  }


  /**
   * Writes a header.
   *
   * @param header
   */
  public void writeHeader(final String header) {
    LOG.debug("writeHeader(header={})", header);
    try {
      this.writer.write('\n');
      final String lineSeparator = StringUtils.repeat("-", header.length()) + '\n';
      this.writer.write(lineSeparator);
      this.writer.write(header + '\n');
      this.writer.write(lineSeparator);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot write to the outputFile: " + outputFile, e);
    }
  }


  /**
   * Writes parameters into the single line in the outputFile.
   *
   * @param label
   * @param value - may be null, then -- are written.
   * @param unit - may be null, then not written.
   */
  public void writeData(final String label, final Object value, final String unit) {
    LOG.debug("writeData(label={}, value={}, unit={})", label, value, unit);

    final StringBuilder string = new StringBuilder(256);
    string.append(label).append(": ");
    if (value == null) {
      string.append("--");
    } else {
      string.append(value);
    }
    if (unit != null) {
      string.append(' ').append(unit);
    }
    string.append('\n');

    try {
      this.writer.write(string.toString());
    } catch (IOException e) {
      throw new IllegalStateException("Cannot write to the outputFile: " + outputFile, e);
    }
  }


  /**
   * Silently closes the outputFile.
   */
  public void close() {
    IOUtils.closeQuietly(writer);
  }
}
