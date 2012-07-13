package kaygan.ast;

import kaygan.Scope;
import kaygan.type.Type;

public abstract class ASTNode
{
	public abstract int getOffset();
	
	public abstract int getLength();
	
	public boolean overlaps(int offset)
	{
		int nodeOffset = getOffset();
		return nodeOffset <= offset && (nodeOffset+getLength()) > offset;
	}
	
	public abstract ASTNode findNode(int offset);
	
	
	//public abstract Type getType();
	
	public Type inferType(Scope scope)
	{
		return Type.ANY;
	}
	
	public Type getType()
	{
		return Type.ANY;
	}
	
}
