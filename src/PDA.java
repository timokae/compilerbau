import java.util.ArrayList;
import java.util.LinkedList;

public class PDA {
    private static PDA instance;

    private LinkedList<String> stack;

    private PDA() {
        stack = new LinkedList<>();
    }

    public String pop() {
        return stack.pop();
    }

    public void push(String element) {
        stack.push(element);
    }

    public LinkedList<String> getStack() {
        return this.stack;
    }

    public static PDA getInstance() {
        if (PDA.instance == null) {
            PDA.instance = new PDA();
        }

        return PDA.instance;
    }

    public static boolean evaluate(SyntaxTree sT) {
        PDA pda = PDA.getInstance();

        ArrayList<SyntaxTree> inputSigns = new ArrayList<>();
        for(SyntaxTree s : sT.getChildNodes()) {
            if (s.getTokenString().equals("INPUT_SIGN")) {
                inputSigns.add(s);
            } else {
                PDA.evaluate(s);
            }
        }

        for(SyntaxTree s : inputSigns) {
            PDA.evaluate(s);
        }


        if (sT.getTokenString().equals("INPUT_SIGN")) {
            char sign = sT.getCharacter();

            String a;
            String b;
            int result;

            switch (sign) {
                case '*':
                    a = pda.pop();
                    b = pda.pop();
                    System.out.println(b + " * " + a);
                    result = Integer.parseInt(b) * Integer.parseInt(a);
                    pda.push(String.valueOf(result));
                    break;
                case '/':
                    a = pda.pop();
                    b = pda.pop();
                    System.out.println(b + " / " + a);
                    result = Integer.parseInt(b) * Integer.parseInt(a);
                    pda.push(String.valueOf(result));
                    break;
                case '+':
                    a = pda.pop();
                    b = pda.pop();
                    System.out.println(b + " + " + a);
                    result = Integer.parseInt(b) + Integer.parseInt(a);
                    pda.push(String.valueOf(result));
                    break;
                case '-':
                    a = pda.pop();
                    b = pda.pop();
                    System.out.println(b + " - " + a);
                    result = Integer.parseInt(b) - Integer.parseInt(a);
                    pda.push(String.valueOf(result));
                    break;
                case '(':
                    pda.push("(");
                    break;
                case ')':
                    a = pda.pop();
                    if (!a.equals("(")) {
                        pda.stack.clear();
                        System.out.println("Wrong paranthesis");
                    }
                    break;
                default: // char := number
                    pda.push(Character.toString(sign));
            }

        }

        return false;
    }
}
