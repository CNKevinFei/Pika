 package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class GlobalDeclarationNode extends ParseNode {
	
	public GlobalDeclarationNode(Token token) {
		super(token);
		assert(token.isLextant(Keyword.CONST) || token.isLextant(Keyword.VAR));
	}

	public GlobalDeclarationNode(ParseNode node) {
		super(node);
	}
	
	
	////////////////////////////////////////////////////////////
	// attributes
	
	public Lextant getDeclarationType() {
		return lextantToken().getLextant();
	}
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	
	////////////////////////////////////////////////////////////
	// convenience factory
	
	public static GlobalDeclarationNode withChildren(Token token, ParseNode declaredName, ParseNode initializer) {
		GlobalDeclarationNode node = new GlobalDeclarationNode(token);
		node.appendChild(declaredName);
		node.appendChild(initializer);
		
		return node;
	}
	

	///////////////////////////////////////////////////////////
	// boilerplate for visitors
			
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
