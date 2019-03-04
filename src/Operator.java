/*
	Operator.java
	
	Diese Klasse repraesentiert als Unterklasse von Semantic die
	semantische Funktion der Regeln mit dem Nonterminal
	Operator auf der linken Seite. 

*/

class Operator extends Semantic{
	//-------------------------------------------------------------------------
	// operator -> (expression) 
	// operator.f = expression.f
	//
	// operator -> num 
	// operator.f = num.f
	//-------------------------------------------------------------------------
	int f(SyntaxTree t, int n){
		if (t.getChildNumber()==3){
			SyntaxTree expression=t.getChild(1);
			return expression.value.f(expression,UNDEFINED);
		}else{
			SyntaxTree num=t.getChild(0);
			return num.value.f(num,UNDEFINED);		
		}		
	}//f 	
}//Operator