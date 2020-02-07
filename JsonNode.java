
import java.util.ArrayList;
import java.util.List;

import utilities.Utils;

public class JsonNode {
	Utils u = new Utils();
	public JsonNode parent;	
	public String key;
	public String val;
	//public int contNum = 0;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<JsonNode> children = new ArrayList();
		
	public JsonNode() {}
	
	public JsonNode(String key, String val) {
		this.key = key;
		this.val = val;
	}	
	
	public void setKey(String str) {
		this.key = str;
	}
	
	public void setKey(char ch) {
		this.key = Character.toString(ch);
	}
	
	public void setVal(String str) {
		this.val = str;
	}
	
	public void setVal(char ch) {
		this.val = Character.toString(ch);
	}
	
	public void addChild(JsonNode child) {
		this.children.add(child);
		child.parent = this;
	}
	
	public void printConts(String spc) {
		u.pln(spc + key + ":" + val);
		if (children.size() > 0 ) {			
			for (JsonNode child : children) child.printConts(spc + " ");
		}
	}
	
	

}
