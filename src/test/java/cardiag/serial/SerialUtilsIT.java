/**
 *
 */
package cardiag.serial;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author David Matějček
 */
public class SerialUtilsIT {
  private static final Logger LOG = LoggerFactory.getLogger(SerialUtilsIT.class);

  @Test
  public void testGetPortNames() {
    final List<String> names = SerialUtils.getPortNames();
    LOG.info("port names={}", names);
    assertNotNull(names);
    assertFalse("You must have at last one serial port. No serial port found.", names.isEmpty());
  }

}
