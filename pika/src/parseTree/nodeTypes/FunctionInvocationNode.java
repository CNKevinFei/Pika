package parseTree.nodeTypes;

import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class FunctionInvocationNode extends ParseNode {
	public FunctionInvocationNode(ParseNode lambda, ParseNode param) {
		super(LextantToken.fakeToken("function invocation", Punctuator.FUNCTIONINVOCATION));
		
		this.appendChild(lambda);
		this.appendChild(param);
	}

	public FunctionInvocationNode(ParseNode node) {
		super(node);
	}
	
	

	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}


