package calculator;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    calcState state = new calcState();

    while (!state.activityText.equals("Exit command")) {
      state.reset();
      state.line = scanner.nextLine();
      if (!state.line.equals("")) {
        if (state.line.charAt(0) != '/') {
          if (state.line.contains("=")) {
            assignment(state);
          } else {
            operation(state);
          }
        } else {
          command(state);
        }
      } else {
        emptyLine(state);
      }
      if (state.errorText.equals("")) {
        if (state.activityText.equals("Operation"))
          System.out.println(state.result);
      } else {
        System.out.println(state.errorText);
      }
    }
    System.out.println("Bye!");
  }

  private static void assignment(calcState state) {
    state.activityText = "Assignment";
    state.line = state.line.replaceAll("\\s+", "");
    String[] assignment = state.line.split("=");

    if (assignment.length == 2) {
      if (assignment[0].matches("^[a-zA-Z]+$")) {

        if (assignment[1].matches("^\\d+$")) {
          state.vars.put(assignment[0], Integer.parseInt(assignment[1]));
        } else if (assignment[1].matches("^[a-zA-Z]+$")) {
          if (state.vars.containsKey(assignment[1])) {
            state.vars.put(assignment[0], state.vars.get(assignment[1]));
          }
        } else {
          state.errorText = "Invalid assignment";
        }
      } else {
        state.errorText = "Invalid identifier";
      }
    } else {
      state.errorText = "Invalid assignment";
    }
  }

  private static void command(calcState state) {
    if (state.line.equals("/exit")) {
      state.activityText = "Exit command";
    } else {
      state.errorText = "Unknown command";
    }
  }

  private static String simplifyOp(String op) {
    if (op.charAt(0) == '+') {
      return "+";
    }
    if (op.charAt(0) == '-') {
      if (op.length() % 2 == 0) {
        return "+";
      } else return "-";
    }
    return "";
  }

  private static void operation(calcState state) {
    state.activityText = "Operation";
    state.line = state.line.replaceAll("\\s+", " ");
    String[] strArr = state.line.split(" ");
    if (strArr[0].matches("^([+\\-])?(\\d+)$") ||
      strArr[0].matches("^[a-zA-Z]+$")) {
      if (strArr[0].matches("^([+\\-])?(\\d+)$")) {
        state.result = Integer.parseInt(strArr[0]);
      } else {  // if (strArr[0].matches("^[a-zA-Z]+$"))
        if (state.vars.containsKey(strArr[0])) {
          state.result = state.vars.get(strArr[0]);
        } else {
          state.errorText = "Unknown variable";
        }
      }
      for (int i = 1; i < strArr.length; i += 2) {
        if (i + 1 < strArr.length) {
          if (strArr[i + 1].matches("^([+\\-])?(\\d+)$")) {
            state.result = calculate(state, simplifyOp(strArr[i]), Integer.parseInt(strArr[i + 1]));
          } else {  // if (strArr[0].matches("^[a-zA-Z]+$"))
            state.result = calculate(state, simplifyOp(strArr[i]), state.vars.get(strArr[i + 1]));
          }
        } else {
          state.errorText = "Invalid expression";
          break;
        }
      }
    } else {
      state.errorText = "Invalid expression";
    }
  }

  private static int calculate(calcState state, String op, int nextNum) {
    if (op.equals("+")) {
      return state.result + nextNum;
    }
    if (op.equals("-")) {
      return state.result - nextNum;
    }
    return -1;
  }

  static void emptyLine(calcState state) {
    state.activityText = "Empty Line";
  }

  static class calcState {
    String activityText;
    String errorText;
    String line;
    int result;
    HashMap<String, Integer> vars;

    calcState() {
      activityText = "";
      errorText = "";
      line = "";
      result = 0;
      vars = new HashMap<>();
    }

    void reset() {
      activityText = "";
      errorText = "";
      result = 0;
    }
  }
}
