/**
 *
 */
package cardiag.serial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jssc.SerialPortList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author David Matějček
 */
public class SerialUtils {
  private static final Logger LOG = LoggerFactory.getLogger(SerialUtils.class);

  public static List<String> getPortNames() {
    final String[] portNames = SerialPortList.getPortNames();
    if (portNames == null || portNames.length == 0) {
      return Collections.emptyList();
    }

    return Arrays.asList(portNames);
  }

}
