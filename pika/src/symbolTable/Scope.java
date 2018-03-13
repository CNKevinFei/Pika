      package symbolTable;

import inputHandler.TextLocation;
import lexicalAnalyzer.Keyword;
import logging.PikaLogger;
import parseTree.nodeTypes.IdentifierNode;
import semanticAnalyzer.types.Type;
import tokens.LextantToken;
import tokens.Token;

public class Scope {
	private Scope baseScope;
	private MemoryAllocator allocator;
	private SymbolTable symbolTable;
	
//////////////////////////////////////////////////////////////////////
// factories

	public static Scope createProgramScope() {
		return new Scope(programScopeAllocator(), nullInstance());
	}
	public Scope createSubscope() {
		Scope scope = new Scope(allocator, this);
		scope.enter();

		return scope;
	}
	public static Scope createParameterScope() {
		Scope scope = new Scope(parameterScopeAllocator(), nullInstance());
		scope.enter();
		
		return scope;
	}
	public static Scope createFunctionScope() {
		Scope scope = new Scope(functionScopeAllocator(), nullInstance());
		scope.enter();
		
		return scope;
	}
	
	private static MemoryAllocator programScopeAllocator() {
		return new PositiveMemoryAllocator(
				MemoryAccessMethod.DIRECT_ACCESS_BASE, 
				MemoryLocation.GLOBAL_VARIABLE_BLOCK);
	}
	
	private static MemoryAllocator parameterScopeAllocator() {
		return new ParameterMemoryAllocator(
				MemoryAccessMethod.INDIRECT_ACCESS_BASE, 
				MemoryLocation.FRAME_POINTER);
	}
	
	private static MemoryAllocator functionScopeAllocator() {
		return new FunctionMemoryAllocator(
				MemoryAccessMethod.INDIRECT_ACCESS_BASE, 
				MemoryLocation.FRAME_POINTER);
	}
	
//////////////////////////////////////////////////////////////////////
// private constructor.	
	private Scope(MemoryAllocator allocator, Scope baseScope) {
		super();
		this.baseScope = (baseScope == null) ? this : baseScope;
		this.symbolTable = new SymbolTable();
		
		this.allocator = allocator;
		//allocator.saveState();
	}
///////////////////////////////////////////////////////////////////////
//  enter scope
	public void enter() {
		allocator.saveState();
	}
	
///////////////////////////////////////////////////////////////////////
//  basic queries	
	public Scope getBaseScope() {
		return baseScope;
	}
	public MemoryAllocator getAllocationStrategy() {
		return allocator;
	}
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
	
///////////////////////////////////////////////////////////////////////
//memory allocation
	// must call leave() when destroying/leaving a scope.
	public void leave() {
		allocator.restoreState();
	}
	public int getAllocatedSize() {
		return allocator.getMaxAllocatedSize();
	}
	// for function parameters
	
	public void resetOffset() {
		assert allocator instanceof ParameterMemoryAllocator;
		
		((ParameterMemoryAllocator)allocator).resetOffset();
	}

///////////////////////////////////////////////////////////////////////
//bindings
	public Binding createBinding(IdentifierNode identifierNode, Type type) {
		Token token = identifierNode.getToken();
		Token conOrVar = identifierNode.getConOrVar();
		symbolTable.errorIfAlreadyDefined(token);

		String lexeme = token.getLexeme();
		Binding binding = allocateNewBinding(type, token.getLocation(), lexeme, conOrVar);	
		symbolTable.install(lexeme, binding);

		return binding;
	}
	
	private Binding allocateNewBinding(Type type, TextLocation textLocation, String lexeme, Token conOrVar) {
		MemoryLocation memoryLocation = allocator.allocate(type.getSize());

		return new Binding(type, textLocation, memoryLocation, lexeme, conOrVar);
	}
	
	public Binding createFunctionBinding(IdentifierNode identifierNode, Type type) {
		Token token = identifierNode.getToken();
		Token conOrVar = LextantToken.fakeToken("const function identifier", Keyword.CONST);
		symbolTable.errorIfAlreadyDefined(token);

		String lexeme = token.getLexeme();
		Binding binding = allocateNewBinding(type, token.getLocation(), lexeme, conOrVar);	
		symbolTable.install(lexeme, binding);

		return binding;
	}

///////////////////////////////////////////////////////////////////////
//toString
	public String toString() {
		String result = "scope: ";
		result += " hash "+ hashCode() + "\n";
		result += symbolTable;
		return result;
	}

////////////////////////////////////////////////////////////////////////////////////
//Null Scope object - lazy singleton (Lazy Holder) implementation pattern
	public static Scope nullInstance() {
		return NullScope.instance;
	}
	private static class NullScope extends Scope {
		private static NullScope instance = new NullScope();

		private NullScope() {
			super(	new PositiveMemoryAllocator(MemoryAccessMethod.NULL_ACCESS, "", 0),
					null);
		}
		public String toString() {
			return "scope: the-null-scope";
		}
		@Override
		public Binding createBinding(IdentifierNode identifierNode, Type type) {
			unscopedIdentifierError(identifierNode.getToken());
			return super.createBinding(identifierNode, type);
		}
		// subscopes of null scope need their own strategy.  Assumes global block is static.
		public Scope createSubscope() {
			return new Scope(programScopeAllocator(), this);
		}
	}


///////////////////////////////////////////////////////////////////////
//error reporting
	private static void unscopedIdentifierError(Token token) {
		PikaLogger log = PikaLogger.getLogger("compiler.scope");
		log.severe("variable " + token.getLexeme() + 
				" used outside of any scope at " + token.getLocation());
	}

}
