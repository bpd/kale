package kaygan.ast;

import java.util.Iterator;
import java.util.List;

public class Program extends ASTNode implements Iterable<Exp>
{
	private final List<Exp> exps;
	
	public Program(List<Exp> exps)
	{
		this.exps = exps;
	}
	
	public int size()
	{
		return exps.size();
	}
	
	public Exp get(int i)
	{
		return exps.get(i);
	}
	
	@Override
	public Iterator<Exp> iterator()
	{
		return exps.iterator();
	}
	
	@Override
	public int getOffset()
	{
		return exps.size() > 0 ? exps.get(0).getOffset() : 0;
	}

	@Override
	public int getLength()
	{
		if( exps.size() > 0 )
		{
			Exp first = exps.get(0);
			Exp last = exps.get( exps.size() - 1 );
			return last.getOffset() - first.getOffset() + last.getLength();
		}
		return 0;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		for( Exp exp : exps )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		return overlaps(offset) ? this : null;
	}
	
	@Override
	public String toString()
	{
		return "<Program>";
	}
	
}
