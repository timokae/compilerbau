import javax.swing.*;
import javax.xml.crypto.dsig.TransformService;
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
    private int instructionIndex = 0;

    private Translator() {
        this.instructions = new ArrayList<>();
    }

    //-------------------------------------------------------------------------
    //-------------------Add Instruction Functions-----------------------------
    //-------------------------------------------------------------------------

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

    //-------------------------------------------------------------------------
    //-------------------Add To Stack Functions--------------------------------
    //-------------------------------------------------------------------------

    public static void addValueToStack(SyntaxTree sT) {
        String value = sT.getCharacter();
        Translator.getInstance().addInstruction("LOAD", value);
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
            default:
                break;
        }
    }

    public static void addIfToStack(SyntaxTree sT) {
        String comp1 = Translator.getFirstComp(sT.getChild(0));
        String comp2 = Translator.getSecondComp(sT.getChild(0));
        String comparator = Translator.getComparator(sT.getChild(0));

        String index = String.valueOf(Translator.getInstance().getInstructionIndex());

        Translator.getInstance().addInstruction("LOAD", comp1);
        Translator.getInstance().addInstruction("LOAD", comp2);
        //Compare liefert 0 für true
        Translator.getInstance().addInstruction("COMPARE", comparator);
        Translator.getInstance().addInstruction("GOTRUE", "out" + index);
        Translator.traverse(sT.getChild(1));
        //Translator.getInstance().addInstruction("OUT");
        Translator.getInstance().addInstruction("LABEL", "out" + index);
    }

    public static void addWhileToStack(SyntaxTree sT) {
        String comp1 = Translator.getFirstComp(sT.getChild(0));
        String comp2 = Translator.getSecondComp(sT.getChild(0));
        String comparator = Translator.getComparator(sT.getChild(0));

        String index = String.valueOf(Translator.getInstance().getInstructionIndex());
        // RENAME test -> while1
        Translator.getInstance().addInstruction("LABEL", "while" + index);
        Translator.getInstance().addInstruction("LOAD", comp1);
        Translator.getInstance().addInstruction("LOAD", comp2);
        Translator.getInstance().addInstruction("COMPARE", comparator);
        Translator.getInstance().addInstruction("GOTRUE", "out" + index);
        Translator.traverse(sT.getChild(1));
        Translator.getInstance().addInstruction("GOTO",  "while" + index);
        Translator.getInstance().addInstruction("LABEL", "out" + index);
    }

    public static void addDefineToStack(SyntaxTree tree) {
        String symbol = tree.getChild(0).getChild(0).getCharacter();

        SyntaxTree child = tree.getChild(1);

        if (child.getToken() == TokenList.TERM) {
            Translator.traverseTerm(tree.getChild(1));
        } else {
            Translator.getInstance().addInstruction("LOAD", child.getChild(0).getCharacter());
        }

        Translator.getInstance().addInstruction("ADDSYMBOL", "", "null", symbol);
    }

    public static void addAssignToStack(SyntaxTree tree) {
        String symbol = tree.getChild(0).getChild(0).getCharacter();

        SyntaxTree child = tree.getChild(1);

        if (child.getToken() == TokenList.TERM) {
            Translator.traverseTerm(tree.getChild(1));
        } else {
            Translator.getInstance().addInstruction("LOAD", child.getChild(0).getCharacter());
        }

        Translator.getInstance().addInstruction("CHANGEVALUE", "", "null", symbol);
    }

    public static void addCallToStack(SyntaxTree tree) {
        String functionName = tree.getChild(0).getCharacter();
        String parameter = tree.getChild(1).getCharacter();
        String index = String.valueOf(Translator.getInstance().getInstructionIndex());

        if (functionName.equals("print")) {
            Translator.getInstance().addInstruction("PRINT", parameter);
        } else {
            Translator.getInstance().addInstruction("LOAD", parameter);
            Translator.getInstance().addInstruction("ADDSYMBOL", parameter, "back" + index, functionName + index);
            Translator.getInstance().addInstruction("LOAD", parameter);
            Translator.getInstance().addInstruction("CHANGEVALUE", "", "null", "s");
            Translator.getInstance().addInstruction("LOADSYMBOLLABEL", functionName + index);
            Translator.getInstance().addInstruction("GOTO", functionName);
            Translator.getInstance().addInstruction("LABEL", "back" + index);
        }
    }

    public static void addParameterToStack(SyntaxTree tree) {
        String param = tree.getChild(0).getChild(0).getCharacter();
        Translator.getInstance().addInstruction("ADDPARAM", param);
    }

    public static void startTraverse(SyntaxTree tree, String functionName) {
        //Translator.addParameterToStack(tree);
        if (functionName.equals("main")) {
            Translator.getInstance().addInstruction("LOAD", "0");
            Translator.getInstance().addInstruction("ADDSYMBOL", "", "null", "s");
        }

        Translator.getInstance().addInstruction("LABEL", functionName);

        if (!functionName.equals("main")) {
            Translator.getInstance().addInstruction("LOAD", "s");
        }

        Translator.traverse(tree.getChild(1));

        if (!functionName.equals("main")) {
            Translator.getInstance().addInstruction("POP");
            Translator.getInstance().addInstruction("GOTOSTACK");
        }
        Translator.getInstance().addInstruction("HALT");
    }

    //-------------------------------------------------------------------------
    //-------------------Traverse Functions------------------------------------
    //-------------------------------------------------------------------------

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
        // No specific case found
        else {
            for(SyntaxTree s : sT.getChildNodes()) {
                Translator.traverse(s);
            }
        }
    }

    public static void traverseTerm(SyntaxTree tree) {
        // operator rightTerm
        traverseOperator(tree.getChild(0));
        traverseRightTerm(tree.getChild(1));
    }

    public static void traverseOperator(SyntaxTree tree) {
        // ( term rightExpression )
        if (tree.getChildNodes().size() == 4) {
            traverseTerm(tree.getChild(1));
            traverseRightTerm(tree.getChild(2));
        } else if (tree.getChildNodes().size() == 2) {
            addValueToStack(tree.getChild(0).getChild(0).getChild(0));
            traverseRightTerm(tree.getChild(1));
        }
        else {
            addValueToStack(tree.getChild(0).getChild(0));
        }
    }

    public static void traverseRightTerm(SyntaxTree tree) {
        if (tree.getChild(0).getToken() != TokenList.EPSILON) {
            traverseOperator(tree.getChild(1));
            addOperatorToStack(tree.getChild(0));
            traverseRightTerm(tree.getChild(2));
        }
    }

    //-------------------------------------------------------------------------
    //-------------------Node Matching FUnctions-------------------------------
    //-------------------------------------------------------------------------

    private static boolean hasOperatorSign(SyntaxTree tree) {
        return (
            tree.getToken() == TokenList.PLUS
            || tree.getToken() == TokenList.MINUS
            || tree.getToken() == TokenList.MULT
            || tree.getToken() == TokenList.DIV
        );
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

    private static boolean isTerm(SyntaxTree tree) {
        return tree.getToken() == TokenList.TERM;
    }

    //-------------------------------------------------------------------------
    //-------------------Helper Functions--------------------------------------
    //-------------------------------------------------------------------------

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

    private void incrementInstructionIndex() {
        this.instructionIndex++;
    }


    private int getInstructionIndex() {
        int index = this.instructionIndex;
        this.incrementInstructionIndex();
        return index;
    }
}
