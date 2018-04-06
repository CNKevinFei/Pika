package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class ForElemStatementNode extends ParseNode {

	public ForElemStatementNode(Token token, ParseNode iden, ParseNode expr, ParseNode block) {
		super(token);
		this.appendChild(iden);
		this.appendChild(expr);
		this.appendChild(block);
	}
	public ForElemStatementNode(ParseNode node) {
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
