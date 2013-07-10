/**
 *
 */
package cardiag.serial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jssc.SerialPortList;

import org.apache.commons.lang.StringUtils;
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

  /**
   * @param line
   * @return
   */
  public static boolean[] convertHexToBooleanArray(final String... line) {
    LOG.debug("convertHexToBooleanArray(line={})", (Object) line);
    final boolean[] bools = new boolean[line.length * 8];
    int k = 0;
    for (int i = 0; i < line.length; i++) {
      String hex = line[i];
      int val = Integer.parseInt(hex, 16);
      String binary = StringUtils.leftPad(Integer.toBinaryString(val), 8, '0');
      for (int j = 0; j < binary.length(); j++) {
        bools[k++] = binary.charAt(j) == '1';
      }
    }
    return bools;
  }


  public static int toInteger(boolean... bools) {
    int base = 1;
    int index = bools.length - 1;
    int value = 0;
    while(index >= 0) {
      value += bools[index] ? base : 0;
      base *= 2;
      index--;
    }
    return value;
  }


}
