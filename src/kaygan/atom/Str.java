package kaygan.atom;

import kaygan.Function;
import kaygan.Type;

public class Str extends Function
{
	private final String value;
	
	public Str(String value)
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
	
	private static final Type TYPE = new Type("<Str>");
	
	@Override
	public Type getType()
	{
		return TYPE;
	}
	
	@Override
	public String toString()
	{
		return value.toString();
	}
	
}
