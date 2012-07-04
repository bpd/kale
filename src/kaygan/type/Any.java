package kaygan.type;

import kaygan.Type;

public class Any extends Type
{
	public Any()
	{
		super("<Any>");
	}

	@Override
	public Type getType()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		return "Any";
	}
	
}
