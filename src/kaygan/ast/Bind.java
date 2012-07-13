package kaygan.ast;

import kaygan.Scope;
import kaygan.type.Type;

public class Bind extends Exp
{
	public final Symbol symbol;
	
	public final Exp exp;
	
	public Bind(Symbol symbol, Exp exp)
	{
		this.symbol = symbol;
		this.exp = exp;
	}
	
	
	
	@Override
	public int getOffset()
	{
		return symbol.getOffset();
	}

	@Override
	public int getLength()
	{
		return exp.getOffset() - symbol.getOffset() + exp.getLength();
	}

	@Override
	public ASTNode findNode(int offset)
	{
		if( symbol.overlaps(offset) )
		{
			return symbol;
		}
		
		if( exp.overlaps(offset) )
		{
			return exp.findNode(offset);
		}
		
		return overlaps(offset) ? this : null;
	}

	@Override
	public Type inferType(Scope scope)
	{
		return exp.inferType(scope);
	}
	
	@Override
	public Type getType()
	{
		return exp.getType();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{ Bind: ")
			.append(symbol).append(':').append(exp).append('}');
		return sb.toString();
	}
}
