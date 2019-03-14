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
		
		char transitions[][][]={
		//				START	KOMMA			Symbol														OPEN_PAR	  CLOSE_PAR		PLUS	MINUS	MULT	DIV									NUM                     IF   		String			Enstate
		//				---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		/*START*/		{{},    {},					{'a','b','c','d','e','f','g','h','i','j','k',
							'l','m','n','o','p','q', 'r','s','t','u','v','w','x','y','z'},					{'('},		{')'}, 		{'+'},	{'-'},	{'*'},	{'/'},				{'0','1','2','3','4','5','6','7','8','9'},  {}, 		{}, 			{' '} 	 },
		/*KOMMA*/		{{},    {}, 				{},														{},			{},			{},		{},		{},		{},							{} ,      							{}, 		{},				{} 			},
		/*Symbol*/		{{},    {},					{'a','b','c','d','e','g','h','i','j','k',
				'l','m','n','o','p','q', 'r','s','t','u','v','w','x','y','z','0', '1','2','3',
				'4','5','6','7','8','9'},																	{},			{},			{},		{},		{},		{},							{},	 						 		{'f'}, 		{},				{} 			},
		/*OPEN_PAR*/	{{},    {},					{},														{},			{},			{},		{},		{},		{},							{},	 						 		{}, 		{},				{} 			},
		/*CLOSE_PAR*/	{{},    {},					{},														{},			{},			{},		{},		{},		{},							{},	 						 		{}, 		{},				{} 			},
		/*PLUS*/		{{},    {},					{},														{},			{},			{},		{},		{},		{},							{},	 						 		{}, 		{},				{} 			},
		/*MINUS*/		{{},    {},					{},														{},			{},			{},		{},		{},		{},							{},	 						 		{}, 		{},				{} 			},
		/*MULT*/		{{},    {},					{},														{},			{},			{},		{},		{},		{},							{},	 						 		{}, 		{},				{} 			},
		/*DIV*/			{{},    {},					{},														{},			{},			{},		{},		{},		{},							{},	 								{}, 		{},				{} 			},
		/*NUM */		{{},    {},					{},														{}, 		{},			{},		{},		{},		{},					{'0','1','2','3','4','5','6','7','8','9'},	{}, 		{},				{' '} 			},
		/*IF */			{{},    {},					{'a','b','c','d','e','f','g','h','i','j','k',
				'l','m','n','o','p','q', 'r','s','t','u','v','w','x','y','z'},								{}, 		{},			{},		{},		{},		{},							{},									{}, 	    {},				{' '} 			},
		/*String*/		{{},    {},					{},														{}, 		{},			{},		{},		{},		{},							{},									{}, 		{},				{} 			},
		/*Endstate*/	{{},    {},					{},														{}, 		{},			{},		{},		{},		{},							{},									{}, 		{},				{' '} 			}









		};

		//				--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		// Zustaende zum DEA
		byte states[]={START, KOMMA, Symbol, OPEN_PAR, CLOSE_PAR, PLUS, MINUS, MULT, DIV, NUM, FUNCTION,STRING,EndState};
		// Instanz des DEA anlegen
		this.dea=new DEA(transitions, states);
	}
	
	// Gibt den zum Zahlenwert passenden String des Tokentyps zurueck
	// Implementierung der abstrakten Methode aus der Klasse Scanner
	String getTokenString(byte token){
		switch(token){
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
		default: return "";
		}
	}
	
	

}