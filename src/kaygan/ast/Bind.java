package kaygan.ast;

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
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{ Bind: ")
			.append(symbol).append(':').append(exp).append('}');
		return sb.toString();
	}
}
