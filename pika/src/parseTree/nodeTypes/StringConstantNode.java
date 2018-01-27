package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.StringToken;
import tokens.Token;

public class StringConstantNode extends ParseNode {
	int offset = 0;
	int size = 0;
	public StringConstantNode(Token token) {
		super(token);
		assert(token instanceof StringToken);
	}
	public StringConstantNode(ParseNode node) {
		super(node);
	}

////////////////////////////////////////////////////////////
// attributes
	
	public String getValue() {
		return StringToken().getValue();
	}

	public StringToken StringToken() {
		return (StringToken)token;
	}	
////////////////////////////////////////////////////////////
// offset and size
	public void setLocation(int offset, int size) {
		this.offset = offset;
		this.size = size;
	}
	public int getOffset() {
		return this.offset;
	}
	public int getSize() {
		return this.size;
	}
	

///////////////////////////////////////////////////////////
// accept a visitor
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

}
