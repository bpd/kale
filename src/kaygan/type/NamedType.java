package kaygan.type;

import kaygan.Scope;

public class NamedType extends Type
{
	private final String name;
	
	private final Type type;
	
	public NamedType(String name)
	{
		this( name, new NamedType("Type<"+name+">", Type.TYPE) );
	}
	
	public NamedType(String name, Type type)
	{
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Type inferType(Scope scope)
	{
		return type;
	}

	@Override
	public Type getType()
	{
		return type;
	}



	@Override
	public String toString()
	{
		return name;
	}
}
