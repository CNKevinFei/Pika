package parseTree.nodeTypes;

import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class ParameterNode extends ParseNode {
	public ParameterNode(ParseNode type, ParseNode identifier) {
		super(LextantToken.fakeToken("parameter node", Punctuator.PARAMETER));
		
		this.appendChild(type);
		this.appendChild(identifier);
	}

	public ParameterNode(ParseNode node) {
		super(node);
	}
	
	
	////////////////////////////////////////////////////////////
	// attributes
	
	public Lextant getFunctionDeclarationType() {
		return lextantToken().getLextant();
	}
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}


