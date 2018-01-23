package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.CharacterToken;
import tokens.Token;

public class CharConstantNode extends ParseNode {
	public CharConstantNode(Token token) {
		super(token);
		assert(token instanceof CharacterToken);
	}
	public CharConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public char getValue() {
		return CharToken().getValue();
	}

	public CharacterToken CharToken() {
		return (CharacterToken)token;
	}	

///////////////////////////////////////////////////////////
// accept a visitor
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

}
