/*
 * File: JsonManager.java
 * Name: Andrei Pak
 * Date: Feb 10, 2020
 * Desc: Class that produces an object to handle JSON data string or file into node object tree that is more 
 * 			managable then the string or file it self. 
 * 			By parsing the JSON data in a string format it finds key characters of the data that signify 
 * 			keys, values, object, arrays and delimeters in order split the JSON data string a manageble part
 * 			one at a time as it loads it into node objects by adding key and values and then inserting it into
 * 			the node tree in the way the JSON data is designed to hold its data, in essence to simulating
 * 			its design.
 * 			More functions will be added with more use that this class will have.
 * Usage: Will create an object to parse and create a node object tree from a JSON data string/file in order
 * 			to have ease accessing JSON data, with functions to select containers or keys, and retrive its
 * 			children pairs or values of selected keys.	
 */



import utilities.Utils;

public class JsonManager {
	Utils u = new Utils();
	//FIELDS FOR PARSING JSON STRING
	private String in;		//Initial and main string that will contain the entrie Json data.
	private String str;		//String that will contain a substring for each informative part of Json Data.
	private char fChar;		//Character to hold the first character of each informative part of Json Data.
	private int iLast = 0;	//Integer to hold the last ending index of the str sub string.
	private int iCur;		//Integer to hold the current ending index of the str sub string.
	
	//FIELDS TO BUILD THE JSON TREE
	//Initial empty node set to allow input of key&/value for the initial Json data entry.
	private JsonNode node  = new JsonNode();
	JsonNode root;	//Initial and main root node container to be set at the initial data that Json data provides.
	JsonNode cur;	//Current node container to be set for each data set that Json data provides.
	
	//CONSTRUCTOR
	//Initilized with Json data holding string to initialize data work with, then processing through it.
	public JsonManager(String in) {
		this.in = in;
		stringToTree();
		//root.printConts();
	}
	
	/* Main method of the object that loops through the string, with the use of
	 * indexes that signify informative substrings and characters that then are
	 * processed into nodes and added through node tree sequence that is configured
	 * to hold Json data simulating Json format.
	 */
	private void stringToTree() {
		//Intial boolean to hold the processing information that represents if the loop sequence is for an
		// array object.
		boolean gotArr = false;						
		
		//Looping through the main string using the last ending integer that is intially zero and incremented 
		// after processing key parts of the Json string untill it is greater than the main strings length
		while(iLast < in.length()) {
			//Executing a method to find the first substring for the current part of the sequence in the main string.
			parseString();	
			
			//TESTING FIRST CHARACTER OR STR SUBSTRING FOR VALID NODE ELEMENTS
			//Testing first character if matches an indicator of an object.
			if (fChar == '{') {
				//If root has not been set assighn node val element to the first character, then set the node as root 
				// and then current as the node is a container for other nodes. Reset node to a new node.
				if (root == null) {
					node.setVal(fChar);
					cur = root = node;
					newNode();
				}
				/* Else first test if gotArr is set, if set disable in order process the next
				 * parts of Json data appropriately (Explained further in the method). Setting
				 * the first character as value, adding the node to current node, setting the
				 * current container to node, and resetting the node.
				 */
				else {
					if (gotArr) gotArr = false;
					node.setVal(fChar);
					setChildAsParent();
				}
				
			}
			//If first character is closing curly bracket, set the current container to the parent container.
			else if (fChar == '}') {
				cur = cur.parent;
			}
			//Testing if first character is an indicator of an array of Json objects
			else if (fChar == '[') {
				/* Testing to make sure the substring is only one character to indicate the
				 * array has contents, enabling gotArr to run appropriate processing further in
				 * the Json data.(Explained further in the method). Setting the first character
				 * to value elment of the node like for all containers. Adding node as a child
				 * of current, then setting current to the node in question. Resetting node.
				 */
				if (str.length() == 1) {
					node.setVal(fChar);
					gotArr = true;
					setChildAsParent();
				}
				/* If the 2nd character is a closing square brackets indicating an empty array
				 * thus proccessing both opening and closing brackets as a val of the node,
				 * setting the node as a child and continue processing next parts of Json data
				 * as they also will be children unless indication other wise.
				 */
				else if (str.charAt(1) == ']'){
					node.setVal(str);
					setChild();
				}
			}
			//If first character is an indicator of ending of an array, disable gotArr and set current nodes
			// parent to current container.
			else if (fChar == ']') {
				gotArr = false;
				cur = cur.parent;
			}
			/* If first character is a quote test node key if empty and gotArr is disabled
			 * set the the substring set to the node key. Else the substring is the nodes
			 * value clearing node as complete and able to be set as child to the current
			 * container.
			 */
			else if (fChar == '"') {
				if (node.key == null & !gotArr) node.setKey(str);
				else {
					node.setVal(str);
					setChild();
				}
			}
			/* If the first character of the substring is alphanumeric(including underscore)
			 * or numeric is then set as value element of the node clearing the node
			 * complete and therefore set as a child of a current contaner.
			 */
			else if (str.matches("^[\\d\\w]+(.)*")) {
				node.setVal(str);
				setChild();
			}
		}			
	}	
	
