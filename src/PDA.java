import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;

public class PDA {
    private static PDA instance;

    // new linked list which contains all commands

    private ArrayList<Translator.Instruction> program;
    private LinkedList<String> stack;
    private HashMap<String, Integer> labelList = new HashMap<String, Integer>();
    private String register[];
    private LinkedList<symbolElement> symbolTable = new LinkedList<>();

    public class symbolElement {
        public String name;
        public String value;
        public String label;
    }

    /*
     * public PDA() {
     *
     * stack = new LinkedList<>(); }
     */
    public PDA(ArrayList<Translator.Instruction> program) {

        stack = new LinkedList<>();
        register = new String[140];
        this.program = program;
    }

    private void label(String labelName, int commandLine) {
        this.labelList.put(labelName, commandLine);
    }

    private int goTo(String labelName) {
        return labelList.get(labelName);
    }

    private int goTrue(String labelName) {
        if (Integer.parseInt(this.pop()) == 0) {
            return labelList.get(labelName);
        } else
            return -1;
    }

    private int goFalse(String labelName) {
        if (Integer.parseInt(this.pop()) != 0) {
            return labelList.get(labelName);
        } else
            return -1;
    }

    // ggf. private setzten? ggf. nicht benötigt
    public String pop() {
        return stack.pop();
    }

    public void load(String element) {
        stack.push(element);
    }

    // to load integers
    public void load(Integer element) {
        stack.push(element.toString());
    }

    // momentan ncht verwendet
    public LinkedList<String> getStack() {
        return this.stack;
    }

    // momentan nicht verwednet
    /*
     * public static PDA getInstance() { if (PDA.instance == null) { PDA.instance =
     * new PDA(); }
     *
     * return PDA.instance; }
     */

    public void add() {
        this.load(Integer.parseInt(this.pop()) + Integer.parseInt(this.pop()));
    }

    public void sub() {
        this.load(Integer.parseInt(this.pop()) - Integer.parseInt(this.pop()));
    }

    public void mul() {
        this.load(Integer.parseInt(this.pop()) * Integer.parseInt(this.pop()));
    }

    public void div() {
        this.load(Integer.parseInt(this.pop()) / Integer.parseInt(this.pop()));
    }

    private void printElement(Integer element) {
        System.out.println(element);
    }

    public void move(String labelName) {
        this.load(labelName);
    }

    public int moveToStack() {
        return goTo(this.pop());
    }

    public int goToStack() {
        return goTo(this.pop());
    }

    public void compare(String comperator) {
        switch (comperator) {
        // comparing element 1 (loaded first) to element 2 (loaded most recent)
        case ">":
            if (Integer.parseInt(this.pop()) < Integer.parseInt(this.pop())) {
                this.load(1);
            } else
                this.load(0);
            break;
        case "<":
            if (Integer.parseInt(this.pop()) > Integer.parseInt(this.pop())) {
                this.load(1);
            } else
                this.load(0);
            break;
        case "==":
            if (this.pop().equals(this.pop())) {
                this.load(1);
            } else
                this.load(0);
            break;
        }

    }

    public void addSymbol(String name, String value) {
        symbolElement element = new symbolElement();
        element.name = name;
        element.value = value;
        symbolTable.add(element);
    }

    public void addSymbol(String name, String value, String label) {
        symbolElement element = new symbolElement();
        element.name = name;
        element.value = value;
        element.label = label;
        symbolTable.add(element);
    }

    public int getSymbolIndex(String name) {
        for (symbolElement elements : symbolTable) {
            if (elements.name.equals(name))
                return symbolTable.indexOf(elements);
        }
        return -1;
    }

    public void printSymbolTable() {
        for (symbolElement elements : symbolTable) {
            System.out.println(elements.name + " " + elements.value + " " + "elements.lable");
        }
    }

    // removes and prints an element from the stack.
    public void out() {
        System.out.println(this.pop());
    }

    public void popRegister(String registerAdress) {
        register[Integer.parseInt(registerAdress)] = this.pop();
    }

    public void run() {
        int i = 0;
        // create Labels
        while (i < program.size()) {
            if (program.get(i).getCommand().equals("LABEL")) {
                i = execute(program.get(i), i);
            } else
                i++;
        }

        // run the actual program
        int j = 0;
        while (j < program.size()) {
            j = execute(program.get(j), j);
        }
        System.out.println("Programm completed");
    }

    private int execute(Translator.Instruction Instruction, int currentPosition) {
        String command = Instruction.getCommand();
        Integer ret = -1;
        switch (command) {
        case "ADD":
            this.add();
            break;
        case "SUB":
            this.sub();
            break;
        case "MUL":
            this.mul();
            break;
        case "DIV":
            this.div();
            break;
        case "LOAD":
            this.load(Integer.parseInt(Instruction.getPayload()));
            break;
        case "LABEL":
            this.label(Instruction.getPayload(), currentPosition);
            break;
        case "GOTO":
            ret = this.goTo(Instruction.getPayload());
            break;
        case "GOTRUE":
            ret = this.goTrue(Instruction.getPayload());
            break;
        case "GOFALSE":
            ret = this.goFalse(Instruction.getPayload());
            break;
        case "HALT":
            ret = program.size(); // Jumps beyond the end of the programm thus ending the while loop used in run
            break;
        case "MOVE":
            this.move(Instruction.getPayload());
            break;
        case "GOTOSTACK":
            ret = this.goToStack();
            break;
        case "POP":
            this.pop();
            break;
        case "POPR":
            this.popRegister(Instruction.getPayload());
            break;
        case "COMPARE":
            this.compare(Instruction.getPayload());
            break;
        case "OUT":
            this.out();
            break;
        case "ADDSYMBOL":
            this.addSymbol(Instruction.getNamePayload(), Instruction.getPayload(), Instruction.getLabelPayload());
            break;
        case "LOADSYMBOLVALUE":
            this.load(symbolTable.get(getSymbolIndex(Instruction.getPayload())).value);
            break;
        case "LOADSYMBOLLABEL":
            this.load(symbolTable.get(getSymbolIndex(Instruction.getPayload())).label);
            break;
        }
        if (ret == -1) {
            return currentPosition + 1;
        } else {
            return ret;
        }
    }

    public void outputList(ArrayList<Translator.Instruction> program) {
        for (Translator.Instruction elements : program) {
            System.out.println("C:" + elements.getCommand() + "P: " + elements.getPayload());
        }
    }

    public void outputHashmap() {
        for (String name : labelList.keySet()) {

            String key = name.toString();
            String value = labelList.get(name).toString();
            System.out.println(key + " " + value);
        }
    }
}
