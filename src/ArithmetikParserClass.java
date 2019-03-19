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
    // Konstante f�r Ende der Eingabe
    public final char EOF=(char)255;
    // Zeiger auf das aktuelle Eingabezeichen
    private int pointer;
    // Zeiger auf das Ende der Eingabe
    private int maxPointer;
    // Eingabe zeichenweise abgelegt
    //private char input[];
    private LinkedList<Scanner.Token> tokens;
    // Syntaxbaum
    private SyntaxTree parseTree;
    public HashMap<String, LinkedList<Scanner.Token>> tokenLists;
    public HashMap<String, SyntaxTree> treeList;

    //-------------------------------------------------------------------------
    //------------Konstruktor der Klasse ArithmetikParserClass-----------------
    //-------------------------------------------------------------------------

    ArithmetikParserClass(SyntaxTree parseTree, LinkedList<Scanner.Token> tokenStream){
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
            this.init(this.parseTree);
            treeList.put(pair.getKey().toString(), this.parseTree);

            it.remove(); // avoids a ConcurrentModificationException
        }

        //this.parseTree=parseTree;
        //this.tokens = tokens;
        //this.pointer=0;
        //this.maxPointer=tokens.size() - 1;
    }

    //-------------------------------------------------------------------------
    //-------------------Methoden der Grammatik--------------------------------
    //-------------------------------------------------------------------------

    boolean init(SyntaxTree sT) {
        return (
            parameter(sT.insertSubtree(PARAMETER))
            &&
            expression(sT.insertSubtree(EXPRESSION))
        );
    }

    //-------------------------------------------------------------------------
    // expression -> term rightExpression
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------
    boolean expression(SyntaxTree sT){
        return (
                term(sT.insertSubtree(TERM))
                        &&
                        rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
        );
    }//expression


    //-------------------------------------------------------------------------
    // rightExpression -> '+' term rightExpression |
    //                    '-' term rightExpression | Epsilon
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------
    boolean rightExpression(SyntaxTree sT){
        SyntaxTree epsilonTree;

        // Falls aktuelles Eingabezeichen '+'
        if (match(TokenList.PLUS,sT)) {
            //rightExpression -> '+' term rightExpression
            return term(sT.insertSubtree(TERM)) && rightExpression(sT.insertSubtree(RIGHT_EXPRESSION));
            // Falls aktuelles Eingabezeichen '-'
        } else if (match(TokenList.MINUS,sT)) {
            //rightExpression -> '-' term rightExpression
            return term(sT.insertSubtree(TERM)) && rightExpression(sT.insertSubtree(RIGHT_EXPRESSION));
            // sonst
        } else if (match(TokenList.COMPERATOR, sT)) {
            return term(sT.insertSubtree(TERM)) && rightExpression(sT.insertSubtree(RIGHT_TERM));
        } else {
            //rightExpression ->Epsilon
            epsilonTree = sT.insertSubtree(EPSILON);
            return true;
        }
    }//rightExpression


    //-------------------------------------------------------------------------
    // term -> condition rightTerm
    // term -> operator rightTerm
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------

    boolean term(SyntaxTree sT){
        // term -> operator rightTerm
        if (match(TokenList.IF, sT)) {
            return (
                    condition(sT.insertSubtree(COMPERATOR))
                            &&
                            conditionBranch(sT.insertSubtree(RIGHT_TERM))
            );
        } else if (match(TokenList.DEFINE, sT)) {
            return (
                    symbol(sT.insertSubtree(SYMBOL))
                            &&
                            expression(sT.insertSubtree(EXPRESSION))
            );
        }
        else if (match(TokenList.ASSIGN, sT)) {
            return (
                    symbol(sT.insertSubtree(SYMBOL))
                            &&
                            expression(sT.insertSubtree(EXPRESSION))
            );
        } else {
            return (
                    operator(sT.insertSubtree(OPERATOR))
                            &&
                            rightTerm(sT.insertSubtree(RIGHT_TERM))
            );
        }
    }//term

    //-------------------------------------------------------------------------
    // rightTerm -> '*' operator rightTerm |
    //              '/' operator rightTerm | Epsilon
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------

    boolean rightTerm(SyntaxTree sT){
        char [] multDivSet = {'*','/'};
        char [] divSet = {'/'};
        SyntaxTree epsilonTree;

        // Falls aktuelles Eingabezeichen '*' oder '/'
        if (match(TokenList.MULT,sT) || match(TokenList.DIV, sT)) {
            //rightTerm -> '*' operator rightTerm bzw.
            //rightTerm -> '/' operator rightTerm
            return operator(sT.insertSubtree(OPERATOR)) && rightTerm(sT.insertSubtree(RIGHT_TERM));
        } else {
            //rightTerm ->Epsilon
            epsilonTree = sT.insertSubtree(EPSILON);
            return true;
        }
    }//rightTerm

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

    //-------------------------------------------------------------------------
    // operator -> '(' expression ')' | num
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------
    boolean operator(SyntaxTree sT){
        //char [] openParSet= {'('};
        //char [] closeParSet= {')'};
        //char [] digitSet= {'1','2','3','4','5','6','7','8','9','0'};

        // Falls aktuelle Eingabe '('
        if (match(TokenList.OPEN_PAR,sT)) {
            //operator -> '(' expression ')'
            if (expression(sT.insertSubtree(EXPRESSION))){
                // Fallunterscheidung erm�glicht, den wichtigen Fehler einer
                // fehlenden geschlossenen Klammer gesondert auszugeben
                if(match(TokenList.CLOSE_PAR,sT)) {
                    return true;
                } else {//Syntaxfehler
                    syntaxError("Geschlossene Klammer erwartet");
                    return false;
                }
            }else{
                syntaxError("Fehler in geschachtelter Expression");
                return false;
            }
            // sonst versuchen nach digit abzuleiten
        } else if (num(sT.insertSubtree(NUM))) {
            //operator -> num
            return true;
        } else { //Syntaxfehler
            syntaxError("Ziffer oder Klammer auf erwartet");
            return false;
        }
    }//operator

    boolean condition(SyntaxTree sT) {
        return (
                num(sT.insertSubtree(NUM))
                        &&
                        rightExpression(sT.insertSubtree(RIGHT_EXPRESSION))
        );
    }



    //-------------------------------------------------------------------------
    // num -> digit num | digit
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------
    boolean num(SyntaxTree sT){
        if (match(TokenList.NUM, sT)) {
            return true;
        } else {
            syntaxError("Ziffer erwartet");
            return false;
        }
    }//num

    boolean symbol(SyntaxTree sT) {
        if (match(TokenList.SYMBOL, sT)) {
            return true;
        } else {
            syntaxError("Symbol erwartet");
            return false;
        }
    }

    boolean word(SyntaxTree sT) {
        if (match(TokenList.STRING, sT)) {
            return true;
        } else{
            syntaxError("String erwartet");
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
    // digit -> '1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'0'
    // Der Parameter sT ist die Wurzel des bis hier geparsten Syntaxbaumes
    //-------------------------------------------------------------------------
    boolean digit(SyntaxTree sT){
        //char [] matchSet = {'1','2','3','4','5','6','7','8','9','0'};

        if (match(TokenList.DIGIT,sT)){            //digit->'1'|'2'...|'9'|'0'
            return true;                  // korrekte Ableitung der Regel m�glich
        }else{
            syntaxError("Ziffer erwartet"); // korrekte Ableitung der Regel
            return false;                   // nicht m�glich
        }
    }//digit

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