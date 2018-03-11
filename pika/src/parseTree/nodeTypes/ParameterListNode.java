package parseTree.nodeTypes;

import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class ParameterListNode extends ParseNode {

	public ParameterListNode() {
		super(LextantToken.fakeToken("parameter list node", Punctuator.PARAMETERLIST));
	}
	public ParameterListNode(ParseNode node) {
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
