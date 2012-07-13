package kaygan.type;

public class NamedType extends Type
{
	private final String name;
	
	public NamedType(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
