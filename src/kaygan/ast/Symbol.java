package kaygan.ast;

import java.util.List;

import kaygan.Token;

public class Symbol extends Exp
{
	public final List<Token> parts;
	
	public Symbol(List<Token> parts)
	{
		if( parts.size() == 0 )
		{
			throw new IllegalArgumentException("Symbol parts must exceed 0");
		}
		this.parts = parts;
	}
	
	public String symbol()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(parts.get(0).value);
		for(int i=1; i<parts.size(); i++)
		{
			sb.append('.');
			sb.append(parts.get(i).value);
		}
		return sb.toString();
	}
	
	
	
	@Override
	public int getOffset()
	{
		return parts.get(0).beginOffset;
	}

	@Override
	public int getLength()
	{
		Token first = parts.get(0);
		Token last = parts.get(parts.size()-1);
		return last.endOffset - first.beginOffset;
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
		sb.append(parts.get(0));
		for(int i=1; i<parts.size(); i++)
		{
			sb.append('.');
			sb.append(parts.get(i));
		}
		sb.append('}');
		return sb.toString();
	}
}
