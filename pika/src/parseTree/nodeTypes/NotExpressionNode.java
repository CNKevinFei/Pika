package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class NotExpressionNode extends ParseNode {

	public NotExpressionNode(Token token, ParseNode node) {
		super(token);
		this.appendChild(node);
	}
	public NotExpressionNode(ParseNode node) {
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
