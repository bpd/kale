package kaygan.atom;

import kaygan.Function;

public class Symbol implements Function
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
		return "<Symbol '"+value.toString()+"'>";
	}
}
