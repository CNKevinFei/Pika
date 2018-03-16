package semanticAnalyzer;

import java.util.Arrays;
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

public class PreSemanticAnalysisVistor extends ParseNodeVisitor.Default{

	@Override
	public void visitLeave(ParseNode node) {
		return;
	}
	
	@Override
	public void visitEnter(ProgramNode node) {
		Scope scope = Scope.createProgramScope();
		
		node.setScope(scope);
	}
	
	@Override
	public void visitLeave(ProgramNode node) {
		
	}
	
	@Override
	public void visitLeave(FunctionDeclarationNode node) {
		IdentifierNode identifier = (IdentifierNode) node.child(0);
		ParseNode lambda = node.child(1);
		Type type = lambda.getType();
		
		node.setType(type);
		
		identifier.setType(type);
		identifier.setConOrVar(LextantToken.fakeToken("const function identifier", Keyword.CONST));
		
		addFunctionBinding(identifier, type);
	}
	
	public void addFunctionBinding(IdentifierNode identifier, Type type) {
		Scope scope = identifier.getLocalScope();
		Binding binding = scope.createFunctionBinding(identifier, type);
		identifier.setBinding(binding);
	}
	
	@Override
	public void visitLeave(LambdaNode node) {
		node.setType(node.child(0).getType());
	}
	
	@Override
	public void visitLeave(LambdaParameterTypeNode node) {
		LambdaType type = new LambdaType();
		
		assert node.nChildren()>0;
		
		type.setReturnType(node.child(0).getType());
		
		for(int i = 1; i < node.nChildren(); i++) {
			assert !node.child(i).getType().equivalent(PrimitiveType.VOID);
			type.addParaType(node.child(i).getType());
		}
		
		node.setType(type);
	}
	
	@Override
	public void visitLeave(ParameterNode node) {
		node.setType(node.child(0).getType());
	}
	
	@Override
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
		else if(node.getToken().isLextant(Keyword.VOID)) {
			node.setType(PrimitiveType.VOID);
		}
		else if(node.getToken().isLextant(Keyword.RAT)) {
			node.setType(PrimitiveType.RATIONAL);
		}
	}
	
	@Override
	public void visitLeave(ArrayTypeConstantNode node) {
		Type type = node.child(0).getType();
		assert !type.equivalent(PrimitiveType.VOID);
		node.setType(new ArrayType(type));
	}
	
	@Override
	public void visitLeave(LambdaTypeConstantNode node) {
		LambdaType type = new LambdaType();
		
		assert node.nChildren()>0;
		type.setReturnType(node.child(0).getType());
		
		for(int i = 1; i < node.nChildren(); i++) {
			assert !node.child(i).getType().equivalent(PrimitiveType.VOID);
			type.addParaType(node.child(i).getType());
		}
		
		node.setType(type);
	}
}
