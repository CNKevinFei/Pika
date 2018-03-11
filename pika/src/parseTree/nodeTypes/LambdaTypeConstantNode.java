package parseTree.nodeTypes;

import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class LambdaTypeConstantNode extends ParseNode {
	public LambdaTypeConstantNode(ParseNode param, ParseNode returnType) {
		super(LextantToken.fakeToken("lambda type constant node", Punctuator.LAMBDATYPE));
		
		this.appendChild(param);
		this.appendChild(returnType);
	}

	public LambdaTypeConstantNode(ParseNode node) {
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


