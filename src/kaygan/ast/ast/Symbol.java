package kaygan.ast.ast;

import java.util.List;

import kaygan.ast.Token;

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
