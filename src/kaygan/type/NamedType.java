package kaygan.type;

public class NamedType extends Type
{
	private final String name;
	
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
	public boolean accept(Type type)
	{
		if( type instanceof NamedType )
		{
			return ((NamedType)type).name.equals(this.name);
		}
		return false;
	}
	
	@Override
	public Type substitute(Type from, Type to)
	{
		if( from instanceof NamedType && to instanceof NamedType )
		{
			NamedType fromType = (NamedType)from;
			
			if( fromType.name.equals(this.name) )
			{
				// we are being substituted
				return to;
			}
		}
		return this;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
