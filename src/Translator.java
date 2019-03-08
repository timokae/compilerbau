import java.util.ArrayList;

public class Translator {

    public class Instruction {
        private String command;
        private Integer payload;

        public Instruction(String command, Integer payload) {
            this.command = command;
            this.payload = payload;
        }

        public Instruction(String command) {
            this.command = command;
        }

        public String getCommand() {
            return this.command;
        }

        public Integer getPayload() {
            return this.payload;
        }
    }

    private static Translator instance;
    private ArrayList<Instruction> instructions;

    private Translator() {
        this.instructions = new ArrayList<>();
    }

    public void addInstruction(String command, int payload) {
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
        int[] numbers = Translator.traverserNumber(sT, new int[0]);

        // Add number to stack as instruction
        Translator.getInstance().addInstruction("LOAD", arrayToNumber(numbers));
    }

    public static void addOperatorToStack(SyntaxTree sT) {
        char sign = sT.getCharacter();

        switch (sign) {
            case '*':
                Translator.getInstance().addInstruction("MULT");
                break;
            case '/':
                Translator.getInstance().addInstruction("DIV");
                break;
            case '+':
                Translator.getInstance().addInstruction("ADD");
                break;
            case '-':
                Translator.getInstance().addInstruction("SUB");
                break;
            case '(':
                break;
            case ')':
                break;
            default: // char := number
                break;
        }
    }

    public static void traverse(SyntaxTree sT) {
        boolean isTermNode = Translator.isOperatorNode(sT);

        // Numbers > 9 consist of several nodes
        if (sT.getTokenString().equals("NUMBER")) {
            Translator.addNumberToStack(sT);
        }
        // Node has an expression/term and has an operator
        else if (isTermNode) {
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

    /*
        Number (current Node)
        |-OPERATOR
            |---NUMBER
                |---DIGIT
                   |---INPUT_SIGN:5
               |---NUMBER
                  |---DIGIT
                     |---INPUT_SIGN:0
                 |---NUMBER
                     ---DIGIT
                     ---INPUT_SIGN:0

         Read number of current node and insert into array
         Traverse through all children and add them to the array
    */
    public static int[] traverserNumber(SyntaxTree sT, int[] numbers) {
        // Init array with length + 1
        int[] newNumbers = new int[numbers.length + 1];
        int numberIndex = 0;

        // Copy old number into new array
        for(int number : numbers) {
            newNumbers[numberIndex] = number;
            numberIndex++;
        }

        // Insert current number to array
        int nodeNumber = Character.getNumericValue(sT.getChildNodes().get(0).getChildNodes().get(0).getCharacter());
        newNumbers[numberIndex] = nodeNumber;

        // If node has children iterave over them recursively
        if (sT.getChildNodes().size() > 1) {
            return Translator.traverserNumber(sT.getChildNodes().get(1), newNumbers);
        }

        // No children -> return array
        return newNumbers;
    }

    private static int arrayToNumber(int[] numbers) {
        int result = 0;
        int decimalPlace = 1;

        for(int i = numbers.length-1; i >= 0; i--) {
            result += numbers[i] * decimalPlace;
            decimalPlace *= 10;
        }

        return result;
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
}
