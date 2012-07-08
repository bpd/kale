package kaygan.ast;

import java.util.List;

import kaygan.Token;

public class Symbol extends Exp
{
	private List<Token> parts;
	
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
