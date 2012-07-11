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

	@Override
	public int getOffset()
	{
		return from.getOffset();
	}

	@Override
	public int getLength()
	{
		return to.getOffset() - from.getOffset() + to.getLength();
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		if( from.overlaps(offset) )
		{
			return from.findNode(offset);
		}
		
		if( to.overlaps(offset) )
		{
			return to.findNode(offset);
		}
		
		return overlaps(offset) ? this : null;
	}
	
}
