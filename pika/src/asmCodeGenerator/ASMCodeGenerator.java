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
import semanticAnalyzer.types.LambdaType;
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
		code.append( frameStackPointer());
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
	private ASMCodeFragment frameStackPointer() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		code.add(DLabel, RunTime.FRAME_STACK_POINTER);
		code.add(DataZ, 4);
		code.add(DLabel, RunTime.STACK_POINTER);
		code.add(DataZ, 4);
		
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
		int ifNum = 1;
		int whileNum = 1;
		
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
			else if(node.getType() == PrimitiveType.RATIONAL) {
				code.add(Duplicate);
				code.add(LoadI);
				code.add(Exchange);
				code.add(PushI,4);
				code.add(Add);
				code.add(LoadI);
			}
			else if(node.getType() instanceof ArrayType) {
				code.add(LoadI);
			}
			else if(node.getType() instanceof LambdaType) {
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
		
		
		////////////////////////////////////////////////////////////////////////////
		// function definitions
		public void visitLeave(FunctionDeclarationNode node) {
			newVoidCode(node);
			
			ASMCodeFragment identifier = removeAddressCode(node.child(0));
			ASMCodeFragment function = removeValueCode(node.child(1));
			
			code.append(identifier);
			code.append(function);
			code.add(StoreI);
		}
		
		public void visitLeave(LambdaNode node) {
			newValueCode(node);
			
			code.append(removeValueCode(node.child(1)));
		}
		
		public void visitLeave(FunctionBodyNode node) {
			newValueCode(node);
			Labeller labeller = new Labeller("function");
			String skip = labeller.newLabel("skip");
			
			code.add(PushPC);
			code.add(PushI, 3);
			code.add(Add);
			code.add(Jump, skip);
			
			// addr->MEM[sp]
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(PushI, -4);
			code.add(Add);
			code.add(Exchange);
			code.add(StoreI);
			
			// fp->MEM[sp-4]
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(PushI, -8);
			code.add(Add);
			code.add(PushD, RunTime.FRAME_STACK_POINTER);
			code.add(LoadI);
			code.add(StoreI);
			
			// sp->fp
			code.add(PushD, RunTime.FRAME_STACK_POINTER);
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(StoreI);
			
			// fp-scopeSize -> sp
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(PushD, RunTime.FRAME_STACK_POINTER);
			code.add(LoadI);
			code.add(PushI, node.getScope().getAllocatedSize()*(-1));
			code.add(Add);
			code.add(StoreI);
			
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		
			code.add(Jump, RunTime.FUNCTION_WITHOUT_RETURN_RUNTIME_ERROR);
			code.add(Label, skip);
		}
		
		public void visitLeave(LambdaParameterTypeNode node) {
			
		}
		
		public void visitLeave(ParameterNode node) {
			
		}
		///////////////////////////////////////////////////////////////////////////
		// return statement
		public void visitLeave(ReturnStatementNode node) {
			newVoidCode(node);
			if(node.nChildren()==1) {
				ASMCodeFragment expr = removeValueCode(node.child(0));
				
				code.append(expr);
			}

			
			// fp->sp
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(PushD, RunTime.FRAME_STACK_POINTER);
			code.add(LoadI);
			code.add(StoreI);
			
			// mem[sp-8]->fp
			code.add(PushD, RunTime.FRAME_STACK_POINTER);
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(PushI, -8);
			code.add(Add);
			code.add(LoadI);
			code.add(StoreI);
			
			// mem[sp]->stack
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(PushI, -4);
			code.add(Add);
			code.add(LoadI);
			
			// [...addr]
			code.add(Return);
		}
		
		///////////////////////////////////////////////////////////////////////////
		// call
		public void visitLeave(CallStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment func = removeValueCode(node.child(0));
			
			code.append(func);
			if(!node.child(0).getType().equivalent(PrimitiveType.VOID)) {
				code.add(Pop);
			}
		}
		///////////////////////////////////////////////////////////////////////////
		// function invocation
		public void visitLeave(FunctionInvocationNode node) {
			newValueCode(node);
			
			ASMCodeFragment functionAddr = removeValueCode(node.child(0));
			ASMCodeFragment parameter = removeValueCode(node.child(1));
			
			code.append(parameter);
			code.append(functionAddr);
			code.add(CallV);
			
			int paraSize = 0;
			LambdaType type = (LambdaType)node.child(0).getType();
			
			for(Type paraType:type.getParaType()) {
				paraSize += paraType.getSize();
			}
			
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(PushD, RunTime.STACK_POINTER);
			code.add(LoadI);
			code.add(PushI, paraSize);
			code.add(Add);
			code.add(StoreI);
		}
		
		public void visitLeave(ParaExpressionListNode node) {
			newValueCode(node);
			
			for(ParseNode para : node.getChildren()) {
				// sp-size->sp
				code.add(PushD, RunTime.STACK_POINTER);
				code.add(PushD, RunTime.STACK_POINTER);
				code.add(LoadI);
				code.add(PushI, para.getType().getSize()*(-1));
				code.add(Add);
				code.add(StoreI);
				
				// para -> MEM[sp]
				
				if(para.getType().getSize()==1) {
					code.add(PushD, RunTime.STACK_POINTER);
					code.add(LoadI);
					code.append(removeValueCode(para));
					code.add(StoreC);
				}
				else if(para.getType().getSize()==4) {
					code.add(PushD, RunTime.STACK_POINTER);
					code.add(LoadI);
					code.append(removeValueCode(para));
					code.add(StoreI);
				}
				else if(para.getType().getSize()==8) {
					if(para.getType() == PrimitiveType.RATIONAL) {
						code.append(removeValueCode(para));
						code.add(PushD, RunTime.STACK_POINTER);
						code.add(LoadI);
						code.add(PushI,4);
						code.add(Add);
						code.add(Exchange);
						code.add(StoreI);
						code.add(PushD, RunTime.STACK_POINTER);
						code.add(LoadI);
						code.add(Exchange);
						code.add(StoreI);
					}
					else {
						code.add(PushD, RunTime.STACK_POINTER);
						code.add(LoadI);
						code.append(removeValueCode(para));
						code.add(StoreF);
					}
					
				}
				else {
					assert 1==0;
				}
				
				
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
			
			if(type == PrimitiveType.RATIONAL) {
				code.add(Call, MemoryManager.MEM_RAT_STORE);
			}
			else {
				code.add(opcodeForStore(type));
			}
			
			
		}
		public void visitLeave(GlobalDeclarationNode node) {
			newVoidCode(node);
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));	
			ASMCodeFragment rvalue = removeValueCode(node.child(1));
			
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			
			if(type == PrimitiveType.RATIONAL) {
				code.add(Call, MemoryManager.MEM_RAT_STORE);
			}
			else {
				code.add(opcodeForStore(type));
			}
			
			
		}
		public void visitLeave(AssignmentStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));	
			ASMCodeFragment rvalue = removeValueCode(node.child(1));
			
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			if(type == PrimitiveType.RATIONAL) {
				code.add(Call, MemoryManager.MEM_RAT_STORE);
			}
			else {
				code.add(opcodeForStore(type));
			}

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
			if(type instanceof LambdaType) {
				return StoreI;
			}
			assert false: "Type " + type + " unimplemented in opcodeForStore()";
			return null;
		}

		///////////////////////////////////////////////////////////////////////////
		// control flow statements
		public void visitLeave(IfStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment condition = removeValueCode(node.child(0));
			ASMCodeFragment trueContent = removeVoidCode(node.child(1));
			
			code.append(condition);
			code.add(JumpFalse, "$ELSE"+this.ifNum);
			code.append(trueContent);
			code.add(Jump, "$IFEND"+this.ifNum);
			code.add(Label, "$ELSE"+this.ifNum);
				if(node.nChildren()==3) {
					code.append(removeVoidCode(node.child(2)));
				}
			code.add(Label, "$IFEND"+this.ifNum);
			
			this.ifNum++;
			
		}
		
		public void visitLeave(WhileStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment condition = removeValueCode(node.child(0));
			ASMCodeFragment content = removeVoidCode(node.child(1));
			
			code.add(Label, "$WhileLoop"+this.whileNum);
				code.append(condition);
				code.add(JumpFalse, "$WhileEnd"+this.whileNum);
				code.append(content);
				code.add(Jump, "$WhileLoop"+this.whileNum);
				
			code.add(Label, "$WhileEnd"+this.whileNum);
			
			this.whileNum++;
		}
		
		public void visitLeave(ForIndexStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment iden = removeAddressCode(node.child(0));
			ASMCodeFragment expr = removeValueCode(node.child(1));
			ASMCodeFragment content = removeVoidCode(node.child(2));
			
			code.append(iden);
			code.add(Duplicate);
			code.add(PushI,0);
			code.add(StoreI);
			
			
			code.add(Label, "$WhileLoop"+this.whileNum);
				code.add(Duplicate);
				code.add(LoadI);
				
				code.append(expr);
				if(node.child(1).getType()==PrimitiveType.STRING) {
					code.add(PushI, MemoryManager.MEM_STRING_LENGTH_OFFSET);
				}
				else {
					code.add(PushI, MemoryManager.MEM_ARRAY_LENGTH_OFFSET);
				}
				code.add(Add);
				code.add(LoadI);
				code.add(Subtract);
				
				code.add(JumpFalse, "$WhileEnd"+this.whileNum);
				code.append(content);
				code.add(Duplicate);
				code.add(Duplicate);
				code.add(LoadI);
				code.add(PushI,1);
				code.add(Add);
				code.add(StoreI);
				code.add(Jump, "$WhileLoop"+this.whileNum);
			
		code.add(Label, "$WhileEnd"+this.whileNum);
			code.add(Pop);
		
		this.whileNum++;
		}
		
		public void visitLeave(ForElemStatementNode node) {
			newVoidCode(node);
			ASMCodeFragment iden = removeAddressCode(node.child(1));
			ASMCodeFragment expr = removeValueCode(node.child(0));
			ASMCodeFragment content = removeVoidCode(node.child(2));
			
			
			code.add(DLabel, "-for-elem"+this.whileNum);
			code.add(DataZ, 4);
			code.add(PushD, "-for-elem"+this.whileNum);
			code.add(PushI, 0);
			code.add(StoreI);
			
			
			code.add(Label, "$WhileLoop"+this.whileNum);
				code.append(expr);
				code.add(Duplicate);
				code.add(PushD, "-for-elem"+this.whileNum);
				code.add(LoadI);
				code.add(Exchange);
				
				if(node.child(0).getType()==PrimitiveType.STRING) {
					code.add(PushI, MemoryManager.MEM_STRING_LENGTH_OFFSET);
				}
				else {
					code.add(PushI, MemoryManager.MEM_ARRAY_LENGTH_OFFSET);
				}
				code.add(Add);
				code.add(LoadI);
				code.add(Subtract);
				
				code.add(JumpFalse, "$WhileEnd"+this.whileNum);
				
				//[...recordAddr, index]
				
				if(node.child(0).getType()==PrimitiveType.STRING) {
					code.add(PushD, "-for-elem"+this.whileNum);
					code.add(LoadI);
					code.add(Call, MemoryManager.MEM_STRING_INDEX);
					code.add(LoadC);
					code.append(iden);
					code.add(Exchange);
					code.add(StoreC);
				}
				else {
					code.append(iden);
					code.add(Exchange);
					code.add(PushD, "-for-elem"+this.whileNum);
					code.add(LoadI);
					if(node.child(1).getType() == PrimitiveType.RATIONAL) {
						code.add(PushI,1);
					}
					else {
						code.add(PushI, 0);
					}
					code.add(Call, MemoryManager.MEM_ARRAY_INDEX);
					if(node.child(1).getType() == PrimitiveType.RATIONAL) {
						code.add(Duplicate);
						code.add(LoadI);
						code.add(Exchange);
						code.add(PushI,4);
						code.add(Add);
						code.add(LoadI);
						code.add(Call,MemoryManager.MEM_RAT_STORE);
					}
					else {
						Type type = node.child(1).getType();
						if(type == PrimitiveType.INTEGER) {
							code.add(LoadI);
						}	
						else if(type == PrimitiveType.FLOAT) {
							code.add(LoadF);
						}
						else if(type== PrimitiveType.BOOLEAN) {
							code.add(LoadC);
						}	
						else if(type== PrimitiveType.CHAR) {
							code.add(LoadC);
						}	
						else if(type== PrimitiveType.STRING) {
							code.add(LoadI);
						}
						else if(type instanceof ArrayType) {
							code.add(LoadI);
						}
						else if(type instanceof LambdaType) {
							code.add(LoadI);
						}
						else {
							assert false : "node " + node;
						}
						
						code.add(opcodeForStore(node.child(1).getType()));
					}
				}
				//[...]
				
				code.append(content);
				code.add(PushD, "-for-elem"+this.whileNum);
				code.add(PushD, "-for-elem"+this.whileNum);
				code.add(LoadI);
				code.add(PushI,1);
				code.add(Add);
				code.add(StoreI);
				code.add(Jump, "$WhileLoop"+this.whileNum);
			
		code.add(Label, "$WhileEnd"+this.whileNum);
			code.add(Pop);
		
		this.whileNum++;
		}
		public void visit(BreakStatementNode node) {
			newVoidCode(node);
			
			code.add(Jump, "$WhileEnd"+this.whileNum);
		}
		
		public void visit(ContinueStatementNode node) {
			newVoidCode(node);
			
			code.add(Jump, "$WhileLoop"+this.whileNum);
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
			newValueCode(node);
			ASMCodeFragment value = removeValueCode(node.child(0));
			
			code.append(value);
			//[...recordAddr]
			code.add(Call,MemoryManager.MEM_ARRAY_CLONE);
			//[...cloneAddr]
		}
		
		public void visitLeave(ReverseExpressionNode node) {
			newValueCode(node);
			ASMCodeFragment str = removeValueCode(node.child(0));
			
			code.append(str);
			if(node.getType()==PrimitiveType.STRING) {
				code.add(Call, MemoryManager.MEM_STRING_REVERSE);
			}
			else if (((ArrayType)node.getType()).getSubType() == PrimitiveType.RATIONAL){
				code.add(PushI,1);
				code.add(Call, MemoryManager.MEM_ARRAY_REVERSE);
			}
			else {
				code.add(PushI,0);
				code.add(Call, MemoryManager.MEM_ARRAY_REVERSE);
			}
			
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
			if(((ArrayType)(node.getType())).getSubType() instanceof ArrayType) {
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
			else if(size==4 || ((ArrayType)(node.getType())).getSubType() == PrimitiveType.RATIONAL) {
				if( ((ArrayType)(node.getType())).getSubType() == PrimitiveType.RATIONAL) {
					code.add(Exchange);
					code.add(PushI, 2);
					code.add(Multiply);
					code.add(Exchange);
				}
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
			else if(operator == Punctuator.OVER) {
				newValueCode(node);
				ASMCodeFragment arg1 = removeValueCode(node.child(0));
				ASMCodeFragment arg2 = removeValueCode(node.child(1));
				
				code.append(arg1);
				code.append(arg2);
				code.add(Duplicate);
				code.add(JumpFalse, RunTime.RAT_WITH_ZERO_DENOMINATOR_RUNTIME_ERROR);
				code.add(Call, MemoryManager.MEM_RAT_GCD);
			}
			else if(operator == Punctuator.EXPRESSOVER) {
				newValueCode(node);
				ASMCodeFragment arg1 = removeValueCode(node.child(0));
				ASMCodeFragment arg2 = removeValueCode(node.child(1));
				
				if(node.child(0).getType() == PrimitiveType.RATIONAL) {
					code.append(arg1);
					code.add(Exchange);
					code.append(arg2);
					
					code.add(Duplicate);
					code.add(JumpFalse, RunTime.RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);	
					
					code.add(Multiply);
					code.add(Exchange);
					code.add(Divide);
				}
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.append(arg1);
					code.append(arg2);
					code.add(Duplicate);
					code.add(JumpFalse, RunTime.RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
					code.add(ConvertF);
					code.add(FMultiply);
					code.add(ConvertI);
					
				}
			}
			else if(operator == Punctuator.RATIONALIZE) {
				newValueCode(node);
				ASMCodeFragment arg1 = removeValueCode(node.child(0));
				ASMCodeFragment arg2 = removeValueCode(node.child(1));
				
				if(node.child(0).getType() == PrimitiveType.RATIONAL) {
					code.append(arg1);
					code.append(arg2);
					code.add(Duplicate);
					code.add(JumpFalse, RunTime.RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
					
					code.add(Call, MemoryManager.MEM_RAT_AID);
					code.add(Call, MemoryManager.MEM_RAT_GCD);
				}
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.append(arg2);
					code.add(Duplicate);
					code.add(Duplicate);
					code.add(JumpFalse, RunTime.RAT_DIVIDE_BY_ZERO_RUNTIME_ERROR);
					code.append(arg1);
					code.add(Exchange);
					code.add(ConvertF);
					code.add(FMultiply);
					code.add(ConvertI);
					code.add(Exchange);
					
					code.add(Call, MemoryManager.MEM_RAT_GCD);
				}
			}
			else if(operator == Punctuator.ARRAY_INDEX) {				
				newAddressCode(node);
				ASMCodeFragment arg1 = removeValueCode(node.child(0));
				ASMCodeFragment arg2 = removeValueCode(node.child(1));
				
				code.append(arg1);
				code.append(arg2);
				
				if(node.child(0).getType().equivalent(PrimitiveType.STRING)) {
					if(node.nChildren()==2) {
						code.add(Call, MemoryManager.MEM_STRING_INDEX);	
						return;
					}
					else {
						assert 0==1;
					}
					
				}
				
				if(((ArrayType)(node.child(0).getType())).getSubType()==PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				//[...arrayAddr, index, flag]
				code.add(Call, MemoryManager.MEM_ARRAY_INDEX);
				//[...value]
			}
			else if(operator == Punctuator.STRING_RANGE) {
				newValueCode(node);
				code.append(removeValueCode(node.child(0)));
				code.append(removeValueCode(node.child(1)));
				code.append(removeValueCode(node.child(2)));
				code.add(Call, MemoryManager.MEM_STRING_RANGE);
			}
			else if(operator == Keyword.MAP) {
				newValueCode(node);
				ASMCodeFragment lamb = removeValueCode(node.child(1));
				ASMCodeFragment array = removeValueCode(node.child(0));
				
				code.append(array);
				
				code.add(Duplicate);
				code.add(Duplicate);
				code.add(PushI, MemoryManager.MEM_ARRAY_LENGTH_OFFSET);
				code.add(Add);
				code.add(LoadI);
				code.add(PushI, ((LambdaType)node.child(1).getType()).getReturnType().getSize());
				code.add(Multiply);
				code.add(PushI, MemoryManager.MEM_ARRAY_HEADER);
				code.add(Add);
				code.add(Call, MemoryManager.MEM_MANAGER_ALLOCATE);
				//[...arrayAddr, arrayAddr, newAddr]
				
				code.add(Exchange);
				code.add(PushI, MemoryManager.MEM_ARRAY_LENGTH_OFFSET);
				code.add(Add);
				code.add(LoadI);
				code.add(PushI, ((LambdaType)node.child(1).getType()).getReturnType().getSize());
				code.add(Exchange);
				
				if(((LambdaType)node.child(1).getType()).getReturnType() instanceof ArrayType) {
					code.add(PushI, 4);
				}
				else {
					code.add(PushI, 0);
				}
				
				code.add(Call, MemoryManager.MEM_STORE_ARRAY_HEADER);
				code.add(Pop);
				code.append(lamb);
				if(((LambdaType)node.child(1).getType()).getParaType().get(0) == PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				
				if(((LambdaType)node.child(1).getType()).getReturnType() == PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				//[...arrayAddr, newAddr, lamb, flag1, flag2]
				
				code.add(Call, MemoryManager.MEM_ARRAY_MAP);
				//[...newAddr]
				
				
			}
			else if(operator == Keyword.REDUCE) {
				newValueCode(node);
				ASMCodeFragment lamb = removeValueCode(node.child(1));
				ASMCodeFragment array = removeValueCode(node.child(0));
				
				code.append(array);
				code.add(Call, MemoryManager.MEM_ARRAY_CLONE);
				code.append(lamb);
				if(((LambdaType)node.child(1).getType()).getParaType().get(0) == PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				
				//[...newAddr, lamb, flag]
				code.add(Call, MemoryManager.MEM_ARRAY_REDUCE);
				//[...newAddr]
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
		
		public void visitLeave(FoldExpressionNode node) {
			newValueCode(node);
			
			if(node.nChildren() == 2) {
				code.append(removeValueCode(node.child(0)));
				code.append(removeValueCode(node.child(1)));
				if(((ArrayType)node.child(0).getType()).getSubType() == PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				
				//[...arrayAddr, lamb, flag]
				code.add(Call, MemoryManager.MEM_ARRAY_FOLD_TWO);
			}
			
			//[...arrayAddr, lamb, base, ]
			else if(node.nChildren() == 3) {
				code.append(removeValueCode(node.child(2)));
				code.append(removeValueCode(node.child(0)));
				code.append(removeValueCode(node.child(1)));
				
				
				code.add(PushI, node.child(2).getType().getSize());
				if(node.child(2).getType() == PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				
				if(((ArrayType)node.child(0).getType()).getSubType() == PrimitiveType.RATIONAL) {
					code.add(PushI, 1);
				}
				else {
					code.add(PushI, 0);
				}
				
				//[...base, arrayAddr, lamb, size, flagU, flagT]
				code.add(Call, MemoryManager.MEM_ARRAY_FOLD_THREE);
			}
		}
		private void visitNewNode(BinaryOperatorNode node) {
			newValueCode(node);
			Type type = ((ArrayType)(node.getType())).getSubType();
			int size = type.getSize();
			ASMCodeFragment value = removeValueCode(node.child(1));
			
			code.append(value);
			code.add(Duplicate);
			code.add(JumpNeg, RunTime.NEW_NEG_ERROR);
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
			}
			else if(node.child(0).getType() == PrimitiveType.RATIONAL) {
				code.add(Call, MemoryManager.MEM_RAT_SUBTRACT);
				code.add(Multiply);
			}
			else {
				code.add(Subtract);
			}
			
			if(node.getOperator() == Punctuator.GREATER) {
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.add(JumpFPos, trueLabel);
				}
				else {
					code.add(JumpPos, trueLabel);
				}
				code.add(Jump, falseLabel);
			}
			else if(node.getOperator() == Punctuator.SMALLER) {
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.add(JumpFNeg, trueLabel);
				}
				else {
					code.add(JumpNeg, trueLabel);
				}
				code.add(Jump, falseLabel);
			}
			else if(node.getOperator() == Punctuator.EQUAL) {
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.add(JumpFZero, trueLabel);
				}
				else {
					code.add(JumpFalse, trueLabel);
				}
				code.add(Jump, falseLabel);
			}
			else if(node.getOperator() == Punctuator.NOTEQUAL) {
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.add(JumpFZero, falseLabel);
				}
				else {
					code.add(JumpFalse, falseLabel);
				}
				code.add(Jump, trueLabel);
			}
			else if(node.getOperator() == Punctuator.GREATEROREQUAL) {
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.add(JumpFNeg, falseLabel);
				}
				else {
					code.add(JumpNeg, falseLabel);
				}
				code.add(Jump, trueLabel);
			}
			else if(node.getOperator() == Punctuator.SMALLEROREQUAL) {
				if(node.child(0).getType() == PrimitiveType.FLOAT) {
					code.add(JumpFPos, falseLabel);
				}
				else {
					code.add(JumpPos, falseLabel);
				}
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
			else if(node.child(0).getType()==PrimitiveType.INTEGER && node.getType()==PrimitiveType.CHAR) {
				code.add(PushI, 127);
				code.add(BTAnd);
			}
			else if(node.child(0).getType()==PrimitiveType.INTEGER && node.getType()==PrimitiveType.FLOAT) {
				code.add(ConvertF);
			}else if(node.child(0).getType()==PrimitiveType.CHAR && node.getType()==PrimitiveType.FLOAT) {
				code.add(ConvertF);
			}
			else if(node.child(1).getType()==PrimitiveType.BOOLEAN) {
				code.add(PushI,1);
				code.add(And);
			}
			else if(node.child(0).getType()==PrimitiveType.RATIONAL && node.getType()==PrimitiveType.FLOAT) {
				code.add(ConvertF);
				code.add(Exchange);
				code.add(ConvertF);
				code.add(Exchange);
				code.add(FDivide);
			}
			else if(node.child(0).getType()==PrimitiveType.RATIONAL && node.getType()==PrimitiveType.INTEGER) {
				code.add(Divide);
			}
			else if(node.child(0).getType()==PrimitiveType.INTEGER && node.getType()==PrimitiveType.RATIONAL) {
				code.add(PushI, 1);
			}
			else if(node.child(0).getType()==PrimitiveType.CHAR && node.getType()==PrimitiveType.RATIONAL) {
				code.add(PushI, 1);
			}
			else if(node.child(0).getType()==PrimitiveType.FLOAT && node.getType()==PrimitiveType.RATIONAL) {
				code.add(PushI, 223092870);
				code.add(ConvertF);
				code.add(FMultiply);
				code.add(ConvertI);
				code.add(PushI, 223092870);
				
				code.add(Call, MemoryManager.MEM_RAT_GCD);
			}
			
			
			
			//code.add(opcodeForStore(node.child(1).getType()));
		}
		
		private void visitNormalBinaryOperatorNode(BinaryOperatorNode node) {
			newValueCode(node);
			ASMCodeFragment arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			
			code.append(arg1);
			code.append(arg2);
			
			if(node.getOperator() == Punctuator.ADD && node.getType()==PrimitiveType.STRING) {
				if(node.child(0).getType()==node.child(1).getType()) {
					code.add(Call, MemoryManager.MEM_STRING_CONCATENATION);
					return;
				}
				else if(node.child(0).getType() == PrimitiveType.STRING && node.child(1).getType() == PrimitiveType.CHAR) {
					code.add(Exchange);
					code.add(Call, MemoryManager.MEM_STRING_CHAR);
					return;
				}
				else if(node.child(1).getType() == PrimitiveType.STRING && node.child(0).getType() == PrimitiveType.CHAR) {
					code.add(Call, MemoryManager.MEM_CHAR_STRING);
					return;
				}
			}
			if(node.getType() == PrimitiveType.RATIONAL) {
				if(node.getOperator() == Punctuator.ADD) {
					code.add(Call, MemoryManager.MEM_RAT_ADD);
				}
				else if(node.getOperator() == Punctuator.SUBTRACT) {
					code.add(Call, MemoryManager.MEM_RAT_SUBTRACT);
				}
				else if(node.getOperator() == Punctuator.DIVIDE) {
					code.add(Call, MemoryManager.MEM_RAT_DIVIDE);
				}else if(node.getOperator() == Punctuator.MULTIPLY) {
					code.add(Call, MemoryManager.MEM_RAT_MULTIPLY);
				}
				
				return;
			}
			
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
			else if(type == PrimitiveType.BOOLEAN) {
				switch(punctuator) {
				case AND:			return And;
				case OR:			return Or;
				default:
					assert false : "unimplemented operator in opcodeForOperator";
				}
			}
			
			return null;
		}

		///////////////////////////////////////////////////////////////////////////
		// !()
		public void visitLeave(NotExpressionNode node) {
			newValueCode(node);
			ASMCodeFragment arg = removeValueCode(node.child(0));
			
			code.append(arg);
			code.add(BNegate);
		}
		
		///////////////////////////////////////////////////////////////////////////
		// length
		public void visitLeave(LengthExpressionNode node) {
			newValueCode(node);
			ASMCodeFragment arg = removeValueCode(node.child(0));
			
			if(node.child(0).getType().equivalent(PrimitiveType.STRING)) {
				code.append(arg);
				code.add(PushI, MemoryManager.MEM_STRING_LENGTH_OFFSET);
				code.add(Add);
				code.add(LoadI);
			}
			else {
				code.append(arg);
				code.add(PushI, MemoryManager.MEM_ARRAY_LENGTH_OFFSET);
				code.add(Add);
				code.add(LoadI);
			}
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
