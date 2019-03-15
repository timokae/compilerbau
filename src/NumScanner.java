/*
	NunScanner.java
	
	Diese Klasse implementiert die Zustnde und Transitionstabelle eines DEA fuer
	Ziffernfolgen nach dem folgenden regulaeren Ausdruck:
	
													+
	NUM := {'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'0'}

*/
class NumScanner extends Scanner{
	
	//-------------------------------------------------------------------------
	// Konstruktor (Legt die Zustaende und Transitionstabelle des DEA an)
	//-------------------------------------------------------------------------
	
	NumScanner(){
		// Transitionstabelle zum regulaeren Ausdruck
		//	    											+
		// NUM := {'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'0'}

		char[] az = {'a','b','c','d','e','f','g','h','i','j','k', 'l','m','n','o','p','q', 'r','s','t','u','v','w','x','y','z'};
		char[] n09 = {'0','1','2','3','4','5','6','7','8','9'};
		char[] az09 = {'a','b','c','d','e','f','g','h','i','j','k', 'l','m','n','o','p','q', 'r','s','t','u','v','w','x','y','z', '0','1','2','3','4','5','6','7','8','9'};

		char transitions[][][]={
				//              START   KOMMA   SYMBOL  OPEN_PAR    CLOSE_PAR  PLUS     MINUS     MULT     DIV     NUM   Comp0              Comp1   Comp2   String  Enstate
				//              -------------------------------------------------------------------------------------------------------------------------------------------------
				/*START*/       {{},    {},     az,     {'('},      {')'},     {'+'},   {'-'},    {'*'},   {'/'},  n09,  {'<', '>', '='},   {},     {},     {'"'},  {}          },
				/*KOMMA*/       {{},    {},     {},     {},         {},        {},      {},       {},      {},     n09,  {},                {},     {},     {},     {}          },
				/*SYMBOL*/      {{},    {},     az09,   {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*OPEN_PAR*/    {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*CLOSE_PAR*/   {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*PLUS*/        {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*MINUS*/       {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*MULT*/        {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*DIV*/	        {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*NUM */        {{},    {'.'},  {},     {},         {},        {},      {},       {},      {},     n09,  {},                {},     {},     {},     {' '}        },
				/*Comp0*/       {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {'='},  {},     {},     {' '}       },
				/*Comp1*/       {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {' '}       },
				/*Comp2*/       {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {}          },
				/*String*/      {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     az09,   {'"'}       },
				/*Endstate*/    {{},    {},     {},     {},         {},        {},      {},       {},      {},     {},   {},                {},     {},     {},     {}          }
		};

		//				--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		// Zustaende zum DEA
		byte states[]={START, KOMMA, Symbol, OPEN_PAR, CLOSE_PAR, PLUS, MINUS, MULT, DIV, NUM, COMPERATOR, COMPERATOR, COMPERATOR ,STRING,EndState};
		// Instanz des DEA anlegen
		this.dea=new DEA(transitions, states);
	}
	
	// Gibt den zum Zahlenwert passenden String des Tokentyps zurueck
	// Implementierung der abstrakten Methode aus der Klasse Scanner
	String getTokenString(byte token){
		switch(token){
            //START   KOMMA   SYMBOL  OPEN_PAR    CLOSE_PAR  PLUS     MINUS     MULT     DIV     NUM   Comp0              Comp1   Comp2   String  Enstate
			case  1: return "NUMBER";
			case  5: return "START";
			case  7: return "KOMMA";
			case  8: return "Symbol";
			case  9: return "OPEN_PAR";
			case 10: return "CLOSE_PAR";
			case 11: return "PLUS";
			case 12: return "MINUS";		
			case 13: return "MULT";
			case 14: return "DIV";
			case 22: return "FUNCTION";
			case 23: return "String";
			case 24: return "Enstate";
			case 25: return "Comperator";
		default: return "";
		}
	}
	
	

}