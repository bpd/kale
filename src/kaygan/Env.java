package kaygan;

import java.util.HashMap;
import java.util.Map;

import kaygan.cell.Atom;
import kaygan.cell.Cell;
import kaygan.cell.Cell.Type;
import kaygan.cell.Cons;

public class Env
{
	private final Env parent;
	
	private final Map<Atom, Cell> bindings = new HashMap<Atom, Cell>();
	
	public Env( )
	{
		this( ROOT );
	}
	
	public Env( Env parent )
	{
		this.parent = parent;
	}
	
	public void set(Atom key, Cell value)
	{
		bindings.put( key, value );
	}
	
	public Cell get(Atom key)
	{
		Cell bound = bindings.get(key);
		if( bound == null )
		{
			bound = parent.get(key);
		}
		return bound;
	}
	
	public Env nest()
	{
		return new Env(this);
	}
	
	public Cell eval(Cell cell)
	{
		if( cell.getType() == Type.Nil )
		{
			return Type.Nil;
		}
		else if( cell.getType().isInstance(Type.Cons) )
		{
			//System.out.println( cell.getClass().getName() + " -> " + cell );
			Cons cons = (Cons)cell;
			if( cons.left.getType() == Type.Symbol )
			{
				//System.out.println("symbol: " + cons.left);
				// bind the left symbol to whatever the right evals to
				set( (Atom)cons.left, eval(cons.right) );
			}
			else
			{
				//System.out.println("eval left/right");
				eval(cons.left);
				return eval(cons.right);
			}
		}
		else if( cell.getType().isInstance(Type.Atom) )
		{
			//System.out.println("atom");
			
			Atom atom = (Atom)cell;
			if( atom.getType() == Type.Symbol )
			{
				return get(atom);
			}
		}

		return cell;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Env{");
		sb.append(bindings.toString());
		sb.append('}');
		return sb.toString();
	}
	
	
	private static final Env ROOT = new Env()
	{
		@Override
		public Cell get(Atom key)
		{
			return Type.Nil;
		}
		
		@Override
		public void set(Atom key, Cell value)
		{
			// no-op
		}
	};
}
