import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Translator {


    public static void main(String args[]){
        instance = getInstance();
        instance.createTestInstructions();
        PDA pda = new PDA(instance.getInstructions());
        System.out.println("Instructions");
        pda.outputList(instance.getInstructions());
        System.out.println("Programm");
        pda.run();
        System.out.println("Hashmap");
        pda.outputHashmap();
        System.out.println("end");
        pda.printSymbolTable();
    }

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
    private ArrayList<Instruction> instructions;
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

    /*private void createTestInstructions(){
        //MAIN

        // var x = 5
        this.addInstruction("ADDSYMBOL","5","null" ,"x");

        //calling function
        this.addInstruction("ADDSYMBOL","null","back1" ,"fak1");
        this.addInstruction("LOADSYMBOLLABEL","fak1");
        this.addInstruction("GOTO","fak");
        this.addInstruction("LABEL","back1");
        //function call completed

        this.addInstruction("HALT");

        //function
        this.addInstruction("LABEL","fak");
        this.addInstruction("LOADSYMBOLVALUE","x");
        this.addInstruction("OUT");
        this.addInstruction("GOTOSTACK");
    }*/
    private void createTestInstructions(){
        //MAIN

        // var x = 5
        this.addInstruction("ADDSYMBOL","5","null" ,"x");

        //calling function
        this.addInstruction("ADDSYMBOL","null","back1" ,"fak1");
        this.addInstruction("LOADSYMBOLLABEL","fak1");
        this.addInstruction("GOTO","fak");
        this.addInstruction("LABEL","back1");
        //function call completed

        this.addInstruction("HALT");

        //function fak
        this.addInstruction("LABEL","fak");
        //print x
        this.addInstruction("LOADSYMBOLVALUE","x");
        this.addInstruction("OUT");

        this.addInstruction("GOTOSTACK");
        //funktionsende
    }
}