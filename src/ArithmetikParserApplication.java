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

import java.util.ArrayList;

class ArithmetikParserApplication implements TokenList{
    public static void main(String args[]){
        // Anlegen des Wurzelknotens fuer den Syntaxbaum. Dem Konstruktor
        // wid als Token das Startsymbol der Grammatik uebergeben

        BufferedReader reader;
        BufferedWriter writer;
        try {
            reader = new BufferedReader(new FileReader("testdatei_arithmetik.txt"));
            writer = new BufferedWriter(new FileWriter("tmp.txt", false));
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


        SyntaxTree parseTree = new SyntaxTree(EXPRESSION);

        // Anlegen des Parsers als Instanz der Klasse ArithmetikParserClass
        ArithmetikParserClass parser = new ArithmetikParserClass(parseTree);
        NumScanner numScanner = new NumScanner();
        parser.readInput("tmp.txt");

        if (numScanner.readInput("tmp.txt"))
            //System.out.println(parser.tokenStream.toString());
            // lexikalische Analyse durchfuehren
            if (numScanner.lexicalAnalysis()) {
                //Aufruf des Parsers und Test, ob gesamte Eingabe gelesen

                if (parser.expression(parseTree) && parser.inputEmpty()) {
                    //Ausgabe des Syntaxbaumes und des sematischen Wertes
                    parseTree.printSyntaxTree("", true);

                    Translator translator = Translator.getInstance();
                    Translator.traverse(parseTree);
                    ArithmetikParserApplication.printInstructions(translator.getInstructions());


                    //System.out.println("Korrekter Ausdruck mit Wert:"  +parseTree.value.f(parseTree,UNDEFINED));
                } else {
                    //Fehlermeldung, falls Ausdruck nicht zu parsen war
                    System.out.println("Fehler im Ausdruck");
                }
            } else {
                //Fehlermeldung, falls lexikalische Analyse fehlgeschlagen
                System.out.println("Fehler in lexikalischer Analyse");
            }

    }//main

    private static void printInstructions(ArrayList<Translator.Instruction> instructions) {
        StringBuilder builder = new StringBuilder();
        for(Translator.Instruction i : instructions) {
            builder.setLength(0);
            builder.append(i.getCommand());

            if(i.getPayload() != null) {
                builder.append(": ");
                builder.append(i.getPayload());
            }

            System.out.println(builder.toString());
        }
    public static String replaceEmojis(String s) {
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

        return builder.toString();
    }
}//ArithmetikParserApplikation