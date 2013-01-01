/**
 *
 */
package cardiag.serial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Matějček
 */
public class PortCommunication {

  private static final Logger LOG = LoggerFactory.getLogger(PortCommunication.class);
  private static final String RESPONSE_OK = "OK";
  private static final String RESPONSE_TERMINALCHAR = ">";

  private final SerialPort port;


  public PortCommunication(final PortConfiguration cfg) throws SerialPortException {
    LOG.debug("PortCommunication(cfg={})", cfg);
    this.port = new SerialPort(cfg.getPortName());
    if (!this.port.openPort()) {
      throw new IllegalStateException("Cannot open port!");
    }
    // see page 7 in elm327.pdf - do not change!
    if (!this.port.setParams(38400, 8, 1, 0, true, true)) {
      throw new IllegalStateException("Setting params was unsuccessful!");
    }
  }


  protected List<String> readResponse(final long maxTime) throws SerialPortException {
    LOG.trace("readResponse(maxTime={})", maxTime);

    final StringBuilder buffer = new StringBuilder(256);
    final long start = System.currentTimeMillis();
    while (start + maxTime > System.currentTimeMillis()) {
      if (this.port.getInputBufferBytesCount() > 0) {
        final String string = this.port.readString();
        LOG.trace("response string={}", string);
        buffer.append(string).append('\r');
        if (StringUtils.endsWith(string, RESPONSE_TERMINALCHAR)) {
          buffer.setLength(buffer.length() - 2);
          break;
        }
      } else {
        sleep();
      }
    }
    final String response = buffer.toString();
    if (response == null || response.trim().isEmpty()) {
      return Collections.emptyList();
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
  }


  private void sleep() {
    LOG.trace("sleep()");
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // what to do?
    }
  }


  private void writeln(String... command) throws SerialPortException {
    LOG.debug("writeln(command={})", Arrays.toString(command));
    for (String commandPart : command) {
      this.port.writeString(commandPart);
    }
    this.port.writeString("\r\n");
  }

  private String translate(final boolean value) throws SerialPortException {
    LOG.trace("translate(value={})", value);
    return value ? "1" : "0";
  }

  private void checkOkResponse() throws SerialPortException, CommunicationException {
    LOG.trace("checkOkResponse()");
    final List<String> response = readResponse(1000);
    // FIXME: not nice ... we should know exactly ...
    if (!RESPONSE_OK.equalsIgnoreCase(response.get(0)) && !RESPONSE_OK.equalsIgnoreCase(response.get(1))) {
      throw new CommunicationException("Command unsuccessful! Response: " + response);
    }
  }


  public String at(final String command) throws SerialPortException {
    LOG.info("at(command={})", command);
    writeln("AT", command);
    return readResponse(1000).get(0);
  }


  public void reset() throws SerialPortException, CommunicationException {
    LOG.debug("reset()");
    writeln("ATZ");
    final List<String> response = readResponse(20000);
    // with echo on the first line will be ATZ (sent command)
    // another line will be a device type identification.
    if (response.isEmpty()) {
      throw new CommunicationException("Command unsuccessful! Response: " + response);
    }
  }


  public void setEcho(final boolean on) throws SerialPortException, CommunicationException {
    LOG.debug("setEcho(on={})", on);
    writeln("ATE", translate(on));
    checkOkResponse();
  }


  public void setLineTermination(final boolean on) throws SerialPortException, CommunicationException {
    LOG.debug("setLineTermination(on={})", on);
    writeln("ATL", translate(on));
    checkOkResponse();
  }

}
