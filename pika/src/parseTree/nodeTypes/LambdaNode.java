package parseTree.nodeTypes;

import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class LambdaNode extends ParseNode {
	public LambdaNode(ParseNode param, ParseNode functionBody) {
		super(LextantToken.fakeToken("lambda node", Punctuator.LAMBDA));
		
		this.appendChild(param);
		this.appendChild(functionBody);
	}

	public LambdaNode(ParseNode node) {
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


