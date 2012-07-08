package kaygan.ast;

import java.util.List;

import kaygan.Token;

public class Array extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> contents;
	
	public Array(Token open, Token close, List<Exp> contents)
	{
		this.open = open;
		this.close = close;
		this.contents = contents;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Array: ");
		sb.append(" contents:").append(contents);
		sb.append('}');
		return sb.toString();
	}
}
