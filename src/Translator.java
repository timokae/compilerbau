import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Translator {

    public class Instruction {
        private String command;
        private String payload;
        private String labelPayload;
        private String namePayload;

        public Instruction(String command, String payload) {
            this.command = command;
            this.payload = payload;
        }
        public Instruction(String command, String payload,String labelPayload,String namePayload) {
            this.command = command;
            this.payload = payload;
            this.labelPayload = labelPayload;
            this.namePayload = namePayload;
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

        public String getLabelPayload(){return this.labelPayload;}
        public String getNamePayload() {return this.namePayload;}
    }

    private static Translator instance;
    public ArrayList<Instruction> instructions;

    private Translator() {
        this.instructions = new ArrayList<>();
    }

    public void addInstruction(String command, String payload) {
        instructions.add(new Instruction(command, payload));
    }
    public void addInstruction(String command,String payLoad, String labelPayload,String namePayload){ instructions.add(new Instruction(command,payLoad,labelPayload,namePayload));}

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

    public static void addIfToStack(SyntaxTree sT) {
        String comp1 = Translator.getFirstComp(sT.getChild(0));
        String comp2 = Translator.getSecondComp(sT.getChild(0));
        String comparator = Translator.getComparator(sT.getChild(0));


        Translator.getInstance().addInstruction("LOAD", comp1);
        Translator.getInstance().addInstruction("LOAD", comp2);
        //Compare liefert 0 für true
        Translator.getInstance().addInstruction("COMPARE", comparator);
        Translator.getInstance().addInstruction("GOTRUE", "out");
        Translator.traverse(sT.getChild(1));
        Translator.getInstance().addInstruction("OUT");
        Translator.getInstance().addInstruction("LABEL", "out");
    }

    public static void addWhileToStack(SyntaxTree sT) {
        String comp1 = Translator.getFirstComp(sT);
        String comp2 = Translator.getSecondComp(sT);
        String comparator = Translator.getComparator(sT);

        Translator.getInstance().addInstruction("LABEL", "test");
        Translator.getInstance().addInstruction("LOAD", comp1);
        Translator.getInstance().addInstruction("LOAD", comp2);
        Translator.getInstance().addInstruction("COMPARE", comparator);
        Translator.getInstance().addInstruction("GOTRUE", "out");
        Translator.traverse(sT.getChild(2));
        Translator.getInstance().addInstruction("GOTO test");
        Translator.getInstance().addInstruction("LABEL", "out");
        Translator.getInstance().addInstruction("HALT");

    }

    public static void addDefineToStack(SyntaxTree tree) {
        String symbol = tree.getChild(0).getChild(0).getCharacter();
        Translator.traverse(tree.getChild(1));

        Translator.getInstance().addInstruction("ADDSYMBOL", "", "null", symbol);
    }

    public static void addAssignToStack(SyntaxTree tree) {
        Translator.addDefineToStack(tree);
    }

    public static void addCallToStack(SyntaxTree tree) {
        String functionName = tree.getChild(0).getCharacter();
        String parameter = tree.getChild(1).getCharacter();

        Translator.getInstance().addInstruction("ADDSYMBOL", parameter, "back1", functionName + "1");
        Translator.getInstance().addInstruction("LOADSYMBOLLABEL", functionName + "1");
        Translator.getInstance().addInstruction("GOTO", functionName);
        Translator.getInstance().addInstruction("LABEL", "back1");
    }

    public static void startTraverse(SyntaxTree tree) {
        String param = tree.getChild(0).getChild(0).getCharacter();
        System.out.println("PARAM: " + param);
        Translator.traverse(tree.getChild(1));
    }

    public static void traverse(SyntaxTree sT) {
        boolean isTermNode = Translator.isOperatorNode(sT);

        if (isDefine(sT)) {
            Translator.addDefineToStack(sT);
        }
        else if (isAssign(sT)) {
            Translator.addAssignToStack(sT);
        }
        else if (isCall(sT)) {
            Translator.addCallToStack(sT);
        }
        else if (isIfCondition(sT)) {
            Translator.addIfToStack(sT);
        }
        else if (isWhileLoop(sT)) {
            Translator.addWhileToStack(sT);
        }
        else if (sT.getTokenString().equals("NUMBER")) { // Numbers > 9 consist of several nodes
            Translator.addNumberToStack(sT);
        }
        else if (isTermNode) { // Node has an expression/term and has an operator
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
        return tree.getToken() == TokenList.TERM;
    }

    // Node Matcher

    private static boolean isDefine(SyntaxTree tree) {
        return tree.getToken() == TokenList.DEFINE;
    }

    private static boolean isAssign(SyntaxTree tree) {
        return tree.getToken() == TokenList.ASSIGN;
    }

    private static boolean isCall(SyntaxTree tree) {
        return tree.getToken() == TokenList.CALL;
    }

    private static boolean isIfCondition(SyntaxTree tree) {
        return tree.getToken() == TokenList.IF;
    }

    private static boolean isWhileLoop(SyntaxTree tree) {
        return tree.getToken() == TokenList.WHILE;
    }

    // Helper Functions

    private static String getFirstComp(SyntaxTree sT) {
        // From comparision node
        return sT.getChild(0).getChild(0).getCharacter();
    }

    private static String getSecondComp(SyntaxTree sT) {
        return sT.getChild(2).getChild(0).getCharacter();
    }

    private static String getComparator(SyntaxTree sT) {
        return sT.getChild(1).getCharacter();
    }
}
