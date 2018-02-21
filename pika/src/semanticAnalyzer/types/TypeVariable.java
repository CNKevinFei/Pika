package semanticAnalyzer.types;

public class TypeVariable implements Type{
	private String name;
	private Type typeConstraint;
	
	public TypeVariable(String name) {
		this.name = name;
		this.typeConstraint = PrimitiveType.NO_TYPE;
	}
	
	public void reset() {
		this.typeConstraint = PrimitiveType.NO_TYPE;
	}
	
	public int getSize() {
		return 0;
	}
	
	public String infoString() {
		return toString();
	}
	
	public String toString() {
		return "<" + getName() + ">";
	}
	
	public String getName() {
		return name;
	}
	
	public boolean equivalent(Type type) {
		if(type instanceof TypeVariable) {
			throw new RuntimeException("equals attempted on two types containing type variables.");
		}
		
		if(this.getType()==PrimitiveType.NO_TYPE) {
			this.setType(type);
			return true;
		}
		
		return this.getType().equivalent(type);
		
	}
	
	public Type getType() {
		return typeConstraint;
	}
	
	public void setType(Type type) {
		typeConstraint = type;
	}
	
	public Type getConcreteType() {
		return getType().getConcreteType();
	}
}
