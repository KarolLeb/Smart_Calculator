package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

class calcState {
  int result;
  HashMap<String, Integer> vars;
  private String errorText;
  private String line;
  private String activityText;

  calcState() {
    activityText = "";
    errorText = "";
    line = "";
    result = 0;
    vars = new HashMap<>();
  }

  String getActivityText() {
    return activityText;
  }

  void setActivityText(String text) {
    activityText = text;
  }


  void cutFirstChar() {
    line = line.substring(1);
  }

  void cutWhiteChars() {
    line = line.replaceAll("\\s+", "");
  }

  void reset() {
    activityText = "";
    errorText = "";
    result = 0;
  }

  public String getErrorText() {
    return errorText;
  }

  public void setErrorText(String errorText) {
    this.errorText = errorText;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }
}

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    calcState state = new calcState();

    while (!state.getActivityText().equals("Exit command")) {
      state.reset();
      state.setLine(scanner.nextLine());
      state.cutWhiteChars();
      if (!state.getLine().equals("")) {
        if (state.getLine().charAt(0) != '/') {
          if (state.getLine().contains("=")) {
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
      if (state.getErrorText().equals("")) {
        if (state.getActivityText().equals("Operation"))
          System.out.println(state.result);
        if (state.getActivityText().equals("Help command"))
          helpDisplay();
      } else {
        System.out.println(state.getErrorText());
      }
    }
    System.out.println("Bye!");
  }

  private static void helpDisplay() {
    System.out.println("/exit to exit");
    System.out.println("+...+ = +");
    System.out.println("-- = +");
  }

  private static void assignment(calcState state) {
    state.setActivityText("Assignment");
    String[] assignment = state.getLine().split("=");

    if (assignment.length == 2) {
      if (isIdentifier(assignment[0])) {
        if (isNumber(assignment[1])) {
          state.vars.put(assignment[0], Integer.parseInt(assignment[1]));
        } else if (isIdentifier(assignment[1])) {
          if (state.vars.containsKey(assignment[1])) {
            state.vars.put(assignment[0], state.vars.get(assignment[1]));
          } else {
            state.setErrorText("Invalid assignment");
          }
        } else {
          state.setErrorText("Invalid assignment");
        }
      } else {
        state.setErrorText("Invalid identifier");
      }
    } else {
      state.setErrorText("Invalid assignment");
    }
  }

  private static void command(calcState state) {
    if (state.getLine().equals("/exit")) {
      state.setActivityText("Exit command");
    } else if (state.getLine().equals("/help")) {
      state.setActivityText("Help command");
    } else {
      state.setErrorText("Unknown command");
    }
  }

  private static void operation(calcState state) {
    state.setActivityText("Operation");
    ArrayList<String> list = new ArrayList<>();
    makePostfix(state, list);
    calculateResult(state, list);
  }

  private static void makePostfix(calcState state, ArrayList<String> list) {
    Stack<String> rpn = new Stack<>();
    while (state.getLine().length() > 0) {
      if (state.getLine().charAt(0) == ('(')) {
        rpn.push("(");
        state.cutFirstChar();
      } else if (state.getLine().charAt(0) == (')')) {
        while (!"(".equals(rpn.peek())) {
          list.add(rpn.pop());
        }
      } else {
        String[] strArray = state.getLine().split("(?=-) | (?=/+) |" +
          " (?='/(') | (?='/)') | (?=/*) | (?='//') | (?=^)", 2);
        if (isNumber(strArray[0]) || isIdentifier(strArray[0])) {
          list.add(strArray[0]);
          int opLength = longOperator(strArray[1]);
          if (opLength > 1) {
            if (strArray[1].equals("+")) {
              pushOperator(list, rpn, "+");
            } else if (strArray[1].equals("-")) {
              if (opLength % 2 == 0) {
                pushOperator(list, rpn, "+");
              } else {
                pushOperator(list, rpn, "-");
              }
            } else {
              state.setErrorText("Invalid expression");
            }
          } else {
            pushOperator(list, rpn, Character.toString(strArray[1].charAt(0)));
          }
          state.setLine(state.getLine().substring(opLength));
        } else {
          state.setErrorText("Invalid expression");
        }
      }
    }
    while (rpn.size() > 0) {
      if (isOperator(rpn.peek())) {
        list.add(rpn.pop());
      }
      if (isParenthesis(rpn.peek())) {
        if (rpn.peek().equals(")")) {
          state.setErrorText("Invalid expression");
        } else {
          rpn.pop();
        }
      }
    }
  }

  private static void calculateResult(calcState state, ArrayList<String> list) {
    Stack<String> rpn = new Stack<>();
//...
  }

  private static int longOperator(String str) {
    int i = 1;
    while (str.charAt(0) == str.charAt(i)) {
      i++;
    }
    return i;
  }

  private static void pushOperator(ArrayList<String> list, Stack<String> rpn, String op) {
    if (rpn.isEmpty() || rpn.peek().equals("(")) {
      rpn.push(op);
    } else if (precedence(rpn.peek()) < precedence(op)) {
      rpn.push(op);
    } else {
      while (precedence(rpn.peek()) >= precedence(op)) {
        list.add(rpn.pop());
      }
      rpn.push(op);
    }
  }

  private static boolean isParenthesis(String str) {
    return "(".equals(str) || ")".equals(str);
  }

  private static boolean isOperator(String str) {
    return ("+".equals(str)) || ("-".equals(str)) || ("*".equals(str)) || ("^".equals(str)) || ("/".equals(str));
  }

  private static boolean isIdentifier(String str) {
    return str.matches("^[a-zA-Z]+$");
  }

  private static boolean isNumber(String str) {
    return str.matches("^-?\\d+$");
  }

  static void emptyLine(calcState state) {
    state.setActivityText("Empty Line");
  }

  static int precedence(String op) {
    if (op.equals("+") || op.equals("-")) {
      return 0;
    }
    if (op.equals("/") || op.equals("*")) {
      return 1;
    }
    if (op.equals("^")) {
      return 2;
    }
    return -1;
  }
}