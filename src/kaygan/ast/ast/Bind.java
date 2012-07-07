package kaygan.ast.ast;

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
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{ Bind: ")
			.append(symbol).append(':').append(exp).append('}');
		return sb.toString();
	}
}
