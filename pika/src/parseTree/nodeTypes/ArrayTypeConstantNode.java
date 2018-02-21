package parseTree.nodeTypes;

import parseTree.ParseNode;
import tokens.Token;
import parseTree.ParseNodeVisitor;


public class ArrayTypeConstantNode extends ParseNode{
	
	public ArrayTypeConstantNode(Token token, ParseNode type) {
		super(token);
		this.appendChild(type);
	}
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors

	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		//System.out.println(layerNum);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
