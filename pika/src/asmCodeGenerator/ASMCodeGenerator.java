package asmCodeGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.runtime.RunTime;
import asmCodeGenerator.runtime.MemoryManager;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import parseTree.*;
import parseTree.nodeTypes.*;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Type;
import semanticAnalyzer.types.ArrayType;
import symbolTable.Binding;
import symbolTable.Scope;
import static asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.codeStorage.ASMOpcode.*;

// do not call the code generator if any errors have occurred during analysis.
public class ASMCodeGenerator {
	ParseNode root;

	public static ASMCodeFragment generate(ParseNode syntaxTree) {
		ASMCodeGenerator codeGenerator = new ASMCodeGenerator(syntaxTree);
		return codeGenerator.makeASM();
	}
	public ASMCodeGenerator(ParseNode root) {
		super();
		this.root = root;
	}
	
	public ASMCodeFragment makeASM() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		
		code.append( RunTime.getEnvironment() );
		code.append( globalVariableBlockASM() );
		code.append( stringConstantBlock());
		code.append( programASM() );
		code.append( MemoryManager.codeForAfterApplication() );
		
		return code;
	}
	private ASMCodeFragment globalVariableBlockASM() {
		assert root.hasScope();
		Scope scope = root.getScope();
		int globalBlockSize = scope.getAllocatedSize();
		
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		code.add(DLabel, RunTime.GLOBAL_MEMORY_BLOCK);
		code.add(DataZ, globalBlockSize);
		return code;
	}
	private ASMCodeFragment stringConstantBlock() {

		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		code.add(DLabel, RunTime.STRING_CONSTANT_BLOCK);
		return code;
	}
	private ASMCodeFragment programASM() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		
		code.add(    Label, RunTime.MAIN_PROGRAM_LABEL);
		code.append( programCode());
		code.add(    Halt );
		
		return code;
	}
	private ASMCodeFragment programCode() {
		CodeVisitor visitor = new CodeVisitor();
		root.accept(visitor);
		return visitor.removeRootCode(root);
	}


	protected class CodeVisitor extends ParseNodeVisitor.Default {
		private Map<ParseNode, ASMCodeFragment> codeMap;
		ASMCodeFragment code;
		
		public CodeVisitor() {
			codeMap = new HashMap<ParseNode, ASMCodeFragment>();
		}


		////////////////////////////////////////////////////////////////////
        // Make the field "code" refer to a new fragment of different sorts.
		private void newAddressCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_ADDRESS);
			codeMap.put(node, code);
		}
		private void newValueCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VALUE);
			codeMap.put(node, code);
		}
		private void newVoidCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VOID);
			codeMap.put(node, code);
		}

	    ////////////////////////////////////////////////////////////////////
        // Get code from the map.
		private ASMCodeFragment getAndRemoveCode(ParseNode node) {
			ASMCodeFragment result = codeMap.get(node);
			codeMap.remove(result);
			return result;
		}
	    public  ASMCodeFragment removeRootCode(ParseNode tree) {
			return getAndRemoveCode(tree);
		}		
		ASMCodeFragment removeValueCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			makeFragmentValueCode(frag, node);
			return frag;
		}		
		private ASMCodeFragment removeAddressCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			assert frag.isAddress();
			return frag;
		}		
		ASMCodeFragment removeVoidCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			assert frag.isVoid();
			return frag;
		}
		
	    ////////////////////////////////////////////////////////////////////
        // convert code to value-generating code.
		private void makeFragmentValueCode(ASMCodeFragment code, ParseNode node) {
			assert !code.isVoid();
			
			if(code.isAddress()) {
				turnAddressIntoValue(code, node);
			}	
		}
		private void turnAddressIntoValue(ASMCodeFragment code, ParseNode node) {
			if(node.getType() == PrimitiveType.INTEGER) {
				code.add(LoadI);
			}	
			else if(node.getType() == PrimitiveType.FLOAT) {
				code.add(LoadF);
			}
			else if(node.getType() == PrimitiveType.BOOLEAN) {
				code.add(LoadC);
			}	
			else if(node.getType() == PrimitiveType.CHAR) {
				code.add(LoadC);
			}	
			else if(node.getType() == PrimitiveType.STRING) {
				code.add(LoadI);
			}
			else {
				assert false : "node " + node;
			}
			code.markAsValue();
		}
		
	    ////////////////////////////////////////////////////////////////////
        // ensures all types of ParseNode in given AST have at least a visitLeave	
		public void visitLeave(ParseNode node) {
			assert false : "node " + node + " not handled in ASMCodeGenerator";
		}
		
		
		
		///////////////////////////////////////////////////////////////////////////
		// constructs larger than statements
		public void visitLeave(ProgramNode node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}
		public void visitLeave(MainBlockNode node) {
			newVoidCode(node);
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}

		///////////////////////////////////////////////////////////////////////////
		// statements and declarations

		public void visitLeave(PrintStatementNode node) {
			newVoidCode(node);
			new PrintStatementGenerator(code, this).generate(node);	
		}
		public void visit(NewlineNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.NEWLINE_PRINT_FORMAT);
			code.add(Printf);
		}
		public void visit(SpaceNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.SPACE_PRINT_FORMAT);
			code.add(Printf);
		}
		public void visit(TabNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.TAB_PRINT_FORMAT);
			code.add(Printf);
		}
		

		public void visitLeave(DeclarationNode node) {
			newVoidCode(node);
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));	
			ASMCodeFragment rvalue = removeValueCode(node.child(1));
			
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			code.add(opcodeForStore(type));
		}
		public void visitLeave(AssignmentStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));	
			ASMCodeFragment rvalue = removeValueCode(node.child(1));
			
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			code.add(opcodeForStore(type));
		}
		private ASMOpcode opcodeForStore(Type type) {
			if(type == PrimitiveType.INTEGER) {
				return StoreI;
			}
			if(type == PrimitiveType.FLOAT) {
				return StoreF;
			}
			if(type == PrimitiveType.BOOLEAN) {
				return StoreC;
			}
			if(type == PrimitiveType.CHAR) {
				return StoreC;
			}
			if(type == PrimitiveType.STRING) {
				return StoreI;
			}
			if(type instanceof ArrayType) {
				return StoreI;
			}
			assert false: "Type " + type + " unimplemented in opcodeForStore()";
			return null;
		}

		///////////////////////////////////////////////////////////////////////////
		// release statement
		public void visitLeave(ReleaseStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment value = removeValueCode(node.child(0));
			
			code.append(value);
			//[...recordAddr]
			code.add(Call, MemoryManager.MEM_ARRAY_RELEASE);
			//[...]
		}
		///////////////////////////////////////////////////////////////////////////
		// clone expression
		public void visitLeave(CloneExpressionNode node) {
			
		}
		///////////////////////////////////////////////////////////////////////////
		// expression list
		public void visitLeave(ExpressionListNode node) {
			newValueCode(node);
			ASMCodeFragment value = removeValueCode(node.child(0));
			
			code.append(value);
			
			
			int size = ((ArrayType)(node.getType())).getSubType().getSize();
			int length = 0;
			
			if(node.nChildren()!=0) {
				length = 1;
				ParseNode child = node.child(0);
				while((child instanceof BinaryOperatorNode) && ((BinaryOperatorNode)child).getOperator() == Punctuator.SEPARATOR) {
					length++;
					child = child.child(0);
				}
			}
			
			code.add(PushI, MemoryManager.MEM_ARRAY_HEADER);
			code.add(PushI, size*length);
			code.add(Add);
			code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
			code.add(PushI, size);
			code.add(PushI, length);
			if(((ArrayType)(node.getType())).getSubType() == PrimitiveType.STRING || ((ArrayType)(node.getType())).getSubType() instanceof ArrayType) {
				code.add(PushI, 4);
			}
			else {
				code.add(PushI, 0);
			}
			
			//[...blockAddr, size, length, status]
			code.add(Call, MemoryManager.MEM_STORE_ARRAY_HEADER);
			code.add(PushI, 1);
			//[...blockAddr, length, flag]
			if(size==1) {
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_ONE_BYTE);
			}
			else if(size==4) {
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_FOUR_BYTE);
			}
			else if(size==8) {
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_EIGHT_BYTE);
			}
			
		}

		///////////////////////////////////////////////////////////////////////////
		// expressions
		public void visitLeave(BinaryOperatorNode node) {
			Lextant operator = node.getOperator();

			if(isComparison(operator)) {
				visitComparisonOperatorNode(node, operator);
			}
			else if(operator == Punctuator.OPEN_BRACKET) {
				visitCastNode(node);
			}
			else if(operator == Keyword.NEW) {
				visitNewNode(node);
			}
			else if(operator == Punctuator.SEPARATOR) {
				newValueCode(node);
				ASMCodeFragment arg1 = removeValueCode(node.child(0));
				ASMCodeFragment arg2 = removeValueCode(node.child(1));
				
				code.append(arg1);
				code.append(arg2);
			}
			else {
				visitNormalBinaryOperatorNode(node);
			}
		}
		
		private boolean isComparison(Lextant operator) {
			if(operator == Punctuator.GREATER || operator == Punctuator.SMALLER || 
					operator == Punctuator.GREATEROREQUAL || operator == Punctuator.SMALLEROREQUAL ||
					operator == Punctuator.EQUAL || operator == Punctuator.NOTEQUAL) {
				return true;
			}
			return false;
				
		}
		
		private void visitNewNode(BinaryOperatorNode node) {
			newValueCode(node);
			Type type = ((ArrayType)(node.getType())).getSubType();
			int size = type.getSize();
			ASMCodeFragment value = removeValueCode(node.child(1));
			
			code.append(value);
			code.add(Duplicate);
			code.add(PushI, size);
			code.add(Multiply);
			code.add(PushI, MemoryManager.MEM_ARRAY_HEADER);
			code.add(Add);
			code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
			code.add(Exchange);
			code.add(PushI, size);
			code.add(Exchange);
			if(((ArrayType)(node.getType())).getSubType() == PrimitiveType.STRING || ((ArrayType)(node.getType())).getSubType() instanceof ArrayType) {
				code.add(PushI, 4);
			}
			else {
				code.add(PushI, 0);
			}
			
			//[...blockAddr, blockAddr, size, length, status]
			code.add(Call, MemoryManager.MEM_STORE_ARRAY_HEADER);
			code.add(PushI, 0);
			//[...blockAddr, length, flag]
			if(size==1) {
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_ONE_BYTE);
			}
			else if(size==4) {
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_FOUR_BYTE);
			}
			else if(size==8) {
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_EIGHT_BYTE);
			}
			
		}
		private void visitComparisonOperatorNode(BinaryOperatorNode node,
				Lextant operator) {

			ASMCodeFragment arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			
			Labeller labeller = new Labeller("compare");
			
			String startLabel = labeller.newLabel("arg1");
			String arg2Label  = labeller.newLabel("arg2");
			String subLabel   = labeller.newLabel("sub");
			String trueLabel  = labeller.newLabel("true");
			String falseLabel = labeller.newLabel("false");
			String joinLabel  = labeller.newLabel("join");
			
			newValueCode(node);
			code.add(Label, startLabel);
			code.append(arg1);
			code.add(Label, arg2Label);
			code.append(arg2);
			code.add(Label, subLabel);
			if(node.child(0).getType() == PrimitiveType.FLOAT) {
				code.add(FSubtract);
				code.add(ConvertI);
			}
			else {
				code.add(Subtract);
			}
			
			if(node.getOperator() == Punctuator.GREATER) {
				code.add(JumpPos, trueLabel);
				code.add(Jump, falseLabel);
			}
			else if(node.getOperator() == Punctuator.SMALLER) {
				code.add(JumpNeg, trueLabel);
				code.add(Jump, falseLabel);
			}
			else if(node.getOperator() == Punctuator.EQUAL) {
				code.add(JumpFalse, trueLabel);
				code.add(Jump, falseLabel);
			}
			else if(node.getOperator() == Punctuator.NOTEQUAL) {
				code.add(JumpFalse, falseLabel);
				code.add(Jump, trueLabel);
			}
			else if(node.getOperator() == Punctuator.GREATEROREQUAL) {
				code.add(JumpNeg, falseLabel);
				code.add(Jump, trueLabel);
			}
			else if(node.getOperator() == Punctuator.SMALLEROREQUAL) {
				code.add(JumpPos, falseLabel);
				code.add(Jump, trueLabel);
			}
			
			
			code.add(Label, trueLabel);
			code.add(PushI, 1);
			code.add(Jump, joinLabel);
			code.add(Label, falseLabel);
			code.add(PushI, 0);
			code.add(Jump, joinLabel);
			code.add(Label, joinLabel);

		}
		
		private void visitCastNode(BinaryOperatorNode node) {
			newValueCode(node);
			ASMCodeFragment value = removeValueCode(node.child(0));
			code.append(value);
			
			if(node.child(0).getType()==PrimitiveType.FLOAT && node.getType()==PrimitiveType.INTEGER) {
				code.add(ConvertI);
			}
			else if(node.child(0).getType()==PrimitiveType.INTEGER && node.getType()==PrimitiveType.FLOAT) {
				code.add(ConvertF);
			}
			else if(node.child(1).getType()==PrimitiveType.BOOLEAN) {
				code.add(PushI,1);
				code.add(And);
			}
			
			//code.add(opcodeForStore(node.child(1).getType()));
		}
		
		private void visitNormalBinaryOperatorNode(BinaryOperatorNode node) {
			newValueCode(node);
			ASMCodeFragment arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			
			code.append(arg1);
			code.append(arg2);
			
			if(node.getOperator() == Punctuator.DIVIDE) {
				code.add(Duplicate);
				if(node.child(1).getType()==PrimitiveType.FLOAT) {
					code.add(ConvertI);
					code.add(JumpFalse, RunTime.FLOAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
				}
				else {
					code.add(JumpFalse, RunTime.INTEGER_DIVIDE_BY_ZERO_RUNTIME_ERROR);
				}
			}
			ASMOpcode opcode = opcodeForOperator(node.getOperator(),node.getType());
			code.add(opcode);							// type-dependent! (opcode is different for floats and for ints)
		}
		private ASMOpcode opcodeForOperator(Lextant lextant, Type type) {
			assert(lextant instanceof Punctuator);
			Punctuator punctuator = (Punctuator)lextant;
			if(type == PrimitiveType.INTEGER) {
				switch(punctuator) {
				case ADD: 	   		return Add;			
				case SUBTRACT:		return Subtract;
				case MULTIPLY: 		return Multiply;
				case DIVIDE:		return Divide;
				default:
					assert false : "unimplemented operator in opcodeForOperator";
				}
			}
			else if(type == PrimitiveType.FLOAT) {
				switch(punctuator) {
				case ADD: 	   		return FAdd;			
				case SUBTRACT:		return FSubtract;
				case MULTIPLY: 		return FMultiply;
				case DIVIDE:		return FDivide;
				default:
					assert false : "unimplemented operator in opcodeForOperator";
				}
			}
			
			return null;
		}

		///////////////////////////////////////////////////////////////////////////
		// leaf nodes (ErrorNode not necessary)
		public void visit(BooleanConstantNode node) {
			newValueCode(node);
			code.add(PushI, node.getValue() ? 1 : 0);
		}
		public void visit(CharConstantNode node) {
			newValueCode(node);
			code.add(PushI, node.getValue());
		}
		public void visit(StringConstantNode node) {
			newValueCode(node);
			
			code.add(PushI, node.getValue().length() + MemoryManager.MEM_STRING_RECORD_EXTRA);
			
			// [ ...recordSize]
			code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);  
			code.add(Duplicate);
			//[ ...blockAddr, blockAddr]
			
			code.add(PushI, node.getValue().length());
			
			//[ ...blockAddr, blockAddr, length]
			code.add(Call, MemoryManager.MEM_STORE_STRING_HEADER);
			//[ ...blockAddr]
		
			for(int i = 0; i<=node.getValue().length(); i++) {
				code.add(Duplicate);
				code.add(PushI, i);
				code.add(PushI, MemoryManager.MEM_STRING_CONTENT_OFFSET);
				code.add(Add);
				code.add(Add);
				if(i!=node.getValue().length()) {
					code.add(PushI, (node.getValue()).charAt(i));
				}
				else {
					code.add(PushI, 0);
				}
				code.add(StoreC);
			}
			
			
			
			/*code.add(DataS, node.getValue());
			code.add(PushD, RunTime.STRING_CONSTANT_BLOCK);
			code.add(PushI, node.getOffset());
			code.add(Add);*/
		}
		public void visit(IdentifierNode node) {
			newAddressCode(node);
			Binding binding = node.getBinding();
			
			binding.generateAddress(code);
		}		
		public void visit(IntegerConstantNode node) {
			newValueCode(node);
			
			code.add(PushI, node.getValue());
		}
		public void visit(FloatConstantNode node) {
			newValueCode(node);
			
			code.add(PushF, node.getValue());
		}
	}

} 
