package kaygan.atom;

import kaygan.Function;

public class Pair implements Function
{
	public final String symbol;
	
	public final Function value;
	
	public Pair( String symbol, Function value )
	{
		this.symbol = symbol;
		this.value = value;
	}
	
	@Override
	public Function eval()
	{
		return this;
	}
	
	@Override
	public Function bind(Function f)
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(symbol);
		sb.append(':');
		sb.append(value);
		return sb.toString();
	}
}
