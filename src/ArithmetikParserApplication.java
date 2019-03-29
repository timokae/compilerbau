/*
    ArithmetikParserApplikation.java

    Praktikum Algorithmen und Datenstrukturen
    Grundlage zum Versuch 3

    Diese Java Klasse implementiert die Applikation eines
    einfachen Parsers zum Erkennen arithmetischer
    Ausdruecke.

    Der eigentliche Parser wird in der Klasse ArithmeticParserClasse
    defifiert.

*/

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

class ArithmetikParserApplication implements TokenList{
    public static void main(String args[]){
        // Anlegen des Wurzelknotens fuer den Syntaxbaum. Dem Konstruktor
        // wid als Token das Startsymbol der Grammatik uebergeben

        convertSourceCode("input.txt", "tmp.txt");

        SyntaxTree parseTree = new SyntaxTree(EXPRESSION);

        SourceScanner sourceScanner = new SourceScanner();
        //parser.readInput("tmp.txt");

        if (sourceScanner.readInput("tmp.txt")) {
            // lexikalische Analyse durchfuehren
            if (sourceScanner.lexicalAnalysis()) {

                // Anlegen des Parsers als Instanz der Klasse ArithmetikParserClass
                ArithmetikParserClass parser = new ArithmetikParserClass(sourceScanner.tokenStream);

                //Aufruf des Parsers und Test, ob gesamte Eingabe gelesen
                if (parser.parse() && parser.inputEmpty()) {
                    for (String key : parser.treeList.keySet()) {
                        SyntaxTree tree = parser.treeList.get(key);
                        //tree.printSyntaxTree("", true);
                    }

                    // Array List mit den Instructions aller Funktionen
                    ArrayList<Translator.Instruction> allInstructions = new ArrayList<>();

                    SyntaxTree mainTree = parser.treeList.get("main");
                    if (mainTree == null) {
                        System.out.println("Keine Main 📣 gefunden!");
                        return;
                    }

                    Translator.startTraverse(mainTree);
                    allInstructions.addAll(Translator.getInstance().getInstructions());
                    Translator.getInstance().instructions.clear();

                    for (String key : parser.treeList.keySet()) {
                        if (!key.equals("main")) {
                            SyntaxTree tree = parser.treeList.get(key);
                            Translator.startTraverse(tree);
                            allInstructions.addAll(Translator.getInstance().getInstructions());
                            Translator.getInstance().instructions.clear();
                        }
                    }

                    ArithmetikParserApplication.printInstructions(allInstructions);
                    //PDA pda = new PDA(Translator.getInstance().getInstructions());
                    //pda.printSymbolTable();


                    PDA pda = new PDA(Translator.getInstance().getInstructions());
                    //pda.outputList(allInstructions);
                    //pda.outputHashmap();
                    pda.run();
                } else {
                    System.out.println("💣 im 💬"); //Fehlermeldung, falls Ausdruck nicht zu parsen war
                } // expression

            } else {
                System.out.println("💣 in 📖🔎"); //Fehlermeldung, falls lexikalische Analyse fehlgeschlagen
            } // lexicalAnalysis
        } // readInput
    }//main

    private static void printInstructions(ArrayList<Translator.Instruction> instructions) {
        StringBuilder builder = new StringBuilder();
        for (Translator.Instruction i : instructions) {
            builder.setLength(0);
            builder.append(i.getCommand());
            builder.append("    ");

            if (i.getPayload() != null) {
                builder.append("Payload: ");
                builder.append(i.getPayload());
                builder.append(" ");
            }

            if (i.getLabelPayload() != null) {
                builder.append("Label: ");
                builder.append(i.getLabelPayload());
                builder.append(" ");
            }

            if (i.getNamePayload() != null) {
                builder.append("Name: ");
                builder.append(i.getNamePayload());
                builder.append(" ");
            }

            System.out.println(builder.toString());
        }
    }

    private static String replaceEmojis(String s) {
        StringBuilder builder = new StringBuilder();
        String[] words = s.split(" ");
        for(String word : words) {
            switch (word) {
                case "🔀":
                    builder.append("if");
                    break;
                case "🔁":
                    builder.append("while");
                    break;
                case "👉":
                    builder.append("do");
                    break;
                case "😵":
                    builder.append("end");
                    break;
                case "✍️":
                    builder.append("define");
                    break;
                case "📌":
                    builder.append("assign");
                    break;
                case "🧐":
                    builder.append("function");
                    break;
                case "📣":
                    builder.append("call");
                    break;
                case "🖨":
                    builder.append("print");
                    break;
                default:
                    builder.append(word);
                    break;
            }
            builder.append(" ");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    private static void convertSourceCode(String input, String output) {
        BufferedReader reader;
        BufferedWriter writer;
        try {
            reader = new BufferedReader(new FileReader(input));
            writer = new BufferedWriter(new FileWriter(output, false));
            String line = reader.readLine();

            while (line != null) {
                String s = new String(line.getBytes("UTF-8"), "UTF-8");
                //System.out.println(replaceEmojis(s));

                writer.write(replaceEmojis(s));
                writer.write("\n");

                // read next line
                line = reader.readLine();
            }

            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}//ArithmetikParserApplikation