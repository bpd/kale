package kaygan.ast;

import java.util.List;

import kaygan.Scope;
import kaygan.Token;

public class Function extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> args;
	
	public final List<Exp> contents;
	
	// TODO this really needs to be moved somewhere else, after
	//      the interpreter is more fleshed out
	public Scope scope;
	
	public Function(Token open, Token close, List<Exp> args, List<Exp> contents)
	{
		this.open = open;
		this.close = close;
		this.args = args;
		this.contents = contents;
	}
	
	@Override
	public int getOffset()
	{
		return open.beginOffset;
	}

	@Override
	public int getLength()
	{
		return close.endOffset - open.beginOffset;
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
