package kaygan.ast;

import kaygan.Token;

public class Symbol extends Exp
{
	public final Token symbol;
	
	public Symbol(Token symbol)
	{
		this.symbol = symbol;
	}
	
	public String symbol()
	{
		return symbol.value;
	}
	
	
	@Override
	public int getOffset()
	{
		return symbol.beginOffset;
	}

	@Override
	public int getLength()
	{
		return symbol.endOffset - symbol.beginOffset;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		return overlaps(offset) ? this : null;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("Symbol: ");
		sb.append(symbol);
		sb.append('}');
		return sb.toString();
	}
}
