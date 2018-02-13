package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class ExpressionListNode extends ParseNode {

	public ExpressionListNode(Token token, ParseNode node) {
		super(token);
		this.appendChild(node);
	}
	public ExpressionListNode(ParseNode node) {
		super(node);
		this.appendChild(node);
	}
	
	////////////////////////////////////////////////////////////
	// no attributes

	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
