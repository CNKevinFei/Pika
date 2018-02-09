package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;

public class ArrayIndexVariableNode extends ParseNode{
	public ArrayIndexVariableNode(ParseNode identifier, ParseNode index) {
		super(identifier);
		this.appendChild(identifier);
		this.appendChild(index);
	}
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors

	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
	
}
