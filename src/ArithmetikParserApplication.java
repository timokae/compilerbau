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

class ArithmetikParserApplication implements TokenList{
    public static void main(String args[]){
        // Anlegen des Wurzelknotens fuer den Syntaxbaum. Dem Konstruktor
        // wid als Token das Startsymbol der Grammatik uebergeben

        convertSourceCode("testdatei_arithmetik.txt", "tmp.txt");

        SyntaxTree parseTree = new SyntaxTree(EXPRESSION);

        // Anlegen des Parsers als Instanz der Klasse ArithmetikParserClass
        ArithmetikParserClass parser = new ArithmetikParserClass(parseTree);
        NumScanner numScanner = new NumScanner();
        //parser.readInput("tmp.txt");

        if (numScanner.readInput("tmp.txt")) {
            // lexikalische Analyse durchfuehren
            if (numScanner.lexicalAnalysis()) {
                //Aufruf des Parsers und Test, ob gesamte Eingabe gelesen
                if (parser.expression(parseTree) && parser.inputEmpty()) {
                    parseTree.printSyntaxTree("", true);    //Ausgabe des Syntaxbaumes und des sematischen Wertes

                    Translator translator = Translator.getInstance();
                    Translator.traverse(parseTree);

                    ArithmetikParserApplication.printInstructions(translator.getInstructions());
                } else {
                    System.out.println("Fehler im Ausdruck"); //Fehlermeldung, falls Ausdruck nicht zu parsen war
                } // expression

            } else {
                System.out.println("Fehler in lexikalischer Analyse"); //Fehlermeldung, falls lexikalische Analyse fehlgeschlagen
            } // lexicalAnalysis
        } // readInput
    }//main

    private static void printInstructions(ArrayList<Translator.Instruction> instructions) {
        StringBuilder builder = new StringBuilder();
        for (Translator.Instruction i : instructions) {
            builder.setLength(0);
            builder.append(i.getCommand());

            if (i.getPayload() != null) {
                builder.append(": ");
                builder.append(i.getPayload());
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
                case "🔃":
                    builder.append("for");
                    break;
                case "👉":
                    builder.append("do");
                    break;
                case "😵":
                    builder.append("end");
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
                System.out.println(replaceEmojis(s));

                writer.write(replaceEmojis(s));
                writer.newLine();

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