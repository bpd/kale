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
	public String toString()
	{
		return name;
	}
}
