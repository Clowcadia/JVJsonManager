
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utilities.Utils;

public class Json {
	Utils u = new Utils();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> list = new ArrayList();
	JsonNode root;
	JsonNode cur;
	
	public Json(String in) {
		list = jsonToList(in);
		listToObj(list);
      	idCont("results_arr");
	}
	
	private List<String> jsonToList(String in){
		int iDev = in.indexOf(':');
		int iComma = in.indexOf(',');
		
		if (in.matches("^[\\{|\\[|\\}|\\]](.*)")) {
			if (in.matches("^\\],(.*)")){
				list.add(in.substring(0, 2));
				jsonToList(in.substring(2));
			}
			else if (in.matches("^\\},(.*)")){
				list.add(in.substring(0, 2));
				jsonToList(in.substring(2));
			}
			else {
				list.add(in.substring(0,1));
					jsonToList(in.substring(1));
			}
		}
		else if (in.matches("^\\\"[a-zA-Z0-9 ]*\\\",(.*)")) {
			list.add(in.substring(0, iComma + 1));
			jsonToList(in.substring(iComma + 1));
		}
		else if (in.matches("^\\\"[a-zA-Z0-9 ]*\\\"\\](.*)")) {
			list.add(in.substring(0, in.substring(1).indexOf('"') + 2));
			jsonToList(in.substring(in.substring(1).indexOf('"') + 2));
		}
		else if (in.matches("^[0-9]*,(.*)")) {
			list.add(in.substring(0, iComma + 1));
			jsonToList(in.substring(iComma + 1));
		}
		else if (in.matches("^[0-9]*](.*)")) {
			list.add(in.substring(0, in.indexOf(']')));
			jsonToList(in.substring(in.indexOf(']')));
		}
		else if (in.substring(iDev + 1).matches("^[0-9](.*)")) {
			if (iComma != -1) {
				list.add(in.substring(0, iComma + 1));
				jsonToList(in.substring(iComma + 1));
			}
			else {
				if (in.lastIndexOf('}') != -1) {
					list.add(in.substring(0, in.lastIndexOf('}') - 1));
					jsonToList(in.substring(in.lastIndexOf('}') - 1));
				}				
			}			
		}
		else if (in.substring(iDev + 1).matches("^\\\"(.*)")) {
			if (in.substring(iDev + 2).indexOf(',') < in.substring(iDev + 2).indexOf('"')) {
				list.add(in.substring(0, in.substring(iDev + 2).indexOf('"') + iDev + 4));
				jsonToList(in.substring(in.substring(iDev + 2).indexOf('"') + iDev + 4));
			}			
			else {
				list.add(in.substring(0, iComma + 1));
				jsonToList(in.substring(iComma + 1));
			}
		}
		else if (in.substring(iDev + 1).matches("^\\[(.*)")) {
			if (in.substring(iDev + 2).matches("^\\](.*)")) {
				list.add(in.substring(0, iDev + 4));
				jsonToList(in.substring(iDev + 4));
			}
			else {
				list.add(in.substring(0, iDev + 1));
				jsonToList(in.substring(iDev + 1));
			}
		}
		else if (in.substring(iDev + 1).matches("^\\{(.*)")) {
			list.add(in.substring(0, iDev + 1));
			jsonToList(in.substring(iDev + 1));
		}
		else if (in.substring(iDev + 1).matches("^(null|false|true)(.*)")) {
			if (in.substring(iDev + 1).matches("^(null|false|true)}(.*)"))
			{
				list.add(in.substring(0, in.indexOf('}')));
				jsonToList(in.substring(in.indexOf('}')));
			}
			else if (in.substring(iDev + 1).matches("^(null|false|true)](.*)"))
			{
				list.add(in.substring(0, in.indexOf(']')));
				jsonToList(in.substring(in.indexOf(']') + 1));
			}
			else {
				list.add(in.substring(0, iComma + 1));
				jsonToList(in.substring(iComma + 1));
			}			
		}		
		return list;
	}

