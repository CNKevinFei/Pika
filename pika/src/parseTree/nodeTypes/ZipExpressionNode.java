package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class ZipExpressionNode extends ParseNode {

	public ZipExpressionNode(Token token, ParseNode expr1, ParseNode expr2, ParseNode expr3) {
		super(token);
		this.appendChild(expr1);
		this.appendChild(expr2);
		this.appendChild(expr3);
	}
	public ZipExpressionNode(ParseNode node) {
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
