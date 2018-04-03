package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import logging.PikaLogger;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.IdentifierToken;
import tokens.Token;

public class IdentifierNode extends ParseNode {
	private Binding binding;
	private Token conOrVar;
	private Scope declarationScope;
	private boolean staticFlag;

	public IdentifierNode(Token token) {
		super(token);
		assert(token instanceof IdentifierToken);
		this.binding = null;
	}
	public IdentifierNode(ParseNode node) {
		super(node);
		
		if(node instanceof IdentifierNode) {
			this.binding = ((IdentifierNode)node).binding;
		}
		else {
			this.binding = null;
		}
	}
	
////////////////////////////////////////////////////////////
// attributes
	
	public IdentifierToken identifierToken() {
		return (IdentifierToken)token;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	public Binding getBinding() {
		return binding;
	}
	public void setConOrVar(Token token) {
		this.conOrVar = token;
	}
	public Token getConOrVar() {
		return conOrVar;
	}
	public void setStatic(boolean flag) {
		staticFlag = flag;
	}
	public boolean getStatic() {
		return staticFlag;
	}
	
////////////////////////////////////////////////////////////
// Speciality functions

	public Binding findVariableBinding() {
		String identifier = token.getLexeme();
		Binding bind;
		boolean flag = true;

		for(ParseNode current : pathToRoot()) {
			if(current.containsBindingOf(identifier)) {
				declarationScope = current.getScope();
				bind = current.bindingOf(identifier);
				
				if(flag||bind.getStatic())
					return bind;
				else
					continue;
			}
			else if(current instanceof LambdaNode) {
				flag = false;
			}
		}
		
		
		useBeforeDefineError();
		return Binding.nullInstance();
	}

	public Scope getDeclarationScope() {
		findVariableBinding();
		return declarationScope;
	}
	public void useBeforeDefineError() {
		PikaLogger log = PikaLogger.getLogger("compiler.semanticAnalyzer.identifierNode");
		Token token = getToken();
		log.severe("identifier " + token.getLexeme() + " used before defined at " + token.getLocation());
	}
	
///////////////////////////////////////////////////////////
// accept a visitor
		
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}

///////////////////////////////////////////////////////////
// satisfy the conditions for assignment
	
	public boolean satisfy(ParseNode value) {
		if(conOrVar.isLextant(Keyword.VAR) && this.getType().equivalent(value.getType()))
			return true;
		
		return false;
	}
}
