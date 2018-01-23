package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.LextantToken;
import tokens.Token;

public class TypeConstantNode extends ParseNode {
	public TypeConstantNode(Token token) {
		super(token);
		assert(token instanceof LextantToken);
	}
	public TypeConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public String getValue() {
		return TypeToken().getLextant().getLexeme();
	}

	public LextantToken TypeToken() {
		return (LextantToken)token;
	}	

///////////////////////////////////////////////////////////
// accept a visitor
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

}
