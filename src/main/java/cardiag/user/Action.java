/**
 *
 */
package cardiag.user;


/**
 * @author David Matějček
 */
public enum Action {

  REPORT,
  WATCH,
  CLEAR_TROUBLE_CODES;


  public static Action parse(final String str) {
    for (Action action : Action.values()) {
      if (action.name().equalsIgnoreCase(str)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Invalid action: " + str);
  }

}
