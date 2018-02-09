package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;


public class ArrayTypeConstantNode extends ParseNode{
	private int layerNum;
	
	public ArrayTypeConstantNode(int layer, ParseNode type) {
		super(type);
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
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
