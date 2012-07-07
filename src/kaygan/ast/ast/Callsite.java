package kaygan.ast.ast;

import java.util.List;

import kaygan.ast.Token;

public class Callsite extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> contents;
	
	public Callsite(Token open, Token close, List<Exp> contents)
	{
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
