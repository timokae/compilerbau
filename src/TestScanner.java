class TestScanner{
	static public void main(String args[]){
		SourceScanner scanner;
		scanner = new SourceScanner();
		if (scanner.readInput("testdatei_arithmetik.txt")){
			scanner.printInputStream();
			if(scanner.lexicalAnalysis())
				scanner.printTokenStream();
		}
		else
			System.out.println("Scanner beendet");
	}
}