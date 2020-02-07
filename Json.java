
import utilities.Utils;

public class Json {
	Utils u = new Utils();
	private String in;	
	private String str;
	private char fChar;
	private int iLast = 0;
	private int iCur;
	JsonNode root;
	JsonNode cur;	
	JsonNode node  = new JsonNode();
	
	
	public Json(String in) {
		this.in = in;
		stringToTree();
		//root.printConts("");
	}
	
	private void stringToTree() {
		boolean gotArr = false;
		
		while(iLast < in.length()) {			
			parseString();	
			if (fChar == '{') {				
				if (root == null) {
					node.setVal(fChar);
					cur = root = node;
					newNode();
				}
				else {
					if (gotArr) gotArr = false;
					node.setVal(fChar);
					cur.addChild(node);
					cur = node;
					newNode();
				}
				
			}
			else if (fChar == '}') {
				cur = cur.parent;
			}
			else if (fChar == '[') {
				if (str.length() == 1) {
					gotArr = true;
					node.setVal(fChar);
					cur.addChild(node);
					cur = node;
					newNode();
				}
				else if (str.charAt(1) == ']'){
					node.setVal(str);
					setChild();
				}
			}
			else if (fChar == ']') {
				gotArr = false;
				cur = cur.parent;
			}
			else if (fChar == '"') {
				if (node.key == null & !gotArr) {
					node.setKey(str);
				}
				else {
					node.setVal(str);
					setChild();
				}
			}
			else if (str.matches("^[\\d\\w]+(.)*")) {
				node.setVal(str);
				setChild();
			}
		}			
	}	
	
	private void setChild() {
		cur.addChild(node);
		newNode();
	}
	
	private void newNode() { node  = new JsonNode();}
	
	private void parseString() {
		setTarget(0);
				
		if (str.matches("^[\\[\\{\\]\\:,](.)*")) {
			if(in.charAt(iLast + 1) == ']') iCur += 2;
			else iCur++;
		}
		else if (str.matches("^[:,\\]\\}](.)*")) iCur++;
		else if (fChar == '"') findQEnd();
		else if (str.matches("^[\\d\\w]+(.)*")) findEnd();

		setTarget(1);
		//if (!str.matches("^[:,](.)*")) u.pln(str);
		iLast = iCur;
	}
	
	private void setTarget(int setting) {
		if (setting == 0) {
			str = in.substring(iLast);
			fChar = in.charAt(iLast);
		}
		else {
			str = in.substring(iLast, iCur);
			fChar = in.charAt(iLast);
		}
	}
	
	private void findQEnd() {
		int iQuote = in.indexOf('"', iLast);
		
		iQuote = in.indexOf('"', iLast + 1);
		while (in.charAt(iQuote - 1) == '\\') {				
			iQuote = in.indexOf('"', iQuote + 1);	
		}
		iCur = iQuote + 1;		
	}
	
	private void findEnd() {
		int iComma = in.indexOf(',', iLast);
		int iClsSBrk = in.indexOf(']', iLast); 
		int iClsCBrk = in.indexOf('}', iLast);
		int iCharLast;
		
		
		if (iClsSBrk < iClsCBrk && iClsSBrk != -1) iCharLast = iClsSBrk;
		else iCharLast = iClsCBrk;
		
		if (iComma < iCharLast && iComma != -1) iCharLast = iComma;
		iCur = iCharLast;
	}
}
	
