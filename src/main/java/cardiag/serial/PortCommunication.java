/**
 *
 */
package cardiag.serial;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortException;

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
    } catch (final SerialPortException e) {
      throw new PortCommunicationException(e);
    }
  }


  /**
   * Reads responses line by line from the buffer until {@value #RESPONSE_TERMINALCHAR} comes or
   * timeout occurs.
   *
   * @return a response, never null and never empty.
   * @throws PortCommunicationException - if there was no response or prompt was missing.
   */
  public String readResponse(final String request) throws PortCommunicationException {
    LOG.debug("readResponse(request={})", request);

    try {
      final String response = readAll(request);
      LOG.debug("Received response: {}", response);
      if (response == null) {
        throw new PortCommunicationException("Retrieved no response from the port.");
      }
      return response;
    } catch (final SerialPortException e) {
      throw new PortCommunicationException(e);
    }
  }


  private String readAll(final String request) throws SerialPortException {
    LOG.trace("readAll(request={})", request);
    final StringBuilder buffer = new StringBuilder(256);
    final long start = System.currentTimeMillis();
    final long timeout = cfg.getCommandTimeout();
    while (true) {
      if (start + timeout < System.currentTimeMillis()) {
        throw new PortCommunicationException(String.format("Timeout %d ms occured.", timeout));
      }
      if (this.port.getInputBufferBytesCount() == 0) {
        Thread.yield();
        continue;
      }
      final String string = StringUtils.trimToNull(this.port.readString());
      LOG.trace("response string={}", string);
      if (string == null) {
        sleep(100L);
        continue;
      }
      buffer.append(string);
      if (StringUtils.endsWith(string, RESPONSE_TERMINALCHAR)) {
        LOG.trace("Response terminated with the character {}. Character deleted.", RESPONSE_TERMINALCHAR);
        buffer.setLength(buffer.length() - 1);
        if (buffer.indexOf(request) == 0) {
          buffer.replace(0, request.length(), "");
        }
        return buffer.toString().trim();
      }
    }
  }


  private void sleep(final long timeInMillis) {
    try {
      Thread.sleep(100L);
    } catch (final InterruptedException e) {
      LOG.warn("Interrupted.");
    }
  }


  /**
   * Writes command and arguments to the port.
   *
   * @param command - command and it's arguments.
   * @throws PortCommunicationException
   */
  public void writeln(final String... command) throws PortCommunicationException {
    LOG.debug("writeln(command={})", Arrays.toString(command));
    try {
      for (final String commandPart : command) {
        this.port.writeString(commandPart);
      }
      this.port.writeString("\r\n");
    } catch (final SerialPortException e) {
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
   * @param request
   *
   * @throws PortCommunicationException
   */
  protected void checkOkResponse(final String request) throws PortCommunicationException {
    LOG.trace("checkOkResponse(request={})", request);
    final String response = readResponse(request);
    if (response == null || response.isEmpty()) {
      throw new PortCommunicationException("No response.");
    }
    if (!RESPONSE_OK.equalsIgnoreCase(response)) {
      throw new PortCommunicationException("Command unsuccessful! Response: '" + response + "'");
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
    return readResponse("AT".concat(command));
  }


  /**
   * Sends ATE signal and sets the command echo on/off
   *
   * @param on
   * @throws PortCommunicationException
   */
  public void setEcho(final boolean on) throws PortCommunicationException {
    LOG.debug("setEcho(on={})", on);
    final String onTranslated = translate(on);
    writeln("ATE", onTranslated);
    checkOkResponse("ATE".concat(onTranslated));
  }


  /**
   * Sends ATL signal and sets the line termination on/off
   *
   * @param on
   * @throws PortCommunicationException
   */
  public void setLineTermination(final boolean on) throws PortCommunicationException {
    LOG.debug("setLineTermination(on={})", on);
    final String onTranslated = translate(on);
    writeln("ATL", onTranslated);
    checkOkResponse("ATL".concat(onTranslated));
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
    } catch (final SerialPortException e) {
      throw new IllegalStateException("Cannot close the port.", e);
    }
  }

}
