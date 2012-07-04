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
	public Function bindTo(Function f)
	{
		return f.bind(this);
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
		if( obj instanceof Symbol )
		{
			return value.equals(((Symbol)obj).value);
		}
		else if( obj instanceof String )
		{
			return value.equals((String)obj);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return value;
	}
}
