package kaygan.atom;

import kaygan.Function;
import kaygan.Type;

public class Symbol extends Function
{
	private final String value;
	
	public Symbol(String value)
	{
		this.value = value;
	}

	@Override
	public Function bind(Function f)
	{
		return this;
	}
	
	@Override
	public Function eval()
	{
		return this;
	}
	
	private static final Type TYPE = new Type("<Symbol>");
	
	@Override
	public Type getType()
	{
		return TYPE;
	}
	
	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return value.equals(obj);
	}

	@Override
	public String toString()
	{
		return value;
	}
}
