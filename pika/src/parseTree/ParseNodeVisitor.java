package parseTree;

import parseTree.nodeTypes.*;

// Visitor pattern with pre- and post-order visits
public interface ParseNodeVisitor {
	
	// non-leaf nodes: visitEnter and visitLeave
	void visitEnter(BinaryOperatorNode node);
	void visitLeave(BinaryOperatorNode node);
	
	void visitEnter(FunctionDeclarationNode node);
	void visitLeave(FunctionDeclarationNode node);
	
	void visitEnter(MainBlockNode node);
	void visitLeave(MainBlockNode node);

	void visitEnter(DeclarationNode node);
	void visitLeave(DeclarationNode node);

	void visitEnter(AssignmentStatementNode node);
	void visitLeave(AssignmentStatementNode node);
	
	void visitEnter(LambdaNode node);
	void visitLeave(LambdaNode node);
	
	void visitEnter(LambdaParameterTypeNode node);
	void visitLeave(LambdaParameterTypeNode node);
	
	void visitEnter(TypeListNode node);
	void visitLeave(TypeListNode node);
	
	void visitEnter(LambdaTypeConstantNode node);
	void visitLeave(LambdaTypeConstantNode node);
	
	void visitEnter(FunctionBodyNode node);
	void visitLeave(FunctionBodyNode node);
	
	void visitEnter(ParameterNode node);
	void visitLeave(ParameterNode node);
	
	void visitEnter(ParseNode node);
	void visitLeave(ParseNode node);
	
	void visitEnter(PrintStatementNode node);
	void visitLeave(PrintStatementNode node);
	
	void visitEnter(ProgramNode node);
	void visitLeave(ProgramNode node);
	
	
	void visitEnter(CloneExpressionNode node);
	void visitLeave(CloneExpressionNode node);
	
	void visitEnter(ExpressionListNode node);
	void visitLeave(ExpressionListNode node);
	
	void visitEnter(ParaExpressionListNode node);
	void visitLeave(ParaExpressionListNode node);
	
	
	void visitEnter(LengthExpressionNode node);
	void visitLeave(LengthExpressionNode node);
	
	void visitEnter(NotExpressionNode node);
	void visitLeave(NotExpressionNode node);
	
	void visitEnter(ReleaseStatementNode node);
	void visitLeave(ReleaseStatementNode node);
	
	void visitEnter(IfStatementNode node);
	void visitLeave(IfStatementNode node);
	
	void visitEnter(WhileStatementNode node);
	void visitLeave(WhileStatementNode node);
	
	void visitEnter(ArrayTypeConstantNode node);
	void visitLeave(ArrayTypeConstantNode node);
	
	void visitEnter(FunctionInvocationNode node);
	void visitLeave(FunctionInvocationNode node);
	
	// leaf nodes: visitLeaf only
	void visit(BooleanConstantNode node);
	void visit(ErrorNode node);
	void visit(IdentifierNode node);
	void visit(IntegerConstantNode node);
	void visit(FloatConstantNode node);
	void visit(CharConstantNode node);
	void visit(TypeConstantNode node);
	void visit(StringConstantNode node);
	void visit(NewlineNode node);
	void visit(TabNode node);
	void visit(SpaceNode node);
	void visit(BreakStatementNode node);
	void visit(ContinueStatementNode node);

	
	public static class Default implements ParseNodeVisitor
	{
		public void defaultVisit(ParseNode node) {	}
		public void defaultVisitEnter(ParseNode node) {
			defaultVisit(node);
		}
		public void defaultVisitLeave(ParseNode node) {
			defaultVisit(node);
		}		
		public void defaultVisitForLeaf(ParseNode node) {
			defaultVisit(node);
		}
		
		public void visitEnter(BinaryOperatorNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(BinaryOperatorNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(FunctionDeclarationNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(FunctionDeclarationNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(DeclarationNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(DeclarationNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(AssignmentStatementNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(AssignmentStatementNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(LambdaNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(LambdaNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(LambdaParameterTypeNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(LambdaParameterTypeNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(TypeListNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(TypeListNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(LambdaTypeConstantNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(LambdaTypeConstantNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(FunctionBodyNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(FunctionBodyNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ParameterNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ParameterNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(MainBlockNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(MainBlockNode node) {
			defaultVisitLeave(node);
		}				
		public void visitEnter(ParseNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ParseNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(PrintStatementNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(PrintStatementNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ProgramNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ProgramNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(CloneExpressionNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(CloneExpressionNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ExpressionListNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ExpressionListNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ParaExpressionListNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ParaExpressionListNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(IfStatementNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(IfStatementNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(LengthExpressionNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(LengthExpressionNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(NotExpressionNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(NotExpressionNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ReleaseStatementNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ReleaseStatementNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(WhileStatementNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(WhileStatementNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(ArrayTypeConstantNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ArrayTypeConstantNode node) {
			defaultVisitLeave(node);
		}
		public void visitEnter(FunctionInvocationNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(FunctionInvocationNode node) {
			defaultVisitLeave(node);
		}
		

		public void visit(BooleanConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(ErrorNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(IdentifierNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(IntegerConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(FloatConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(CharConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(StringConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(TypeConstantNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(NewlineNode node) {
			defaultVisitForLeaf(node);
		}	
		public void visit(TabNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(SpaceNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(BreakStatementNode node) {
			defaultVisitForLeaf(node);
		}
		public void visit(ContinueStatementNode node) {
			defaultVisitForLeaf(node);
		}
		
	}
}
