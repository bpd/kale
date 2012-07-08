package kaygan.ast;

import kaygan.Token;

public class Range extends Exp
{
	public final Exp from; // Value | Symbol
	public final Token between;
	public final Exp to;   // Value | Symbol
	
	public Range(Value from, Token between, Value to)
	{
		this.from = from;
		this.between = between;
		this.to = to;
	}
	
	public Range(Symbol from, Token between, Symbol to)
	{
		this.from = from;
		this.between = between;
		this.to = to;
	}
	
	public Range(Exp from, Token between, Exp to)
	{
		this.from = from;
		this.between = between;
		this.to = to;
	}
	
}
