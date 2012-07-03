package kaygan.atom;

import kaygan.Function;

public class Str implements Function
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
	
	@Override
	public String toString()
	{
		return value.toString();
	}
	
}
