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

    public static void traverse(SyntaxTree sT) {
        Translator translator = Translator.getInstance();

        boolean isTermNode = Translator.isOperatorNode(sT);

        // Numbers > 9 consist of several nodes
        if (sT.getTokenString().equals("NUMBER")) {
            int[] numbers = Translator.traverserNumber(sT, new int[0]);
            translator.addInstruction("LOAD", arrayToNumber(numbers));
        } else if (isTermNode) {
            // first traverse left side
            Translator.traverse(sT.getChildNodes().get(1));

            // then push operator onto stack
            Translator.traverse(sT.getChildNodes().get(0));

            // at the end traverse right right
            for(int i = 2; i < sT.getChildNodes().size(); i++) {
                Translator.traverse(sT.getChildNodes().get(i));
            }
        } else {
            for(SyntaxTree s : sT.getChildNodes())
                Translator.traverse(s);
        }

        if (sT.getTokenString().equals("INPUT_SIGN")) {
            char sign = sT.getCharacter();

            switch (sign) {
                case '*':
                    translator.addInstruction("MULT");
                    break;
                case '/':
                    translator.addInstruction("DIV");
                    break;
                case '+':
                    translator.addInstruction("ADD");
                    break;
                case '-':
                    translator.addInstruction("SUB");
                    break;
                case '(':
                    break;
                case ')':
                    break;
                default: // char := number
                    break;
            }
        }
    }

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

        if (sT.getChildNodes().size() > 1) {
            return Translator.traverserNumber(sT.getChildNodes().get(1), newNumbers);
        }

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

    private static boolean isOperatorNode(SyntaxTree tree) {
        // node needs to be a term or right_expression
        boolean isTerm = tree.getTokenString().equals("INPUT_SIGN") || tree.getTokenString().equals("RIGHT_EXPRESSION");

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
