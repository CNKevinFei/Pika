package parseTree;

import parseTree.nodeTypes.*;

// Visitor pattern with pre- and post-order visits
public interface ParseNodeVisitor {
	
	// non-leaf nodes: visitEnter and visitLeave
	void visitEnter(BinaryOperatorNode node);
	void visitLeave(BinaryOperatorNode node);
	
	void visitEnter(MainBlockNode node);
	void visitLeave(MainBlockNode node);

	void visitEnter(DeclarationNode node);
	void visitLeave(DeclarationNode node);

	void visitEnter(AssignmentStatementNode node);
	void visitLeave(AssignmentStatementNode node);
	
	void visitEnter(ParseNode node);
	void visitLeave(ParseNode node);
	
	void visitEnter(PrintStatementNode node);
	void visitLeave(PrintStatementNode node);
	
	void visitEnter(ProgramNode node);
	void visitLeave(ProgramNode node);
	
	void visitEnter(ArrayIndexVariableNode node);
	void visitLeave(ArrayIndexVariableNode node);
	
	void visitEnter(CloneExpressionNode node);
	void visitLeave(CloneExpressionNode node);
	
	void visitEnter(ExpressionListNode node);
	void visitLeave(ExpressionListNode node);
	
	void visitEnter(IfStatementNode node);
	void visitLeave(IfStatementNode node);
	
	void visitEnter(LengthExpressionNode node);
	void visitLeave(LengthExpressionNode node);
	
	void visitEnter(NotExpressionNode node);
	void visitLeave(NotExpressionNode node);
	
	void visitEnter(ReleaseStatementNode node);
	void visitLeave(ReleaseStatementNode node);
	
	void visitEnter(WhileStatementNode node);
	void visitLeave(WhileStatementNode node);
	
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
	void visit(ArrayTypeConstantNode node);

	
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
		public void visitEnter(ArrayIndexVariableNode node) {
			defaultVisitEnter(node);
		}
		public void visitLeave(ArrayIndexVariableNode node) {
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
		public void visit(ArrayTypeConstantNode node) {
			defaultVisitForLeaf(node);
		}
	}
}
