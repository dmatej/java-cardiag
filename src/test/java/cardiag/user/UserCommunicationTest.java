/**
 *
 */
package cardiag.user;

import static org.junit.Assert.*;
import java.util.Arrays;

import org.junit.Test;

/**
 * @author David Matějček
 */
public class UserCommunicationTest {

  @Test
  public void testSelectPort() {
    final ConsoleMock console = new ConsoleMock();
    final UserCommunication comm = new UserCommunication(console);
    final String[] ports = {"/dev/ttyUSB0", "/dev/ttyS10"};
    console.appendLine("0");
    String selectedPort = comm.selectPort(Arrays.asList(ports));
    assertEquals(ports[0], selectedPort);
    console.appendLine("1");
    selectedPort = comm.selectPort(Arrays.asList(ports));
    assertEquals(ports[1], selectedPort);
    console.appendLine("X");
    selectedPort = comm.selectPort(Arrays.asList(ports));
    assertNull(selectedPort);
    console.appendLine("SOMETHING STUPID");
    console.appendLine("X");
    selectedPort = comm.selectPort(Arrays.asList(ports));
    assertNull(selectedPort);
  }

}
