package kaygan.type;

public class GeneratedType extends Type
{
	private static int ID_POOL = 0;
	
	private final int id;
	
	private final String name;
	
	public GeneratedType()
	{
		this("?");
	}
	
	public GeneratedType(String name)
	{
		this.id = ID_POOL++;
		this.name = name;
	}
	
	@Override
	public Type substitute(Type from, Type to)
	{
		if( this.equals(from) )
		{
			return to;
		}
		return this;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public boolean equals(Object o )
	{
		if( o instanceof GeneratedType )
		{
			return ((GeneratedType)o).id == this.id;
		}
		return false;
	}

	@Override
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
}
