package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class IfStatementNode extends ParseNode{
	public IfStatementNode(Token token, ParseNode condition, ParseNode ifContent, ParseNode elseContent) {
		super(token);
		this.appendChild(condition);
		this.appendChild(ifContent);
		this.appendChild(elseContent);
	}
	
	public IfStatementNode(Token token, ParseNode condition, ParseNode ifContent) {
		super(token);
		this.appendChild(condition);
		this.appendChild(ifContent);
	}
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}

}
