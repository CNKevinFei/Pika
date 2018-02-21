package semanticAnalyzer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import semanticAnalyzer.types.*;

public class PromotionType {
	private static Map<PrimitiveType, PromotionType> map = new HashMap<PrimitiveType, PromotionType>();
	private PrimitiveType type;
	private List<PrimitiveType> promotionTypes = new ArrayList<PrimitiveType>();
	private List<PrimitiveType> allChildren = new ArrayList<PrimitiveType>();
	
	
	public PromotionType(PrimitiveType type, PrimitiveType ...primitiveType) {
		this.type = type;
		for(PrimitiveType T:primitiveType) {
			promotionTypes.add(T);
			allChildren.add(T);
			allChildren.addAll(map.get(T).allChildren);
		}
		
		map.put(this.type, this);
	}
	
	public static boolean canPromote(Type testType) {
		if(testType instanceof ArrayType) {
			return  false;
		}
		PromotionType promotionType = getPromotionTypeObject((PrimitiveType)testType);
		
		if(promotionType==null)
			return false;
		
		return promotionType.promotionTypes.size()!=0;
	}
	
	public static PromotionType getPromotionTypeObject(PrimitiveType key) {
		return map.get(key);
	}
	
	public static List<PrimitiveType> getAllChildren(PrimitiveType type){
		return map.get(type).allChildren;
	}
	
	public static boolean hasChildren(PrimitiveType type, List<PrimitiveType> testChild) {
		List<PrimitiveType> children = getPromotionTypeObject(type).allChildren;
		
		for(PrimitiveType T:testChild) {
			
			if(!children.contains(T)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isChildren(PrimitiveType type, List<PrimitiveType> testChild) {
		List<PrimitiveType> children = getPromotionTypeObject(type).allChildren;
		
		for(PrimitiveType T:testChild) {
			if(!(T.equivalent(type))&&!(children.contains(T))) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canPromote(Type ...primitiveTypes) {
		for(Type T:primitiveTypes) {
			if(!this.allChildren.contains(T))
				return false;
		}
		
		return true;
	}
	public List<PrimitiveType> getSuccessor(int number){
		List<PrimitiveType> predecessor = new ArrayList<PrimitiveType>();
		List<PrimitiveType> successor = new ArrayList<PrimitiveType>();
		PromotionType value;
		
		predecessor.add(this.getType());
		
		while(!(predecessor.isEmpty())&&number!=0) {
			successor.clear();
			
			for(PrimitiveType T:predecessor) {
				if((value=map.get(T))!=null&&value.getDirectSuccessor()!=null) {
					successor.addAll(value.getDirectSuccessor());
				}
			}
			
			predecessor.clear();
			predecessor.addAll(successor);
			number--;
		}
		
		if(number!=0)
			return null;
		else
			return predecessor;
	}
	
	public List<PrimitiveType> getDirectSuccessor(){
		return this.promotionTypes;
	}
	public PrimitiveType getType() {
		return type;
	}
	
	static {
		new PromotionType(PrimitiveType.RATIONAL);
		new PromotionType(PrimitiveType.FLOAT);
		new PromotionType(PrimitiveType.INTEGER, PrimitiveType.RATIONAL, PrimitiveType.FLOAT);
		new PromotionType(PrimitiveType.CHAR, PrimitiveType.INTEGER);
	}

}

