package kaygan.atom;

import kaygan.Bindable;
import kaygan.Binding;
import kaygan.Function;
import kaygan.Scope;

public class Pair implements Bindable, Function
{
	public final String symbol;
	
	public final Function value;
	
	public Pair( String symbol, Function value )
	{
		this.symbol = symbol;
		this.value = value;
	}
	
	@Override
	public Function eval(Scope scope)
	{
		return this;
	}
	
	@Override
	public Binding bind(Bindable parent)
	{
		
		return null;
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
