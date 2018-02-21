package semanticAnalyzer.types;

public class ArrayType implements Type{
	private Type subType;
	private int sizeInByte;
	
	public ArrayType(Type type) {
		this.subType = type;
		sizeInByte = 16;
	}
	
	public void setSize(int length) {
		if(subType instanceof ArrayType) {
			sizeInByte = length*4;
		}
		else {
			sizeInByte = length*subType.getSize();
		}
	}
	
	public int getSize() {
		return sizeInByte;
	}
	
	public String infoString() {
		return toString();
	}
	
	public String toString() {
		return "["+subType.infoString()+"]";
	}
	public boolean equivalent(Type type) {
		if(type instanceof ArrayType) {
			return subType.equivalent(((ArrayType)type).getSubType());
		}
		
		return false;
	}
	
	public Type getSubType() {
		return subType;
	}
	
	public Type getConcreteType() {
		return new ArrayType(this.getSubType().getConcreteType());
	}

}
