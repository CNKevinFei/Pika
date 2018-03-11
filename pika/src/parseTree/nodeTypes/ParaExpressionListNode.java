package parseTree.nodeTypes;

import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class ParaExpressionListNode extends ParseNode {

	public ParaExpressionListNode() {
		super(LextantToken.fakeToken("para expression list node", Punctuator.PARAEXPRESSIONLIST));
	}
	public ParaExpressionListNode(ParseNode node) {
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
