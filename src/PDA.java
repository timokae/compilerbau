import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;

public class PDA {
    private static PDA instance;

    //new linked list which contains all commands

    private ArrayList<Translator.Instruction> program;
    private LinkedList<String> stack;

    private HashMap<String, Integer> labelList = new HashMap<String, Integer>();


    /*public PDA() {

        stack = new LinkedList<>();
    }*/
    public PDA(ArrayList<Translator.Instruction> program) {

        stack = new LinkedList<>();
        this.program = program;
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

    //ggf. private setzten? ggf. nicht benötigt
    public String pop() {
        return stack.pop();
    }

    public void load(String element){
        stack.push(element);
    }
    //to load integers
    public void load(Integer element) {
        stack.push(element.toString());
    }
    // momentan ncht verwendet
    public LinkedList<String> getStack() {
        return this.stack;
    }

    //momentan nicht verwednet
    /*public static PDA getInstance() {
        if (PDA.instance == null) {
            PDA.instance = new PDA();
        }

        return PDA.instance;
    }*/

    public void add()
    {
        this.load(Integer.parseInt(this.pop())+Integer.parseInt(this.pop()));
    }
    public void sub()
    {
        this.load(Integer.parseInt(this.pop())-Integer.parseInt(this.pop()));
    }
    public void mul()
    {
        this.load(Integer.parseInt(this.pop())*Integer.parseInt(this.pop()));
    }
    public void div()
    {
        this.load(Integer.parseInt(this.pop())/Integer.parseInt(this.pop()));
    }
    private void printElement(Integer element){
        System.out.println(element);
    }
    public void move(String labelName){
        this.load(labelName);
    }
    public int moveToStack(){
        return goTo(this.pop());
    }



    public void run(){
        int i = 0;
        while (i<program.size()){
        i = execute(program.get(i),i);
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
                this.load(Integer.parseInt(Instruction.getPayload()));
                break;
            case "LABEL":
                this.label("Instruction.getLabelPayload",currentPosition);
                break;
            case"GOTO":
                ret = this.goTo("Instruction.getLabelpayload");
                break;
            case"GOTRUE":
                ret = this.goTrue("Instruction.getLabelpayload");
                break;
            case "GOFALSE":
                ret = this.goFalse("Instruction.getLabelpayload");
                break;
            case"PRINTINT":
                this.printElement(Integer.parseInt(Instruction.getPayload()));
            case"HALT":
                ret = program.size(); //Jumps beyond the end of the programm thus ending the while loop used in run
                break;
            case"MOVE":
                this.move(Instruction.getPayload());
                break;
            case"MOVETOSTACK":
                ret = this.moveToStack();
                break;
            case"POP":
                this.pop();
                break;

        }
            if (ret == -1){
            return currentPosition+1;
        }else {
            return ret;
        }
    }
}