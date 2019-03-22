import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/*
    ArithmetikParserClass.java

    Praktikum Algorithmen und Datenstrukturen
    Grundlage zum Versuch 2

    Diese Java Klasse implementiert einen
    einfachen Parser zum Erkennen arithmetischer
    Ausdr�cke der folgenden Grammatik:

    expression -> term rightExpression
    rightExpression -> '+' term rightExpression
    rightExpression -> '-' term rightExpression
    rightExpression -> Epsilon
    term -> operator rightTerm
    term -> condition rightTerm
    rightTerm -> '*' operator rightTerm
    rightTerm -> '/' operator rightTerm
    rightTerm -> Epsilon
    operator -> '(' expression ')' | num
    num -> digit num | digit
    digit -> '1' | '2' | '3' | '4' | '5' |'6' | '7' | '8' | '9' | '0'

    Epsilon steht hier für das "leere Wort"

    Der Parser ist nach dem Prinzip des rekursiven Abstiegs programmiert,
    d.h. jedes nicht terminale Symbol der Grammatik wird durch eine
    Methode in Java repräsentiert, die die jeweils anderen nicht terminalen
    Symbole auf der rechten Seite der Grammatik Regeln ggf. auch rekursiv
    aufruft.

    Der zu parsende Ausdruck wird aus einer Datei gelesen und in einem
    Array of Char abgespeichert. Pointer zeigt beim Parsen auf den aktuellen
    Eingabewert.

    Ist der zu parsende Ausdruck syntaktisch nicht korrekt, so werden
    �ber die Methode syntaxError() entsprechende Fehlermeldungen ausgegeben.

    Zus�tzlich werden den Methoden der Klasse neben der Rekursionstiefe auch
    eine Referenz auf eine Instanz der Klasse SyntaxTree �bergeben.

    �ber die Instanzen der Klasse SyntaxTree wird beim rekursiven Abstieg
    eine konkreter Syntaxbaum des geparsten Ausdrucks aufgebaut.

*/

public class ArithmetikParserClass implements TokenList{
    // Konstante für Ende der Eingabe
    public final char EOF=(char)255;
    // Zeiger auf das aktuelle Eingabezeichen
    private int pointer;
    // Zeiger auf das Ende der Eingabe
    private int maxPointer;
    // Eingabe zeichenweise abgelegt
    //private char input[];
    public LinkedList<Scanner.Token> tokenStream;
    private LinkedList<Scanner.Token> tokens;
    // Syntaxbaum
    private SyntaxTree parseTree;
    public HashMap<String, LinkedList<Scanner.Token>> tokenLists;
    public HashMap<String, SyntaxTree> treeList;

    //-------------------------------------------------------------------------
    //------------Konstruktor der Klasse ArithmetikParserClass-----------------
    //-------------------------------------------------------------------------

    ArithmetikParserClass(LinkedList<Scanner.Token> tokenStream){
        this.tokenStream = tokenStream;
    }

    boolean parse() {
        tokenLists = new HashMap<>();
        treeList = new HashMap<>();

        String function_name = "";
        int i = 0;
        while(i < tokenStream.size()) {
            Scanner.Token token = tokenStream.get(i);

            if (token.token == FUNCTION) {
                function_name = tokenStream.get(++i).lexem;
                tokenLists.put(function_name, new LinkedList<>());
                i++;
            }

            if (tokenStream.get(i).token != -1){
                tokenLists.get(function_name).add(tokenStream.get(i));
            }

            i++;
        }

        Iterator it = tokenLists.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            System.out.println(pair.getKey());
            for(Scanner.Token t : tokenLists.get(pair.getKey())) {
                System.out.println(t.token + ": " + t.lexem);
            }

            System.out.println("--------");

            this.tokens = tokenLists.get(pair.getKey());
            this.pointer = 0;
            this.parseTree = new SyntaxTree(FUNCTION);

            if(this.tokens.getLast().token == END) {
                Scanner.Token t = this.tokens.getLast();
                t.token = -1;
                t.lexem = "EOF";
            } else if(this.tokens.getLast().token == -1) {
                this.tokens.remove(this.tokens.size() - 1);
            }


            this.maxPointer = this.tokens.size() - 1;
            if(!this.function(this.parseTree)) {
                return false;
            }
            treeList.put(pair.getKey().toString(), this.parseTree);

            it.remove(); // avoids a ConcurrentModificationException
        }

