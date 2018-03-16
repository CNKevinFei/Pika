package semanticAnalyzer.types;

import java.util.ArrayList;
import java.util.List;

public class LambdaType implements Type{
	private Type returnType;
	private List<Type> paraType;
	
	public LambdaType() {
		this.paraType = new ArrayList<Type>();
	}
	
	// attribute operation
	public void setReturnType(Type type) {
		this.returnType = type;
	}
	public Type getReturnType() {
		return this.returnType;
	}
	
	public void addParaType(Type type) {
		this.paraType.add(type);
	}
	public List<Type> getParaType() {
		return this.paraType;
	}
	
	public int getSize() {
		return 4;
	}
	
	// information print
	public String infoString() {
		return toString();
	}
	
	public String toString() {
		String res = "< ";
		
		for(int i = 0; i < paraType.size(); i++) {
			res += paraType.get(i).toString();
			if(i != paraType.size()-1) {
				res += ", ";
			}
			
		}
		
		res += " > -> ";
		
		res += returnType.toString();
		
		return res;
	}
	
	// type equality
	
	public boolean equivalent(Type type) {
		if(type instanceof LambdaType) {
			if(!this.getReturnType().equivalent(((LambdaType) type).getReturnType())) {
				return false;
			}
			
			if(this.getParaType().size() != ((LambdaType) type).getParaType().size()) {
				return false;
			}
			
			for(int i = 0; i < this.getParaType().size(); i++) {
				if(!this.getParaType().get(i).equivalent(((LambdaType) type).getParaType().get(i))) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	public Type getConcreteType() {
		return this;
	}
}