	//Method to shorten 2 lines into one, having current adding a child a current node in question,
	// then resetting node in question.
	private void setChild() {
		cur.addChild(node);
		newNode();
	}
	
	private void setChildAsParent() {
		cur.addChild(node);
		cur = node;
		newNode();
	}
	
	//Method to short hand the reset of the node in question, resetting by assigning a new node 
	// object.
	private void newNode() { node  = new JsonNode();}
	
	/* Method to find the first informative substing in the main string in order to
	 * split the main string in manageble and configurable parts one at a time by
	 * finding last ending(initial 0) and current ending and setting the said
	 * indexes for this object to process if it is a node of part(and what kind) of
	 * a node.
	 */
	private void parseString() {
		//Setting initial substring of the main string to hold everythin but starting 
		//only from the last ending, in order to find the current ending.
		setTarget(0);
				
		//TESTING FIRST CHARACTER OF SUBSTRING
		//If the first character matches square or curly opening brackets
		if (str.matches("^[\\[\\{](.)*")) {
			/* if the next character is a square backet the substring is an empty array
			 * therefore the will be used as a value, by setting the current ending at the
			 * character index after the closing square bracket.
			 */
			if(in.charAt(iLast + 1) == ']') iCur += 2;
			/* Otherwise the backet character will signfy an object or array initialization
			 * by setting the current ending at the next character index after the currently
			 * set ending index
			 */
			else iCur++;
		}
		//If first character matches colon, comma , square or curly ending brackets will 
		// set the current ending index to increment by one.
		else if (str.matches("^[:,\\]\\}](.)*")) iCur++;
		//If first character is a double quote test will execute the method that will find
		// the ending quote of the substring.
		else if (fChar == '"') findQEnd();
		//If the first character is a number or alphanumeric character (including underscore)
		// test will execute the method that will the ending character of the substring.
		else if (str.matches("^[\\d\\w]+(.)*")) findEnd();

		//Setting target substring appropriate to process to node element.
		setTarget(1);
		
		//Setting the last ending index to the current ending index to process the next part 
		// of the Json data.
		iLast = iCur;
	}
	
	/* Method created mainly for readability as mainly it is not required as the
	 * processing be held in the parseString method on their own. This method had 2
	 * settings that set this objects substring based on what the processing of the
	 * object requires. As well as sets the first character as it is required for
	 * more efficient processing.
	 */
	private void setTarget(int setting) {
		//Setting 0 creates the substring to hold evrything but starting from the last ending.
		if (setting == 0) str = in.substring(iLast);
		/* Setting other integer creates a substring to hold the substring that holds
		 * the specific informative part of the Json data that will be used to process
		 * in the current cycle of the Json data and the Json node tree that is being
		 * built.
		 */
		else str = in.substring(iLast, iCur);
		fChar = in.charAt(iLast);	//Setting first character to the character at the last ending.
	}
	
	//Method that finds the ending quote of the current informational part of input string
	// while making sure the quote is not part of the quoted information part.
	private void findQEnd() {
		//Setting the index of the first occurance of quoted after the initial character.
		int iQuote = in.indexOf('"', iLast + 1);
		//While the character is followed by a slash character find and set the quote index
		// of the next occuring quote.
		while (in.charAt(iQuote - 1) == '\\')  iQuote = in.indexOf('"', iQuote + 1);
		//Once appropriate quote is found set the current index to the quote index plus one 
		// in order set the appropriate length of a one character substring.
		iCur = iQuote + 1;		
	}
	
	/* Method that find the ending character of the current infomational part of the
	 * input string by assessing all preceding endings that occur starting from the
	 * last ending ending index and choosing the the closest ending character to the
	 * last ending index.
	 */
	private void findEnd() {
		//Finding the next occurance index for each character (',',],})
		int iComma = in.indexOf(',', iLast);
		int iClsSBrk = in.indexOf(']', iLast); 
		int iClsCBrk = in.indexOf('}', iLast);
		//Initializing an integer field to hold the valid ending character.
		int iEnding;		
		
		/* Testing if square brackets index is less then curly brackets index and making
		 * sure it is available as the indexOf function of a string will return -1 when
		 * the character does not occur. If the test is true the ending index will
		 * equate to the index of the next ocurring square brackets, other wise the
		 * curly bracket is the ending index.
		 */
		if (iClsSBrk < iClsCBrk && iClsSBrk != -1) iEnding = iClsSBrk;
		else iEnding = iClsCBrk;
		
		//Testing is comma index is less then the found ending index and comma character was found.
		// If the test is true the ending index will equate to the comma index other wise no change.
		if (iComma < iEnding && iComma != -1) iEnding = iComma;
		
		//Setting the current ending to the found ending in this method.
		iCur = iEnding;
	}
}
	
