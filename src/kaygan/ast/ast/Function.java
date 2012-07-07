package kaygan.ast.ast;

import java.util.List;

import kaygan.ast.Token;

public class Function extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> args;
	
	public final List<Exp> contents;
	
	public Function(Token open, Token close, List<Exp> args, List<Exp> contents)
	{
		this.open = open;
		this.close = close;
		this.args = args;
		this.contents = contents;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Function: ");
		sb.append("args:").append(args);
		sb.append(' ').append(" contents:").append(contents);
		sb.append('}');
		return sb.toString();
	}
	
}
