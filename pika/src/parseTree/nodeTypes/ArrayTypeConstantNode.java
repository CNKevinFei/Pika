package parseTree.nodeTypes;

import parseTree.ParseNode;
import tokens.Token;
import parseTree.ParseNodeVisitor;


public class ArrayTypeConstantNode extends ParseNode{
	private int layerNum;
	
	public ArrayTypeConstantNode(Token token, int layer, ParseNode type) {
		super(token);
		layerNum = layer;
		this.appendChild(type);
	}
	
	////////////////////////////////////////////////////////////
	// return layer number
	public int getLayerNum() {
		return layerNum;
	}
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors

	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		//System.out.println(layerNum);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