        return true;
    }

    //-------------------------------------------------------------------------
    //-------------------Methoden der Grammatik--------------------------------
    //-------------------------------------------------------------------------

    boolean function(SyntaxTree sT) {
        return (
            parameter(sT.insertSubtree(PARAMETER))
            &&
            expression(sT.insertSubtree(EXPRESSION))
        );
    }

    boolean expression(SyntaxTree sT){
        if (compareToken(TokenList.DEFINE, sT)) {
            return (
                    define(sT.insertSubtree(DEFINE))
                    &&
                    rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (compareToken(TokenList.ASSIGN, sT)) {
            return (
                assign(sT.insertSubtree(ASSIGN))
                &&
                rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (compareToken(TokenList.CALL, sT)) {
            return (
                 call(sT.insertSubtree(CALL))
                 &&
                 rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (compareToken(TokenList.IF, sT)) {
            return (
                comparator(sT.insertSubtree(IF))
                &&
                rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (compareToken(TokenList.WHILE, sT)) {
            return (
                comparator(sT.insertSubtree(WHILE))
                &&
                rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (compareToken(CALL, sT)) {
            return (
                call(sT.insertSubtree(CALL))
                &&
                rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        }
        else {
            sT.insertSubtree(EPSILON);
            return true;
        }
    }

    boolean define(SyntaxTree sT) {
        if (symbol(sT.insertSubtree(SYMBOL))) {
            if (inPlaceCompareToken(TokenList.SYMBOL,sT)) {
                return symbol(sT.insertSubtree(SYMBOL));
            } else if (term(sT.insertSubtree(TERM)) && rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))) {
                return true;
            }
        }

        return false;
    }

    boolean assign(SyntaxTree sT) {
        if (symbol(sT.insertSubtree(SYMBOL))) {
            if (inPlaceCompareToken(TokenList.SYMBOL,sT)) {
                return symbol(sT.insertSubtree(SYMBOL));
            } else if (term(sT.insertSubtree(TERM)) && rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))) {
                return true;
            }
        }

        return false;
    }

    boolean call(SyntaxTree sT) {
        if (match(TokenList.SYMBOL, sT)) {
            if (match(TokenList.NUM, sT)) {
                return true;
            }
        }

        return false;
    }

    boolean comparator(SyntaxTree sT) {
        return (
            comparision(sT.insertSubtree(COMPARISION))
            &&
            conditionBranch(sT.insertSubtree(EXPRESSION))
        );
    }

    boolean comparision(SyntaxTree sT) {
        boolean first = false;
        boolean second = false;
        boolean third = false;

        if (inPlaceCompareToken(TokenList.SYMBOL,sT)) {
            first = symbol(sT.insertSubtree(SYMBOL));
        } else if (num(sT.insertSubtree(NUM))) {
            first = true;
        }

        if (match(TokenList.COMPARISION, sT)) {
            second = true;
        }

        if (inPlaceCompareToken(TokenList.SYMBOL,sT)) {
            third = symbol(sT.insertSubtree(SYMBOL));
        } else if (num(sT.insertSubtree(NUM))) {
            third = true;
        }

        return first && second && third;
    }

    boolean conditionBranch(SyntaxTree sT) {
        if (match(TokenList.DO, sT)) {
            if (expression(sT.insertSubtree(EXPRESSION)) && rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))) {
               if (match(TokenList.END, sT)) {
                   return true;
               }
            }
        }

        return false;
    }

    boolean rightExpression(SyntaxTree sT){
        if (match(TokenList.PLUS,sT)) {
            return (
                term(sT.insertSubtree(TERM))
                &&
                rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (match(TokenList.MINUS,sT)) {
            return (
                term(sT.insertSubtree(TERM))
                &&
                rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
            );
        } else if (match(TokenList.COMPARISION, sT)) {
            return (
                term(sT.insertSubtree(TERM))
                &&
                rightExpression(sT.insertSubtree(RIGHT_TERM))
            );
        } else {
            return (expression(sT));
        }
    }

    boolean term(SyntaxTree sT){
        return (
            operator(sT.insertSubtree(OPERATOR))
            &&
            rightTerm(sT.insertSubtree(RIGHT_TERM))
        );
    }

    boolean rightTerm(SyntaxTree sT){
        if (match(TokenList.MULT,sT) || match(TokenList.DIV, sT)) {
            return (
                operator(sT.insertSubtree(OPERATOR))
                &&
                rightTerm(sT.insertSubtree(RIGHT_TERM))
            );
        } else {
            SyntaxTree epsilonTree = sT.insertSubtree(EPSILON);
            return true;
        }
    }

    boolean operator(SyntaxTree sT){
        if (match(TokenList.OPEN_PAR,sT)) {
            if (term(sT.insertSubtree(TERM)) && rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))){

                if(match(TokenList.CLOSE_PAR,sT)) {
                    return true;
                } else {
                    syntaxError("Geschlossene Klammer erwartet");
                    return false;
                }

            }else{
                syntaxError("Fehler in geschachtelter Expression");
                return false;
            }
        } else if (num(sT.insertSubtree(NUM))) {
            return true;
        } else {
            syntaxError("Ziffer oder Klammer auf erwartet");
            return false;
        }
    }

    boolean num(SyntaxTree sT){
        if (match(TokenList.NUM, sT)) {
            return true;
        } else {
            syntaxError("Ziffer erwartet");
            return false;
        }
    }

    boolean symbol(SyntaxTree sT) {
        if (match(TokenList.SYMBOL, sT)) {
            return true;
        } else {
            syntaxError("Symbol erwartet");
            return false;
        }
    }

    boolean parameter(SyntaxTree sT) {
        if (match(TokenList.NUM, sT)) {
            return true;
        } else {
            syntaxError("Ziffer erwartet");
            return false;
        }
    }

    //-------------------------------------------------------------------------
    //-------------------Hilfsmethoden-----------------------------------------
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Methode, die testet, ob das aktuele Eingabezeichen unter den Zeichen
    // ist, die als Parameter (matchSet) �bergeben wurden.
    // Ist das der Fall, so gibt match() true zur�ck und setzt den Eingabe-
    // zeiger auf das n�chste Zeichen, sonst wird false zur�ckgegeben.
    //-------------------------------------------------------------------------
    boolean match(byte token, SyntaxTree sT){
        SyntaxTree node;

        if (tokens.get(pointer).token==token){
            //Eingabezeichen als entsprechendem Knoten des Syntaxbaumes eintragen
            node=sT.insertSubtree(INPUT_SIGN);
            node.setCharacter(tokens.get(pointer).lexem);

            pointer++;	//Eingabepointer auf das n�chste Zeichen setzen
            return true;
        }

        return false;
    }//match

    boolean compareToken(byte token, SyntaxTree sT){
        if (tokens.get(pointer).token==token){
            pointer++;
            return true;
        }

        return false;
    }//match

    boolean inPlaceCompareToken(byte token, SyntaxTree sT) {
        return tokens.get(pointer).token==token;
    }

    //-------------------------------------------------------------------------
    //Methode, die testet, ob das auf das aktuelle Zeichen folgende Zeichen
    //unter den Zeichen ist, die als Parameter (aheadSet) �bergeben wurden.
    //Der Eingabepointer wird nicht ver�ndert!
    //-------------------------------------------------------------------------
    /*
    boolean lookAhead(char [] aheadSet){
        for (int i=0;i<aheadSet.length;i++) {
            if (input[pointer+1]==aheadSet[i]) {
                return true;
            }
        }
        return false;
    }//lookAhead
    */

    //-------------------------------------------------------------------------
    // Methode, die testet, ob das Ende der Eingabe erreicht ist
    // (pointer == maxPointer)
    //-------------------------------------------------------------------------
    boolean inputEmpty(){
        if (pointer==maxPointer){
            ausgabe("Eingabe leer! bzw zu am Ende",0);
            return true;
        }else{
            syntaxError("Eingabe bei Ende des Parserdurchlaufs nicht leer");
            return false;
        }

    }//inputEmpty


    //-------------------------------------------------------------------------
    // Methode zum korrekt einger�ckten Ausgeben des Syntaxbaumes auf der
    // Konsole
    //-------------------------------------------------------------------------
    void ausgabe(String s, int t){
        for(int i=0;i<t;i++)
            System.out.print("  ");
        System.out.println(s);
    }//ausgabe

    //-------------------------------------------------------------------------
    // Methode zum Ausgeben eines Syntaxfehlers mit Angabe des vermuteten
    // Zeichens, bei dem der Fehler gefunden wurde
    //-------------------------------------------------------------------------
    void syntaxError(String s){
        char z;
        if (pointer >= tokens.size()) {
            System.out.println("Syntax Fehler beim " + (pointer + 1) + ". Zeichen: EOF");
        } else {
            System.out.println("Syntax Fehler beim " + (pointer + 1) + ". Zeichen: " + tokens.get(pointer).token);
            System.out.println(tokens.get(pointer).lexem);
        }
        System.out.println(s);
    }//syntaxError

}//ArithmetikParserClass