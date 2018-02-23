package semanticAnalyzer.signatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import semanticAnalyzer.types.*;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Punctuator;
import asmCodeGenerator.codeStorage.*;

public class FunctionSignatures extends ArrayList<FunctionSignature> {
	private static final long serialVersionUID = -4907792488209670697L;
	private static Map<Object, FunctionSignatures> signaturesForKey = new HashMap<Object, FunctionSignatures>();
	
	Object key;
	
	public FunctionSignatures(Object key, FunctionSignature ...functionSignatures) {
		this.key = key;
		for(FunctionSignature functionSignature: functionSignatures) {
			add(functionSignature);
		}
		signaturesForKey.put(key, this);
	}
	
	public Object getKey() {
		return key;
	}
	public boolean hasKey(Object key) {
		return this.key.equals(key);
	}
	
	public FunctionSignature acceptingSignature(List<Type> types) {
		for(FunctionSignature functionSignature: this) {
			if(functionSignature.accepts(types)) {
				return functionSignature;
			}
		}
		return FunctionSignature.nullInstance();
	}
	public boolean accepts(List<Type> types) {
		return !acceptingSignature(types).isNull();
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	// access to FunctionSignatures by key object.
	
	public static FunctionSignatures nullSignatures = new FunctionSignatures(0, FunctionSignature.nullInstance());

	public static FunctionSignatures signaturesOf(Object key) {
		if(signaturesForKey.containsKey(key)) {
			return signaturesForKey.get(key);
		}
		return nullSignatures;
	}
	public static FunctionSignature signature(Object key, List<Type> types) {
		FunctionSignatures signatures = FunctionSignatures.signaturesOf(key);
		return signatures.acceptingSignature(types);
	}

	
	
	/////////////////////////////////////////////////////////////////////////////////
	// Put the signatures for operators in the following static block.
	
	static {

		new FunctionSignatures(Punctuator.ADD,
		    new FunctionSignature(ASMOpcode.Add, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
		    new FunctionSignature(ASMOpcode.FAdd, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
		    new FunctionSignature(0, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL)
		);
		
		new FunctionSignatures(Punctuator.SUBTRACT,
			    new FunctionSignature(ASMOpcode.Subtract, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
			    new FunctionSignature(ASMOpcode.FSubtract, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
			    new FunctionSignature(0, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL)
		);
		
		new FunctionSignatures(Punctuator.MULTIPLY,
			    new FunctionSignature(ASMOpcode.Multiply, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
			    new FunctionSignature(ASMOpcode.FMultiply, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
			    new FunctionSignature(0, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL)
		);
		
		new FunctionSignatures(Punctuator.DIVIDE,
			    new FunctionSignature(ASMOpcode.Divide, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
			    new FunctionSignature(ASMOpcode.FDivide, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
			    new FunctionSignature(0, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL)
		);
		
		new FunctionSignatures(Punctuator.GREATER,
			    new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.GREATEROREQUAL,
			    new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.SMALLER,
			    new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.SMALLEROREQUAL,
			    new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.EQUAL,
				new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN), 
				new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.BOOLEAN),
				new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.STRING, PrimitiveType.STRING, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.NOTEQUAL,
				new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN), 
				new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.BOOLEAN),
				new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN),
			    new FunctionSignature(1, PrimitiveType.STRING, PrimitiveType.STRING, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.OPEN_BRACKET,
				new FunctionSignature(0, PrimitiveType.CHAR, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
				new FunctionSignature(0, PrimitiveType.INTEGER, PrimitiveType.CHAR, PrimitiveType.CHAR),
				new FunctionSignature(0, PrimitiveType.INTEGER, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
				new FunctionSignature(0, PrimitiveType.FLOAT, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
				new FunctionSignature(0, PrimitiveType.INTEGER, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN),
				new FunctionSignature(0, PrimitiveType.CHAR, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN),
				new FunctionSignature(0, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
				new FunctionSignature(0, PrimitiveType.CHAR, PrimitiveType.CHAR, PrimitiveType.CHAR),
				new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
				new FunctionSignature(1, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN),
				new FunctionSignature(1, PrimitiveType.STRING, PrimitiveType.STRING, PrimitiveType.STRING),
				new FunctionSignature(1, PrimitiveType.RATIONAL, PrimitiveType.FLOAT, PrimitiveType.FLOAT),
				new FunctionSignature(1, PrimitiveType.RATIONAL, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
				new FunctionSignature(1, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL),
				new FunctionSignature(1, PrimitiveType.CHAR, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL),
				new FunctionSignature(1, PrimitiveType.INTEGER, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL),
				new FunctionSignature(1, PrimitiveType.FLOAT, PrimitiveType.RATIONAL, PrimitiveType.RATIONAL)
		);
		
		new FunctionSignatures(Punctuator.AND,
				new FunctionSignature(0, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.OR,
				new FunctionSignature(0, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN)
		);
		
		new FunctionSignatures(Punctuator.OVER,
				new FunctionSignature(0, PrimitiveType.INTEGER, PrimitiveType.INTEGER, PrimitiveType.RATIONAL)
		);
		
		new FunctionSignatures(Punctuator.EXPRESSOVER,
				new FunctionSignature(0, PrimitiveType.RATIONAL, PrimitiveType.INTEGER, PrimitiveType.INTEGER),
				new FunctionSignature(0, PrimitiveType.FLOAT, PrimitiveType.INTEGER, PrimitiveType.INTEGER)
		);
		
		new FunctionSignatures(Punctuator.RATIONALIZE,
				new FunctionSignature(0, PrimitiveType.RATIONAL, PrimitiveType.INTEGER, PrimitiveType.RATIONAL),
				new FunctionSignature(0, PrimitiveType.FLOAT, PrimitiveType.INTEGER, PrimitiveType.RATIONAL)
		);	
		
		
		
		TypeVariable s = new TypeVariable("S");
		List<TypeVariable> setS = Arrays.asList(s);
		
		new FunctionSignatures(Punctuator.ARRAY_INDEX,
				new FunctionSignature(0, setS, new ArrayType(s), PrimitiveType.INTEGER, s));
		
		
		new FunctionSignatures(Punctuator.SEPARATOR,
				new FunctionSignature(0, setS, s, s, s));
		
		new FunctionSignatures(Keyword.NEW,
				new FunctionSignature(0, setS, new ArrayType(s), PrimitiveType.INTEGER, new ArrayType(s)));
		
		// First, we use the operator itself (in this case the Punctuator ADD) as the key.
		// Then, we give that key two signatures: one an (INT x INT -> INT) and the other
		// a (FLOAT x FLOAT -> FLOAT).  Each signature has a "whichVariant" parameter where
		// I'm placing the instruction (ASMOpcode) that needs to be executed.
		//
		// I'll follow the convention that if a signature has an ASMOpcode for its whichVariant,
		// then to generate code for the operation, one only needs to generate the code for
		// the operands (in order) and then add to that the Opcode.  For instance, the code for
		// floating addition should look like:
		//
		//		(generate argument 1)	: may be many instructions
		//		(generate argument 2)   : ditto
		//		FAdd					: just one instruction
		//
		// If the code that an operator should generate is more complicated than this, then
		// I will not use an ASMOpcode for the whichVariant.  In these cases I typically use
		// a small object with one method (the "Command" design pattern) that generates the
		// required code.

	}

}
