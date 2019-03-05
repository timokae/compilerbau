import java.util.LinkedList;

public class PDA {
    private static PDA instance;

    private LinkedList<Integer> stack;

    private PDA() {
        stack = new LinkedList<>();
    }

    public int pop() {
        return stack.pop();
    }

    public void push(int element) {
        stack.push(element);
    }

    public LinkedList<Integer> getStack() {
        return this.stack;
    }

    public static PDA getInstance() {
        if (PDA.instance == null) {
            PDA.instance = new PDA();
        }

        return PDA.instance;
    }

    public static void evaluateNumber(SyntaxTree sT) {
        PDA pda = PDA.getInstance();

        if (sT.getTokenString().equals("INPUT_SIGN")) {
            int number = Character.getNumericValue(sT.getCharacter());
            pda.push(number);
        }
    }

    public static boolean evaluate(SyntaxTree sT) {
        PDA pda = PDA.getInstance();

        SyntaxTree inputSign = null;
        for(SyntaxTree s : sT.getChildNodes()) {
            if (s.getTokenString().equals("INPUT_SIGN")) {
                inputSign = s;
            } else {
                PDA.evaluate(s);
            }
        }
        if (inputSign != null) {
            PDA.evaluate(inputSign);
        }


        if (sT.getTokenString().equals("INPUT_SIGN")) {
            char sign = sT.getCharacter();

            int a;
            int b;
            switch (sign) {
                case '*':
                    a = pda.pop();
                    b = pda.pop();
                    pda.push(a * b);
                    break;
                case '/':
                    break;
                case '+':
                    a = pda.pop();
                    b = pda.pop();
                    pda.push(a + b);
                    break;
                case '-':
                    a = pda.pop();
                    b = pda.pop();
                    pda.push(b - a);
                    break;
                case '(':
                    break;
                case ')':
                    break;
                default: // char := number
                    pda.push(Character.getNumericValue(sign));
            }
        }

        return false;
    }
}
