package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class CloneExpressionNode extends ParseNode {

	public CloneExpressionNode(Token token, ParseNode node) {
		super(token);
		this.appendChild(node);
	}
	public CloneExpressionNode(ParseNode node) {
		super(node);
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
