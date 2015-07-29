package scripts;

import java.util.HashMap;

public class Node {
private int id;
private String name;
private Node parent;
private HashMap<String, Node> children;

public Node(int i){
	this.id = i;
	this.name = "root_of_life";
}

public Node(String name){
	this.name = name;
}

public Node(String name, int id, Node parent){
	this.name = name;
	this.id = id;
	this.parent = parent;
}

public String getName() {
	return name;
}

public int getId() {
	return id;
}

public Node getParent() {
	return parent;
}

public HashMap<String, Node> getChildren() {
	return children;
}

public boolean hasChild(String name){
	if (this.children == null) return false;
	if (this.children.containsKey(name)){
		return true;
	}
	else return false;
}

public Node getChild(String name){
	return this.children.get(name);
}

public void addChild(Node child) {
	if (this.children == null){
		this.children = new HashMap<String, Node>();
	}
	this.children.put(child.getName(), child);
}


}
