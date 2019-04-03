import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;

public class PDA {
    private static PDA instance;

    //new linked list which contains all commands

    private ArrayList<Translator.Instruction> program;
    private LinkedList<String> stack;
    private HashMap<String, Integer> labelList = new HashMap<String, Integer>();
    private String register[];
    private LinkedList<symbolElement> symbolTable = new LinkedList<>();
    private LinkedList<LinkedList<symbolElement>> symbolTableList = new LinkedList<>();

    public class symbolElement{
        public String name;
        public String value;
        public String label;
    }

    public PDA(ArrayList<Translator.Instruction> program) {
        stack = new LinkedList<>();
        register = new String[140];
        this.program = program;
        //symbolTableList.push(symbolTable);
    }

    private void label(String labelName, int commandLine)
    {
        this.labelList.put (labelName, commandLine);
    }

    private int goTo (String labelName) {
        return labelList.get(labelName);
    }
    private int goTrue(String labelName){
        if (Integer.parseInt(this.pop())==0){
            return labelList.get(labelName);
        }
        else return -1;
    }
    private int goFalse(String labelName){
        if (Integer.parseInt(this.pop())!=0){
            return labelList.get(labelName);
        }
        else return -1;
    }


    public String pop() {
        return stack.pop();
    }

    public void load(String element){
        stack.push(element);
        //for (String s : this.stack) {
        //    System.out.println(s);
        //    System.out.println("---");
        //}
    }
    //to load integers
    public void load(Integer element) {
        stack.push(element.toString());
    }

    // momentan ncht verwendet
    public LinkedList<String> getStack() {
        return this.stack;
    }


    public void add()
    {
        String num1 = substitude(this.pop());
        String num2 = substitude(this.pop());
        this.load(Integer.parseInt(num1)+Integer.parseInt(num2));
    }
    public void sub()
    {
        String num1 = substitude(this.pop());
        String num2 = substitude(this.pop());
        this.load(Integer.parseInt(num2)-Integer.parseInt(num1));
    }
    public void mul()
    {
        String num1 = substitude(this.pop());
        String num2 = substitude(this.pop());
        this.load(Integer.parseInt(num1)*Integer.parseInt(num2));
    }
    public void div()
    {
        String num1 = substitude(this.pop());
        String num2 = substitude(this.pop());
        this.load(Integer.parseInt(num2)/Integer.parseInt(num1));
    }
    public void move(String labelName){
        this.load(labelName);
    }
    public int goToStack(){
        String tmp = this.pop();
        return goTo(tmp);
    }

    public void newSymbolTable(){
        //add the current symbolTable to the list
        LinkedList<symbolElement> symbolTableHelper = new LinkedList<>();
        //copy current working symbolTable (which is symbolTable)

        for (symbolElement x:symbolTable){
            symbolTableHelper.add(x);
        }
        this.symbolTableList.add(symbolTableHelper);
    }
    public void destroySymbolTable(){
        //remove the most recent symbolTable

        LinkedList<symbolElement> symbolTableHelper = new LinkedList<>();
        for (symbolElement x:symbolTableList.getLast()){
            symbolTableHelper.add(x);
        }
        symbolTable.clear();
        //restore previous values to symboltable
        for (symbolElement x:symbolTableHelper){
            symbolTable.add(x);
        }


        this.symbolTableList.removeLast();
    }

    public void compare(String comperator){
        String comp1 = substitude(this.pop());
        String comp2 = substitude(this.pop());

        switch(comperator) {
            //comparing element 1 (loaded first) to element 2 (loaded most recent)
            case ">":
                if (Integer.parseInt(comp1)< Integer.parseInt(comp2)) {
                    this.load(1);
                } else this.load(0);
                break;
            case "<":
                if (Integer.parseInt(comp1)> Integer.parseInt(comp2)) {
                    this.load(1);
                } else this.load(0);
                break;
            case "==":
                if (comp1.equals(comp2)) {
                    this.load(1);
                } else this.load(0);
                break;
        }
    }

    public void addSymbol(String name,String value){
        symbolElement element = new symbolElement();
        element.name = name;
        element.value = value;
        symbolTable.add(element);
    }

    public void addSymbol(String name,String value, String label){
        symbolElement element = new symbolElement();
        element.name = name;
        element.value = value;
        element.label = label;
        symbolTable.add(element);
    }

    public int getSymbolIndex(String name){
        for (symbolElement elements : symbolTable){
            if(elements.name.equals(name))
                return symbolTable.indexOf(elements);
        }
        return -1;
    }

