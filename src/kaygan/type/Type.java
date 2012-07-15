package kaygan.type;

import java.util.Map;

import kaygan.ast.ASTNode;

public class Type extends ASTNode
{
	//-- A few default static values
	public static final Type TYPE = new Type("Type");
	
	public static final Type ANY = new Type("Any");
	
	public static final Type ERROR = new Type("Error");
	
	
	private static int ID_POOL = 0;
	
	private final int id;
	
	private final String name;
	
	public Type()
	{
		this("?");
	}
	
	public Type(String name)
	{
		this.id = ID_POOL++;
		this.name = name;
	}
	
//	@Override
//	public Type substitute(Type from, Type to)
//	{
//		if( this.equals(from) )
//		{
//			return to;
//		}
//		return this;
//	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public boolean equals(Object o )
	{
		if( o instanceof Type )
		{
			return ((Type)o).id == this.id;
		}
		return false;
	}

	public boolean accept(Type type)
	{
		// TODO generated types always accept?
		return true;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	
	//public abstract boolean accept(Type type);
//	{
//		// TODO - Any type accepts all types
//		//      - NamedType only accepts types named the same
//		//      - StructuralType accepts types with the same structure, etc
//		
//		// TODO work DependentType in here somewhere (inference)
//		
//		return false;
//	}
	
	public Type substitute(Map<Type, Type> substitutions)
	{
		Type newType = substitutions.get(this);
		if( newType != null )
		{
			return newType.substitute(substitutions);
		}
		return this;
	}
//	{
//		
//	}

	@Override
	public int getOffset()
	{
		return 0;
	}

	@Override
	public int getLength()
	{
		return 0;
	}

	@Override
	public ASTNode findNode(int offset)
	{
		return null;
	}
	
}
