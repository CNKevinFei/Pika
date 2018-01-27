package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;

public class ProgramNode extends ParseNode {
	private int stringBlockSize;
	public ProgramNode(Token token) {
		super(token);
		stringBlockSize = 0;
	}
	public ProgramNode(ParseNode node) {
		super(node);
		stringBlockSize = 0;
	}
	
	////////////////////////////////////////////////////////////
	// stringBlockSie
	public int getStringBlockSize() {
		return this.stringBlockSize;
	}
	
	public void setStringBlocksize(int offset) {
		this.stringBlockSize = offset;
	}

	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
