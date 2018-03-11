package parseTree.nodeTypes;

import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;

public class LambdaParameterTypeNode extends ParseNode {
	public LambdaParameterTypeNode(ParseNode list, ParseNode returnType) {
		super(LextantToken.fakeToken("lambda param type node", Punctuator.LAMBDAPARAMTYPE));
		
		this.appendChild(list);
		this.appendChild(returnType);
	}

	public LambdaParameterTypeNode(ParseNode node) {
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


