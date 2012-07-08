package kaygan.ast;

import java.util.List;

import kaygan.Token;

public class Callsite extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> contents;
	
	public Callsite(Token open, Token close, List<Exp> contents)
	{
		if( contents.size() < 1 )
		{
			throw new IllegalArgumentException(
					"Callsite must have at least one expression");
		}
		this.open = open;
		this.close = close;
		this.contents = contents;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Callsite: ");
		sb.append(" contents:").append(contents);
		sb.append('}');
		return sb.toString();
	}
}
