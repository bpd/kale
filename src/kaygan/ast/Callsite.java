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

	@Override
	public ASTNode findNode(int offset)
	{
		// if this node has errors, we want
		// it to 'cover' its contents
		if( hasErrors() && overlaps(offset) )
		{
			return this;
		}
		
		for( Exp exp : contents )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		return overlaps(offset) ? this : null;
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
