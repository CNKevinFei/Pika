package semanticAnalyzer;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import lexicalAnalyzer.Keyword;
import logging.PikaLogger;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import parseTree.nodeTypes.*;
import semanticAnalyzer.signatures.FunctionSignature;
import semanticAnalyzer.signatures.FunctionSignatures;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.LambdaType;
import semanticAnalyzer.types.ArrayType;
import semanticAnalyzer.types.Type;
import semanticAnalyzer.PromotionType;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.LextantToken;
import tokens.Token;

class SemanticAnalysisVisitor extends ParseNodeVisitor.Default {
	private int stringBlockSize = 0;
	private int whileLoopNun = 0;
	
	/*@Override
	public void visitLeave(ParseNode node) {
		throw new RuntimeException("Node class unimplemented in SemanticAnalysisVisitor: " + node.getClass());
	}*/
	
	///////////////////////////////////////////////////////////////////////////
	// constructs larger than statements
	@Override
	public void visitEnter(ProgramNode node) {
		enterProgramScope(node);
	}
	public void visitLeave(ProgramNode node) {
		leaveScope(node);
		node.setStringBlocksize(this.stringBlockSize);
	}
	public void visitEnter(MainBlockNode node) {
		enterSubscope(node);
	}
	public void visitLeave(MainBlockNode node) {
		leaveScope(node);
	}
	public void visitEnter(LambdaNode node) {
		enterParameterScope(node);
	}
	public void visitLeave(LambdaNode node) {
		leaveScope(node);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// helper methods for scoping.
	private void enterProgramScope(ParseNode node) {
		node.getScope().enter();
	}	
	//@SuppressWarnings("unused")
	private void enterSubscope(ParseNode node) {
		Scope baseScope = node.getLocalScope();
		Scope scope = baseScope.createSubscope();
		node.setScope(scope);
	}
	private void enterParameterScope(ParseNode node) {
		Scope scope = Scope.createParameterScope();

		node.setScope(scope);
	}
	private void leaveScope(ParseNode node) {
		node.getScope().leave();
	}
	
	///////////////////////////////////////////////////////////////////////////
	// statements and declarations
	@Override
	public void visitLeave(PrintStatementNode node) {
	}
	@Override
	public void visitLeave(DeclarationNode node) {
		IdentifierNode identifier = (IdentifierNode) node.child(0);
		ParseNode initializer = node.child(1);
		
		Type declarationType = initializer.getType();
		if(declarationType == PrimitiveType.NO_TYPE) {
			logError("Variable cannot be declared with an empty expression list.");
			node.setType(PrimitiveType.ERROR);
			return;
		}
		node.setType(declarationType);
		
		identifier.setType(declarationType);
		identifier.setConOrVar(node.lextantToken());
		identifier.setStatic(node.getStatic());
		
		addBinding(identifier, declarationType);
	}
	@Override
	public void visitLeave(AssignmentStatementNode node) {
		ParseNode variable = node.child(0);
		ParseNode value = node.child(1);
		
		if(!((variable instanceof IdentifierNode)||(variable.getToken().isLextant(Punctuator.ARRAY_INDEX)&&variable.child(0).getType()instanceof ArrayType))) {
			logError("left value is not targetable at "+node.getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
		
		Type assignmentType = variable.getType();
		node.setType(assignmentType);
		
		if(variable instanceof IdentifierNode && !((IdentifierNode)variable).satisfy(value)) {
			
			if(PromotionType.canPromote(value.getType())&&(PromotionType.hasChildren((PrimitiveType)(value.getType()), Arrays.asList((PrimitiveType)assignmentType)))) {
				Token token;
				if(assignmentType.equivalent(PrimitiveType.INTEGER))
					token = LextantToken.fakeToken("int", Keyword.INT);
				else if(assignmentType.equivalent(PrimitiveType.FLOAT))
					token = LextantToken.fakeToken("float", Keyword.FLOAT);
				else if(assignmentType.equivalent(PrimitiveType.RATIONAL))
					token = LextantToken.fakeToken("rational", Keyword.RAT);
				else
				{
					token = null;
					logError("assignment statement");
				}
				node.replaceChild(node.child(1), BinaryOperatorNode.withChildren(LextantToken.fakeToken("type cast", Punctuator.OPEN_BRACKET), node.child(1), new TypeConstantNode(token)));
				node.child(1).setType(assignmentType);
				return;
			}
			node.setType(PrimitiveType.ERROR);
			logError("Assignment of "+"\""+value.getType()+"\""+" to "+"\""+((IdentifierNode)variable).getConOrVar()+" "+variable.getType()+"\""+" failed.");
		}
		else if(variable.getToken().isLextant(Punctuator.ARRAY_INDEX)&&!(value.getType().equivalent(variable.getType()))) {
			if(PromotionType.canPromote(value.getType())&&(PromotionType.hasChildren((PrimitiveType)(value.getType()), Arrays.asList((PrimitiveType)assignmentType)))) {
				Token token;
				if(assignmentType.equivalent(PrimitiveType.INTEGER))
					token = LextantToken.fakeToken("int", Keyword.INT);
				else if(assignmentType.equivalent(PrimitiveType.FLOAT))
					token = LextantToken.fakeToken("float", Keyword.FLOAT);
				else if(assignmentType.equivalent(PrimitiveType.RATIONAL))
					token = LextantToken.fakeToken("rational", Keyword.RAT);
				else
				{
					token = null;
					logError("assignment statement");
				}
				node.replaceChild(node.child(1), BinaryOperatorNode.withChildren(LextantToken.fakeToken("type cast", Punctuator.OPEN_BRACKET), node.child(1), new TypeConstantNode(token)));
				node.child(1).setType(assignmentType);
				return;
			}
			node.setType(PrimitiveType.ERROR);
			logError("Assignment of "+"\""+value.getType()+"\""+" to "+"\""+" "+variable.getType()+"\""+" failed.");
		}
	}
	///////////////////////////////////////////////////////////////////////////
	// Lambda part
	@Override
	public void visitLeave(LambdaParameterTypeNode node) {
		node.getLocalScope().resetOffset();
	}
	
	@Override
	public void visitLeave(ParameterNode node) {
		IdentifierNode identifier = (IdentifierNode)node.child(1);
		Type type = node.getType();
		
		node.setType(type);
		
		identifier.setType(type);
		identifier.setConOrVar(LextantToken.fakeToken("parameter", Keyword.CONST));
		addBinding(identifier, type);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// function body
	@Override
	public void visitEnter(FunctionBodyNode node) {
		Scope scope = Scope.createFunctionScope();
		
		node.setScope(scope);
	}
	@Override
	public void visitLeave(FunctionBodyNode node) {
		leaveScope(node);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// return
	@Override
	public void visitLeave(ReturnStatementNode node) {
		if(node.nChildren()==1) {
			Type type = null;
			
			for(ParseNode pathNode: node.pathToRoot()) {
				if(pathNode instanceof LambdaNode) {
					type = ((LambdaType)(pathNode.getType())).getReturnType();
				}
			}
			
			assert node.child(0).getType().equivalent(type);
		}
		else {
			Type type = null;
			
			for(ParseNode pathNode: node.pathToRoot()) {
				if(pathNode instanceof LambdaNode) {
					type = ((LambdaType)(pathNode.getType())).getReturnType();
				}
			}
			
			assert type.equivalent(PrimitiveType.VOID);
		}
		 
	}
	
	///////////////////////////////////////////////////////////////////////////
	// call
	
	@Override
	public void visitLeave(CallStatementNode node) {
		assert node.child(0) instanceof FunctionInvocationNode; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	// function invocation part
	
	@Override
	public void visitLeave(FunctionInvocationNode node) {
		assert node.child(0).getType() instanceof LambdaType;
		List<Type> paraType = ((LambdaType)(node.child(0).getType())).getParaType();
		List<ParseNode> input = node.child(1).getChildren();
		assert paraType.size()==input.size();
		
		for(int i = 0; i < paraType.size(); i++) {
			if(!paraType.get(i).equivalent(input.get(i).getType())) {
				logError("function invocation: type doesnt match.");
				
				return;
			}
		}
		
		node.setType(((LambdaType)(node.child(0).getType())).getReturnType());
	}
	
	public void visitLeave(FoldExpressionNode node) {
		assert node.child(0).getType() instanceof ArrayType;
		assert node.child(1).getType() instanceof LambdaType;
		
		if(node.nChildren()==2) {
			assert ((LambdaType)node.child(1).getType()).getParaType().size()==2;
			assert ((LambdaType)node.child(1).getType()).getParaType().get(0).equivalent(((ArrayType)node.child(0).getType()).getSubType());
			assert ((LambdaType)node.child(1).getType()).getReturnType().equivalent(((ArrayType)node.child(0).getType()).getSubType());
			
			
			node.setType(((LambdaType)node.child(1).getType()).getReturnType());
		}
		else if(node.nChildren() == 3) {
			assert ((LambdaType)node.child(1).getType()).getParaType().size()==2;
			assert ((LambdaType)node.child(1).getType()).getParaType().get(1).equivalent(((ArrayType)node.child(0).getType()).getSubType());
			assert ((LambdaType)node.child(1).getType()).getParaType().get(0).equivalent(node.child(2).getType());
			assert ((LambdaType)node.child(1).getType()).getReturnType().equivalent(node.child(2).getType());
			
			node.setType(node.child(2).getType());
		}
		else {
			assert 2==1;
		}
	}
	
	public void visitLeave(ZipExpressionNode node) {
		assert node.child(2).getType() instanceof LambdaType;
		assert node.child(0).getType() instanceof ArrayType;
		assert node.child(1).getType() instanceof ArrayType;
		
		assert ((LambdaType)node.child(2).getType()).getParaType().size()==2;
		assert ((LambdaType)node.child(2).getType()).getParaType().get(0).equivalent(((ArrayType)node.child(0).getType()).getSubType());
		assert ((LambdaType)node.child(2).getType()).getParaType().get(1).equivalent(((ArrayType)node.child(1).getType()).getSubType());
		
		node.setType(new ArrayType(((LambdaType)node.child(2).getType()).getReturnType()));
	}
	///////////////////////////////////////////////////////////////////////////
	// expressions
	@Override
	public void visitLeave(BinaryOperatorNode node) {
		ParseNode left  = node.child(0);
		ParseNode right = node.child(1);
		List<Type> childTypes = Arrays.asList(left.getType(), right.getType());
		Lextant operator = operatorFor(node);
		
		if(operator == Punctuator.STRING_RANGE) {
			assert left.getType()==PrimitiveType.STRING;
			assert right.getType() == PrimitiveType.INTEGER;
			assert node.child(2).getType() == PrimitiveType.INTEGER;
			node.setType(PrimitiveType.STRING);
			return;
		}
		else if (operator == Keyword.MAP) {
			assert node.nChildren()==2;
			assert node.child(1).getType() instanceof LambdaType;
			assert ((LambdaType)node.child(1).getType()).getParaType().size()==1;
			
			Type type = ((ArrayType)node.child(0).getType()).getSubType();
			
			assert type.equivalent(((LambdaType)node.child(1).getType()).getParaType().get(0));
			
			node.setType(new ArrayType(((LambdaType)node.child(1).getType()).getReturnType()));
			
			return;
		}
		else if(operator == Keyword.REDUCE) {
			assert node.nChildren()==2;
			assert node.child(1).getType() instanceof LambdaType;
			assert ((LambdaType)node.child(1).getType()).getParaType().size()==1;
			assert ((LambdaType)node.child(1).getType()).getReturnType() == PrimitiveType.BOOLEAN;
			
			Type type = ((ArrayType)node.child(0).getType()).getSubType();
			
			assert type.equivalent(((LambdaType)node.child(1).getType()).getParaType().get(0));
			
			node.setType(node.child(0).getType());
			
			return;
		}
		
		FunctionSignatures signatures = FunctionSignatures.signaturesOf(operator);
		FunctionSignature signature = signatures.acceptingSignature(childTypes);
		
		
		if(signature.accepts(childTypes)) {
			node.setType(signature.resultType());
			node.setSignature(signature);
			return;
		}
		
		if(PromotionType.canPromote(node.child(0).getType())) {
			
			List<PrimitiveType> matchingType = new ArrayList<PrimitiveType>();
			int number = 1;
			PromotionType promotionType = PromotionType.getPromotionTypeObject((PrimitiveType)node.child(0).getType());
			
			while(promotionType.getSuccessor(number)!=null&&!(promotionType.getSuccessor(number).isEmpty())) {
				matchingType.clear();
				List<PrimitiveType> type = promotionType.getSuccessor(number);
				number++;
				
				for(PrimitiveType T:type) {
					signature = signatures.acceptingSignature(Arrays.asList(T, right.getType()));
					if(signature.accepts(Arrays.asList(T, right.getType()))) {
						matchingType.add(T);
					}
				}
				
				for(PrimitiveType T:matchingType) {
					if(PromotionType.isChildren(T, matchingType)) {
						signature = signatures.acceptingSignature(Arrays.asList(T, right.getType()));
						if(signature.accepts(Arrays.asList(T, right.getType()))) {
							node.setType(signature.resultType());
							node.setSignature(signature);
							Token token;
							if(T.equivalent(PrimitiveType.INTEGER))
								token = LextantToken.fakeToken("int", Keyword.INT);
							else if(T.equivalent(PrimitiveType.FLOAT))
								token = LextantToken.fakeToken("float", Keyword.FLOAT);
							else if(T.equivalent(PrimitiveType.RATIONAL))
								token = LextantToken.fakeToken("rational", Keyword.RAT);
							else
							{
								token = null;
								logError("type promotion");
							}
							node.replaceChild(node.child(0),BinaryOperatorNode.withChildren(LextantToken.fakeToken("type cast", Punctuator.OPEN_BRACKET), node.child(0), new TypeConstantNode(token)));
							node.child(0).setType(T);
							return;
						}
					}
				}
				
				if(matchingType.size()!=0) {
					logError("promotion fails.");
					node.setType(PrimitiveType.ERROR);
				}
			}
			
		}
		
		if(PromotionType.canPromote(node.child(1).getType()) && operator != Punctuator.OPEN_BRACKET) {
			List<PrimitiveType> matchingType = new ArrayList<PrimitiveType>();
			int number = 1;
			PromotionType promotionType = PromotionType.getPromotionTypeObject((PrimitiveType)node.child(1).getType());
			
			while(promotionType.getSuccessor(number)!=null&&!(promotionType.getSuccessor(number).isEmpty())) {
				matchingType.clear();
				List<PrimitiveType> type = promotionType.getSuccessor(number);
				number++;
				
				for(PrimitiveType T:type) {
					signature = signatures.acceptingSignature(Arrays.asList(left.getType(),T));
					if(signature.accepts(Arrays.asList(left.getType(),T))) {
						matchingType.add(T);
					}
				}
				
				for(PrimitiveType T:matchingType) {
					if(PromotionType.isChildren(T, matchingType)) {
						signature = signatures.acceptingSignature(Arrays.asList(left.getType(), T));
						if(signature.accepts(Arrays.asList(left.getType(), T))) {
							node.setType(signature.resultType());
							node.setSignature(signature);
							Token token;
							if(T.equivalent(PrimitiveType.INTEGER))
								token = LextantToken.fakeToken("int", Keyword.INT);
							else if(T.equivalent(PrimitiveType.FLOAT))
								token = LextantToken.fakeToken("float", Keyword.FLOAT);
							else if(T.equivalent(PrimitiveType.RATIONAL))
								token = LextantToken.fakeToken("rational", Keyword.RAT);
							else
							{
								token = null;
								logError("type promotion");
							}
							node.replaceChild(node.child(1),BinaryOperatorNode.withChildren(LextantToken.fakeToken("type cast", Punctuator.OPEN_BRACKET), node.child(1), new TypeConstantNode(token)));
							node.child(1).setType(T);
							return;
						}
					}
				}
				if(matchingType.size()!=0) {
					logError("promotion fails.");
					node.setType(PrimitiveType.ERROR);
				}
			}
			
			
		}
		
		if(PromotionType.canPromote(node.child(0).getType())&&PromotionType.canPromote(node.child(1).getType())) {
			List<PrimitiveType> typeA = PromotionType.getAllChildren((PrimitiveType)node.child(0).getType());
			List<PrimitiveType> typeB = PromotionType.getAllChildren((PrimitiveType)node.child(1).getType());
			List<PrimitiveType> matchingTypeA = new ArrayList<PrimitiveType>();
			List<PrimitiveType> matchingTypeB = new ArrayList<PrimitiveType>();
			
			for(PrimitiveType A:typeA) {
				for(PrimitiveType B:typeB) {
					signature = signatures.acceptingSignature(Arrays.asList(A, B));
					if(signature.accepts(Arrays.asList(A,B))) {
						matchingTypeA.add(A);
						matchingTypeB.add(B);
					}
				}
			}
			
			for(PrimitiveType A:matchingTypeA) {
				if(PromotionType.isChildren(A, matchingTypeA)) {
					for(PrimitiveType B:matchingTypeB) {
						signature = signatures.acceptingSignature(Arrays.asList(A, B));
						if(signature.accepts(Arrays.asList(A,B))) {
							node.setType(signature.resultType());
							node.setSignature(signature);
							
							int index = 0;
							for(PrimitiveType T:Arrays.asList(A,B)) {
								Token token;
								if(T.equivalent(PrimitiveType.INTEGER))
									token = LextantToken.fakeToken("int", Keyword.INT);
								else if(T.equivalent(PrimitiveType.FLOAT))
									token = LextantToken.fakeToken("float", Keyword.FLOAT);
								else if(T.equivalent(PrimitiveType.RATIONAL))
									token = LextantToken.fakeToken("rational", Keyword.RAT);
								else
								{
									token = null;
									logError("type promotion");
								}
								node.replaceChild(node.child(index),BinaryOperatorNode.withChildren(LextantToken.fakeToken("type cast", Punctuator.OPEN_BRACKET), node.child(index), new TypeConstantNode(token)));
								node.child(index).setType(T);
								index++;
							}
							return;
						}
					}
				}
			}
			
			if(matchingTypeA.size()!=0) {
				logError("promotion fails.");
				node.setType(PrimitiveType.ERROR);
			}
		}
		
		logError("no matching type for operator at "+ node.getToken().getLocation());
		node.setType(PrimitiveType.ERROR);
		
	}
	private Lextant operatorFor(BinaryOperatorNode node) {
		LextantToken token = (LextantToken) node.getToken();
		return token.getLextant();
	}
	
	///////////////////////////////////////////////////////////////////////////
	// expression list
	@Override
	public void visitLeave(ExpressionListNode node) {
		if(node.nChildren()==0) {
			node.setType(PrimitiveType.NO_TYPE);
		}
		else {
		node.setType(new ArrayType(node.child(0).getType()));
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////
	// unary operator expressions
	@Override
	public void visitLeave(CloneExpressionNode node) {
		Type type = node.child(0).getType();
		
		if(!(type instanceof ArrayType)) {
			logError("clone target is not array type at "+node.child(0).getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
		else {
			node.setType(type);
		}
	}
	
	@Override
	public void visitLeave(ReverseExpressionNode node) {
		assert node.child(0).getType() == PrimitiveType.STRING || node.child(0).getType() instanceof ArrayType;
		
		node.setType(node.child(0).getType());
	}
	
	@Override
	public void visitLeave(NotExpressionNode node) {
		Type type = node.child(0).getType();
		
		if(!(type.equivalent(PrimitiveType.BOOLEAN))) {
			logError("not expression target is not bool type at "+node.child(0).getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
		else{
			node.setType(PrimitiveType.BOOLEAN);
		}
	}
	
	@Override
	public void visitLeave(LengthExpressionNode node) {
		Type type = node.child(0).getType();
		
		if(!(type instanceof ArrayType)&&!(type.equivalent(PrimitiveType.STRING))) {
			logError("length expression target is not array type at "+node.child(0).getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
		else {
			node.setType(PrimitiveType.INTEGER);
		}
	}
	///////////////////////////////////////////////////////////////////////////
	// release statements
	@Override
	public void visitLeave(ReleaseStatementNode node) {
		Type type = node.child(0).getType();
		
		if(!(type instanceof ArrayType)) {
			logError("release target is not array type at "+node.child(0).getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
	}
	///////////////////////////////////////////////////////////////////////////
	// control flow statements
	@Override
	public void visitLeave(IfStatementNode node) {
		ParseNode condition = node.child(0);
		
		if(!condition.getType().equals(PrimitiveType.BOOLEAN)) {
			logError("If statement needs a boolean type condition at Location:"+node.getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
	}
	
	@Override
	public void visitEnter(WhileStatementNode node) {
		this.whileLoopNun++;
	}
	
	@Override
	public void visitLeave(WhileStatementNode node) {
		this.whileLoopNun--;
		
		ParseNode condition = node.child(0);
		
		if(!condition.getType().equals(PrimitiveType.BOOLEAN)) {
			logError("While statement needs a boolean type expression as condition at Location:"+node.getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
	}
	@Override
	public void visitEnter(ForIndexStatementNode node) {
		enterSubscope(node);
		this.whileLoopNun++;
		
		IdentifierNode identifier = (IdentifierNode) node.child(0);
		
		identifier.setType(PrimitiveType.INTEGER);
		identifier.setConOrVar(LextantToken.fakeToken("const", Keyword.CONST));
		identifier.setStatic(false);
		
		SemanticAnalysisVisitor.addBinding(identifier, PrimitiveType.INTEGER);
	}
	@Override
	public void visitLeave(ForIndexStatementNode node) {
		assert node.child(1).getType() instanceof ArrayType || node.child(1).getType()==PrimitiveType.STRING;
		this.whileLoopNun--;
		
		leaveScope(node); 
	}
	
	@Override
	public void visitEnter(ForElemStatementNode node) {
		enterSubscope(node);
		this.whileLoopNun++;
	}
	@Override
	public void visitLeave(ForElemStatementNode node) {
		assert node.child(0).getType() instanceof ArrayType || node.child(0).getType()==PrimitiveType.STRING;
		this.whileLoopNun--;
		
		leaveScope(node); 
	}
	
	public void visit(BreakStatementNode node) {
		if(this.whileLoopNun==0) {
			logError("break statement is not in a valid while loop at Location:"+node.getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
	}
	
	public void visit(ContinueStatementNode node) {
		if(this.whileLoopNun==0) {
			logError("continue statement is not in a valid while loop at Location:"+node.getToken().getLocation());
			node.setType(PrimitiveType.ERROR);
		}
	}
	///////////////////////////////////////////////////////////////////////////
	// array type
	/*@Override
	public void visitLeave(ArrayTypeConstantNode node) {
		Type type = node.child(0).getType();
		
		node.setType(new ArrayType(type));
	}*/
	
	///////////////////////////////////////////////////////////////////////////
	// simple leaf nodes
	@Override
	public void visit(BooleanConstantNode node) {
		node.setType(PrimitiveType.BOOLEAN);
	}
	@Override
	public void visit(ErrorNode node) {
		node.setType(PrimitiveType.ERROR);
	}
	@Override
	public void visit(IntegerConstantNode node) {
		node.setType(PrimitiveType.INTEGER);
	}
	@Override
	public void visit(FloatConstantNode node) {
		node.setType(PrimitiveType.FLOAT);
	}
	@Override
	public void visit(StringConstantNode node) {
		node.setType(PrimitiveType.STRING);
		
		int size = node.getValue().length()+1;
		node.setLocation(this.stringBlockSize, size);
		this.stringBlockSize = this.stringBlockSize + size;
	}
	@Override
	public void visit(CharConstantNode node) {
		node.setType(PrimitiveType.CHAR);
	}
	
	/*@Override
	public void visit(TypeConstantNode node) {
		if(node.getToken().isLextant(Keyword.INT)) {
			node.setType(PrimitiveType.INTEGER);
		}
		else if(node.getToken().isLextant(Keyword.FLOAT)) {
			node.setType(PrimitiveType.FLOAT);
		}
		else if(node.getToken().isLextant(Keyword.BOOL)) {
			node.setType(PrimitiveType.BOOLEAN);
		}
		else if(node.getToken().isLextant(Keyword.CHAR)) {
			node.setType(PrimitiveType.CHAR);
		}
		else if(node.getToken().isLextant(Keyword.STRING)) {
			node.setType(PrimitiveType.STRING);
		}
		else if(node.getToken().isLextant(Keyword.RAT)) {
			node.setType(PrimitiveType.RATIONAL);
		}
	}*/
	@Override
	public void visit(NewlineNode node) {
	}
	@Override
	public void visit(SpaceNode node) {
	}
	@Override
	public void visit(TabNode node) {
	}
	///////////////////////////////////////////////////////////////////////////
	// IdentifierNodes, with helper methods
	@Override
	public void visit(IdentifierNode node) {
		if(!isBeingDeclared(node) && !(node.getParent() instanceof ParameterNode)) {	
			if(node.getParent() instanceof ForElemStatementNode && node == node.getParent().child(1)) {
				if(node.getParent().child(0).getType() == PrimitiveType.STRING) {
					node.setType(PrimitiveType.CHAR);
					node.setConOrVar(LextantToken.fakeToken("const", Keyword.CONST));
					node.setStatic(false);
					
					SemanticAnalysisVisitor.addBinding(node, PrimitiveType.CHAR);
				}
				else {
					node.setType(((ArrayType)node.getParent().child(0).getType()).getSubType());
					node.setConOrVar(LextantToken.fakeToken("const", Keyword.CONST));
					node.setStatic(false);
					
					SemanticAnalysisVisitor.addBinding(node, ((ArrayType)node.getParent().child(0).getType()).getSubType());
				}
			}
			else {
			Binding binding = node.findVariableBinding();
			
			node.setType(binding.getType());
			node.setConOrVar(binding.getConOrVar());
			node.setBinding(binding);
			}
		}
		// else parent DeclarationNode does the processing.
	}
	
	@Override
	public void visitLeave(GlobalDeclarationNode node) {
		IdentifierNode identifier = (IdentifierNode) node.child(0);
		ParseNode initializer = node.child(1);
		
		Type declarationType = initializer.getType();
		if(declarationType == PrimitiveType.NO_TYPE) {
			SemanticAnalysisVisitor.logError("Variable cannot be declared with an empty expression list.");
			node.setType(PrimitiveType.ERROR);
			return;
		}
		node.setType(declarationType);
		
		identifier.setType(declarationType);
		identifier.setConOrVar(node.lextantToken());
		identifier.setStatic(true);
		
		SemanticAnalysisVisitor.addBinding(identifier, declarationType);
	}
	
	private boolean isBeingDeclared(IdentifierNode node) {
		ParseNode parent = node.getParent();
		return (parent instanceof DeclarationNode || parent instanceof FunctionDeclarationNode || parent instanceof GlobalDeclarationNode) && (node == parent.child(0));
	}
	static public void addBinding(IdentifierNode identifierNode, Type type) {
		Scope scope = identifierNode.getLocalScope();
		Binding binding = scope.createBinding(identifierNode, type);
		identifierNode.setBinding(binding);
		
	}
	
	///////////////////////////////////////////////////////////////////////////
	// error logging/printing

	private void typeCheckError(ParseNode node, List<Type> operandTypes) {
		Token token = node.getToken();
		
		logError("operator " + token.getLexeme() + " not defined for types " 
				 + operandTypes  + " at " + token.getLocation());	
	}
	static public void logError(String message) {
		PikaLogger log = PikaLogger.getLogger("compiler.semanticAnalyzer");
		log.severe(message);
	}
}