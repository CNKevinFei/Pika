package parseTree;

import parseTree.nodeTypes.BinaryOperatorNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.MainBlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.AssignmentStatementNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.FloatConstantNode;
import parseTree.nodeTypes.CharConstantNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.TypeConstantNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.TabNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.SpaceNode;

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
	}
}
