import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Translator {

    public class Instruction {
        private String command;
        private String payload;

        public Instruction(String command, String payload) {
            this.command = command;
            this.payload = payload;
        }

        public Instruction(String command) {
            this.command = command;
        }

        public String getCommand() {
            return this.command;
        }

        public String getPayload() {
            return this.payload;
        }
    }

    private static Translator instance;
    private ArrayList<Instruction> instructions;

    private Translator() {
        this.instructions = new ArrayList<>();
    }

    public void addInstruction(String command, String payload) {
        instructions.add(new Instruction(command, payload));
    }

    public void addInstruction(String command) {
        instructions.add(new Instruction(command));
    }

    public ArrayList<Instruction> getInstructions() {
        return this.instructions;
    }

    public static Translator getInstance() {
        if (Translator.instance == null) {
            Translator.instance = new Translator();
        }

        return Translator.instance;
    }

    public static void addNumberToStack(SyntaxTree sT) {
        // Read all numbers from children and add into one number
        //int[] numbers = Translator.traverserNumber(sT, new int[0]);
        int number = Integer.valueOf(sT.getChildNodes().getFirst().getCharacter());

        // Add number to stack as instruction
        Translator.getInstance().addInstruction("LOAD", String.valueOf(number));
    }

    public static void addOperatorToStack(SyntaxTree sT) {
        String sign = sT.getCharacter();

        switch (sign) {
            case "*":
                Translator.getInstance().addInstruction("MULT");
                break;
            case "/":
                Translator.getInstance().addInstruction("DIV");
                break;
            case "+":
                Translator.getInstance().addInstruction("ADD");
                break;
            case "-":
                Translator.getInstance().addInstruction("SUB");
                break;
            case "(":
                break;
            case ")":
                break;
            default: // char := number
                break;
        }
    }

    public static void addConditionToStack(SyntaxTree sT) {
        String comp1 = Translator.getFirstComp(sT);
        String comp2 = Translator.getSecondComp(sT);
        String comparator = Translator.getComparator(sT);

        Translator.getInstance().addInstruction("LOAD", comp1);
        Translator.getInstance().addInstruction("LOAD", comp2);
        Translator.getInstance().addInstruction("COMPARE", comparator);
        Translator.getInstance().addInstruction("GOFALSE", "out");
        Translator.traverse(sT.getChild(2));
        Translator.getInstance().addInstruction("LABEL", "out");
    }

    public static String getFirstComp(SyntaxTree sT) {
        return sT.getChild(1).getChild(0).getChild(0).getCharacter();
    }

    public static String getSecondComp(SyntaxTree sT) {
        return sT.getChild(1).getChild(1).getChild(1).getChild(0).getChild(0).getChild(0).getCharacter();
    }

    public static String getComparator(SyntaxTree sT) {
        return sT.getChild(1).getChild(1).getChild(0).getCharacter();
    }

    public static void traverse(SyntaxTree sT) {

        boolean isConditionNode = Translator.isConditionNode(sT);
        boolean isTermNode = Translator.isOperatorNode(sT);

        if (isConditionNode) {
            Translator.addConditionToStack(sT);
        } else if (sT.getTokenString().equals("NUMBER")) { // Numbers > 9 consist of several nodes
            Translator.addNumberToStack(sT);
        } else if (isTermNode) { // Node has an expression/term and has an operator
            Translator.traverse(sT.getChildNodes().get(1)); // first traverse left side
            Translator.traverse(sT.getChildNodes().get(0)); // then push operator onto stack

            // at the end traverse right right
            for(int i = 2; i < sT.getChildNodes().size(); i++) {
                Translator.traverse(sT.getChildNodes().get(i));
            }
        }
        // No specific case found
        else {
            for(SyntaxTree s : sT.getChildNodes()) {
                Translator.traverse(s);
            }
        }

        // INPUT_SIGN reached that is not a number
        if (sT.getTokenString().equals("INPUT_SIGN")) {
            addOperatorToStack(sT);
        }
    }

    // Returns true if the node has a operator sign and child with an input sign
    private static boolean isOperatorNode(SyntaxTree tree) {
        // node needs to be a term or right_expression
        boolean isTerm = tree.getTokenString().equals("TERM") || tree.getTokenString().equals("RIGHT_EXPRESSION");

        // node needs a 'input_sign' child
        boolean childHasInputSign = false;
        for(SyntaxTree s : tree.getChildNodes()) {
            if(s.getTokenString().equals("INPUT_SIGN")) {
                childHasInputSign = true;
                break;
            }
        }

        return isTerm && childHasInputSign;
    }

    private static boolean isConditionNode(SyntaxTree tree) {
        if (tree.getChildNodes().size() > 0) {
            boolean isTerm = tree.getTokenString().equals("TERM");
            boolean isCondition = tree.getChild(0).getCharacter().equals("if");

            return isTerm && isCondition;
        }

        return false;
    }
}

