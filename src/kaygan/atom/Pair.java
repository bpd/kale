package kaygan.atom;

import kaygan.Function;
import kaygan.Type;

public class Pair extends Function
{
	public final String symbol;
	
	public final Function value;
	
	public Pair( String symbol, Function value )
	{
		this.symbol = symbol;
		this.value = value;
	}
	
	@Override
	public void setParent(Function parent)
	{
		this.parent = parent;
		this.value.setParent(parent);
	}
	
	@Override
	public Function eval()
	{
		return this;
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
	public Type getType()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<Pair<");
		sb.append(value.getType().toString());
		sb.append(">>");
		return new Type(sb.toString());
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
