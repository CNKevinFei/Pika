package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class FoldExpressionNode extends ParseNode {

	public FoldExpressionNode(Token token, ParseNode array, ParseNode lamb, ParseNode base) {
		super(token);
		this.appendChild(array);
		this.appendChild(lamb);
		this.appendChild(base);
	}
	public FoldExpressionNode(Token token, ParseNode array, ParseNode lamb) {
		super(token);
		this.appendChild(array);
		this.appendChild(lamb);
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