	public void listToObj(List<String> items) {
		for (int i = 0; i < items.size(); i++) {
			String item =  items.get(i);	
			int iDev = item.indexOf(':');
			int iComma = item.indexOf(',');
			String key;
			String val;
			JsonNode node;
			
			if (item.charAt(0) == '{') {
				if (root == null) {
					cur = new JsonNode(null, "main", null);
					root = cur;
				}
				else {	
					node = new JsonNode(cur, "cont", null);
					cur.children.add(node);
					cur = node;
				}
			}			
			else if (item.charAt(0) == '[') { 
				int iDevL = items.get(i - 1).indexOf(':');
				key = items.get(i - 1).substring(0, iDevL).replace("\"", "") + "_arr"; 
				node = new JsonNode(cur, key, null);
				cur.children.add(node); 
				cur = node;
			} 
			  else if (item.charAt(0) == ']' | item.charAt(0) == '}') { 
				  if (cur.parent!= null) {			  
					  cur = cur.parent; 
				  } 
			  }			 
			else if (item.matches("^\\\"[a-zA-Z_]*\\\":[\\\"\\(\\)\\[\\]\\.\\?\\-\\&\\\\a-zA-Z0-9,_:;*/! ]*")) {
				key = item.substring(0, iDev).replace("\"", "");
				if (item.charAt(item.length() - 1) == ',') val = item.substring(iDev + 1, item.length() - 1).replace("\"", "");
				else val = item.substring(iDev + 1).replace("\"", "");
				cur.children.add(new JsonNode(cur, key, val));
			}
			else if (item.matches("^[\\\"a-zA-Z0-9, ]+,*")) {
				if (iComma != -1) val = item.substring(0, iComma).replace("\"", "");
				else val = item.replace("\"", "");
				cur.children.add(new JsonNode(cur, null, val));
			}
		}
		moveUpToMain();
		
	}
	
	public void printNodePath(JsonNode node) {
		if (node.key != "main") {
			printNodePath(node.parent);
			u.print("->" + node.key);
		} else u.print("->" + node.key);
	}
	
	public void moveUpContainer() {
		if (cur.key != "main") cur = cur.parent;
	}
	
	public void moveUpToMain() {
		if (cur.key != "main") {
			moveUpContainer();
			moveUpToMain();
		}
	}
	
	public void moveToCont(String add) {
		String[] conts = add.split(",");
		boolean found = false;
		for (String cont : conts) {
			for (JsonNode child : cur.children) {
				if(child.key.equals(cont)) {
					found = true;
					cur = child;					
				}
			}
		}
		if (found == false) u.print("Invalid Address");
	}
	
	public void printConts() {
		u.print(cur.key);
		if (cur.children.size() > 0) for (JsonNode node : cur.children) printConts(node, 0);
	}
	
	public void printConts(JsonNode node, int spcN) {
		String spc = "";
		if(node.key != null && node.children.size() > 0) {
			for (int i = 0; i <= spcN; i++) spc += " ";
			if (node.val != null) u.print("->" + spc + node.key + " " + node.val);
			else u.print("->" + spc + node.key);
			for (JsonNode node1 : node.children) printConts(node1, spcN + 1);
		}
	}
	
	public void printContItems(String add) {
		moveToCont(add);
		for (JsonNode child : cur.children) child.print(false, true, true);
	}
	
	
	
	public void idCont(String contAdd) {
		moveToCont(contAdd);
		for (JsonNode cont : cur.children) {
			cont.key = cont.children.get(0).key;
			cont.val = cont.children.get(0).val;
		}
		moveUpToMain();
	}

	public void saveChildrenAsFiles(String dirAdd, String jsonAdd, String filter) throws IOException {
		File jsonFile;
		moveToCont(jsonAdd);
		for (JsonNode node : cur.children) {
			if (node.key.equals(filter)) {
				jsonFile = new File(dirAdd + "/" + node.val + ".txt");	
				jsonFile.createNewFile();
				FileWriter writer = new FileWriter(jsonFile);
				for (JsonNode child : node.children) writer.write(child.nodeToString() + "\n");
				writer.flush();
				writer.close();	
			}
		}
		
	}
}
