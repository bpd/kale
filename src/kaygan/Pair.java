package kaygan;

public class Pair
{
	public final String symbol;
	
	public final Object value;
	
	public Pair( String symbol, Object value )
	{
		this.symbol = symbol;
		this.value = value;
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
