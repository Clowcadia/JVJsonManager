
import java.util.ArrayList;
import java.util.List;

import utilities.Utils;

public class JsonNode {
	Utils u = new Utils();
	JsonNode parent;	
	public String key;
	public String val;
	//public int contNum = 0;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<JsonNode> children = new ArrayList();
		
	public JsonNode(JsonNode parent, String key, String val) {
		this.parent = parent;
		this.key = key;
		this.val = val;
	}	
	
	public void print(boolean par, boolean k, boolean v) {
		if (k == true) u.print("Key " + key);
		if (v == true) u.print("Value " + val);
		if (par == true) u.print("Parent " + parent);
	}
	
	public String nodeToString() {
		return key + ":	" + val;
	}
	
	
	

}