    public void changeValue(Translator.Instruction instruction    ){
        symbolTable.remove(getSymbolIndex(instruction.getNamePayload()));
        this.addSymbol(instruction.getNamePayload(),this.pop(),instruction.getLabelPayload());
    }

    public void changeValue(String name, String value){
        symbolElement  help = symbolTable.get(getSymbolIndex((name)));
        symbolTable.remove((getSymbolIndex(name)));
        help.value = value;
        this.addSymbol(help.name,substitude(help.value),help.label);
    }


    //removes and prints an element from the stack.
    public void out(){
        System.out.println(this.pop());
    }

    public void print(String payload) {
        for (symbolElement elements : symbolTable){
            if (payload.equals(elements.name)){
                payload = elements.value;
            }
        }
        System.out.println(payload);
    }
    public void popRegister(String registerAdress){
        register[Integer.parseInt(registerAdress)]= this.pop();
    }
    public void showstack(){
        for (String s : this.stack) {
            System.out.println(s);
        }
        System.out.println("---");
    }
    public void run(){
        int i = 0;
        //create Labels
        while (i<program.size()){
            //System.out.println(i);
            if (program.get(i).getCommand().equals("LABEL"))
            {
                //System.out.println("label created");
                i = execute(program.get(i),i);

            }else i++;
        }

        //run the actual program
        int j = 0;
        while (j<program.size()){
            j = execute(program.get(j),j);
        }
        System.out.println("Programm completed");
    }
    private int execute(Translator.Instruction Instruction, int currentPosition){
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
            case"DIV":
                this.div();
                break;
            case"LOAD":
                String payload = Instruction.getPayload();
                this.load(substitude(payload));
                //this.showstack();
                break;
            case "LOAD_FUNCTION_NAME":
                String functionName = Instruction.getPayload();
                this.load(functionName);
                break;
            case "LABEL":
                this.label(Instruction.getPayload(),currentPosition);
                break;
            case"GOTO":
                ret = this.goTo(Instruction.getPayload());
                break;
            case"GOTRUE":
                ret = this.goTrue(Instruction.getPayload());
                break;
            case "GOFALSE":
                ret = this.goFalse(Instruction.getPayload());
                break;
            case"HALT":
                ret = program.size(); //Jumps beyond the end of the programm thus ending the while loop used in run
                break;
            case"MOVE":
                this.move(Instruction.getPayload());
                break;
            case"GOTOSTACK":
                ret = this.goToStack();
                break;
            case"POP":
                this.pop();
                break;
            case"POPR":
                this.popRegister(Instruction.getPayload());
                break;
            case"COMPARE":
                this.compare(Instruction.getPayload());
                break;
            case"OUT":
                this.out();
                break;
            case"ADDSYMBOL":
                this.addSymbol(Instruction.getNamePayload(),this.pop(),Instruction.getLabelPayload());
                break;
            case"LOADSYMBOLVALUE":
                this.load(symbolTable.get(getSymbolIndex(Instruction.getPayload())).value);
                break;
            case"LOADSYMBOLLABEL":
                this.load(symbolTable.get(getSymbolIndex(Instruction.getPayload())).label);
                break;
            case"CHANGEVALUE":
                this.changeValue(Instruction);
                //this.printSymbolTable();
                break;
            case"CHANGESTACK":
                String value;
                String target;
                value = this.pop();
                target = this.pop();
                this.changeValue(target,value);
                break;
            case"PRINT":
                this.print(Instruction.getPayload());
                break;
            case"SHOWSTACK":
                this.showstack();
                break;
            case"NEWSYMBOLTABLE":
                this.newSymbolTable();
                break;
            case"DESTROYSYMBOLTABLE":
                this.destroySymbolTable();
                break;

            default:
                System.out.println(command);
        }
        if (ret == -1){
            return currentPosition+1;
        }else {
            return ret;
        }
    }
    public void outputList(ArrayList<Translator.Instruction> program ){
        for (Translator.Instruction elements : program){
            System.out.println("C:" +elements.getCommand()+"P: "+elements.getPayload());
        }
    }
    public void outputHashmap(){
        for (String name: labelList.keySet()){

            String key =name.toString();
            String value = labelList.get(name).toString();
            System.out.println(key + " " + value);
        }
    }
    public void printSymbolTable(){
        for (symbolElement elements : symbolTable){
            System.out.println(elements.name +" " + elements.value + " "+ elements.label);
        }
    }

    public String substitude(String payload) {
        for (symbolElement elements : symbolTable){
            if (payload.equals(elements.name)){
                return elements.value;
            }
        }
        return payload;
    }
}