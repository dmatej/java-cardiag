/**
 *
 */
package cardiag.serial;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Communication with the serial port device.
 *
 * @author David Matějček
 */
public class PortCommunication implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(PortCommunication.class);
  private static final String RESPONSE_OK = "OK";
  private static final String RESPONSE_TERMINALCHAR = ">";

  private final PortConfiguration cfg;
  private final SerialPort port;


  /**
   * Initializes and opens the port.
   *
   * @param cfg - a port configuration
   * @throws PortCommunicationException - cannot initialize the communication.
   */
  public PortCommunication(final PortConfiguration cfg) throws PortCommunicationException {
    LOG.debug("PortCommunication(cfg={})", cfg);
    try {
      this.cfg = cfg;
      this.port = new SerialPort(cfg.getPortName());
      if (!this.port.openPort()) {
        throw new PortCommunicationException("Cannot open port!");
      }
      // see page 7 in elm327.pdf - do not change!
      if (!this.port.setParams(38400, 8, 1, 0, true, true)) {
        throw new PortCommunicationException("Setting parameters was unsuccessful!");
      }
    } catch (SerialPortException e) {
      throw new PortCommunicationException(e);
    }
  }


  /**
   * Reads responses line by line from the buffer until {@value #RESPONSE_TERMINALCHAR} comes or
   * timeout occurs.
   *
   * @return a list of lines in response, never null and never empty list.
   * @throws PortCommunicationException - if there was no response or prompt was missing.
   */
  public List<String> readResponse() throws PortCommunicationException {
    LOG.trace("readResponse()");

    try {
      final StringBuilder buffer = new StringBuilder(256);
      final long start = System.currentTimeMillis();
      final long timeout = cfg.getCommandTimeout();
      while (true) {
        if (start + timeout < System.currentTimeMillis()) {
          throw new PortCommunicationException(String.format("Timeout %dms occured.", timeout));
        }
        if (this.port.getInputBufferBytesCount() == 0) {
          Thread.yield();
          continue;
        }
        final String string = this.port.readString();
        LOG.trace("response string={}", string);
        buffer.append(string).append('\r');
        if (StringUtils.endsWith(string, RESPONSE_TERMINALCHAR)) {
          buffer.setLength(buffer.length() - 2);
          break;
        }
      }
      final String response = buffer.toString();
      if (response == null || response.trim().isEmpty()) {
        throw new PortCommunicationException("Retrieved no response from the port.");
      }
      final String[] lines = StringUtils.split(response, '\r');
      final List<String> responses = new ArrayList<String>(lines.length);
      for (String line : lines) {
        final String trimmed = StringUtils.trimToNull(line);
        if (trimmed != null) {
          responses.add(trimmed);
        }
      }

      LOG.info("Received response: {}", responses);
      return responses;
    } catch (SerialPortException e) {
      throw new PortCommunicationException(e);
    }
  }


  /**
   * Writes command and arguments to the port.
   *
   * @param command - command and it's arguments.
   * @throws PortCommunicationException
   */
  public void writeln(String... command) throws PortCommunicationException {
    LOG.debug("writeln(command={})", Arrays.toString(command));
    try {
      for (String commandPart : command) {
        this.port.writeString(commandPart);
      }
      this.port.writeString("\r\n");
    } catch (SerialPortException e) {
      throw new PortCommunicationException(e);
    }
  }


  /**
   * @param value
   * @return 1 for true, 0 for false.
   * @throws PortCommunicationException
   */
  protected final String translate(final boolean value) throws PortCommunicationException {
    LOG.trace("translate(value={})", value);
    return value ? "1" : "0";
  }


  /**
   * Checks for OK in port response.
   *
   * @throws PortCommunicationException
   */
  protected void checkOkResponse() throws PortCommunicationException {
    LOG.trace("checkOkResponse()");
    final List<String> response = readResponse();
    if (response == null || response.isEmpty()) {
      throw new PortCommunicationException("No response.");
    }
    final int resultCodeIndex = response.size() - 1;
    if (!RESPONSE_OK.equalsIgnoreCase(response.get(resultCodeIndex))) {
      throw new PortCommunicationException("Command unsuccessful! Response: " + response);
    }
  }


  /**
   * Executes an AT command and returns an answer.
   *
   * @param command - an AT command to execute.
   * @return an answer of the command.
   * @throws PortCommunicationException
   */
  public String at(final String command) throws PortCommunicationException {
    LOG.info("at(command={})", command);
    writeln("AT", command);
    return readResponse().get(0);
  }


  /**
   * Sends ATE signal and sets the command echo on/off
   *
   * @param on
   * @throws PortCommunicationException
   */
  public void setEcho(final boolean on) throws PortCommunicationException {
    LOG.debug("setEcho(on={})", on);
    writeln("ATE", translate(on));
    checkOkResponse();
  }


  /**
   * Sends ATL signal and sets the line termination on/off
   *
   * @param on
   * @throws PortCommunicationException
   */
  public void setLineTermination(final boolean on) throws PortCommunicationException {
    LOG.debug("setLineTermination(on={})", on);
    writeln("ATL", translate(on));
    checkOkResponse();
  }


  /**
   * Sends AT Z command, resets the communication.
   *
   * @throws PortCommunicationException
   */
  public void reset() throws PortCommunicationException {
    LOG.debug("reset()");
    final String response = at("Z");
    // with echo on the first line will be ATZ (sent command)
    // another line will be a device type identification.
    if (response == null) {
      throw new PortCommunicationException("Command unsuccessful! Response: " + response);
    }
  }


  /**
   * Closes the port.
   */
  @Override
  public void close() {
    try {
      this.port.closePort();
    } catch (SerialPortException e) {
      throw new IllegalStateException("Cannot close the port.", e);
    }
  }

}
