package kaygan;

import kaygan.atom.Pair;
import kaygan.atom.Symbol;
import kaygan.type.Any;

public class Chain extends Sequence
{
	
	@Override
	public Function bind(Function f)
	{
		System.out.println("chain.bind(): " + f);
		
		
		return super.bind(f);
	}
	
	@Override
	public Function bindTo(Function function)
	{
		System.out.println("chain.bindTo(): " + function + ":" + function.getClass().getSimpleName()
							+ " from " + this + ":" + this.getClass().getSimpleName());

		if( elements.size() > 0 )
		{
			Function first = elements.get(0);
			if( first instanceof Sequence )
			{
				// this is a function
				System.out.println("function");
				
				setParent(function);
				
				Sequence arguments = (Sequence)first;
				
				for( Function argument : arguments )
				{
					if( argument instanceof Symbol )
					{
						set((Symbol)argument, new Any());
					}
					else if( argument instanceof Pair )
					{
						Pair pair = (Pair)argument;
						set(pair.symbol, pair.value);
					}
					else
					{
						throw new RuntimeException("Illegal function in arguments: " + argument);
					}
				}
				
				if( elements.size() > 1 )
				{
					Function previous = this;
					
					for( int i=1; i<elements.size(); i++ )
					{
						Function element = elements.get(i);
						
						Function bound = previous.bind(element);
						
						elements.set(i, bound);
						
						previous = bound;
					}
				}
				
				return this;
			}
		}
		

		return super.bindTo(function);
	}

	@Override
	public Function eval()
	{
//		Function previous = null;
//		for( int i=0; i<elements.size(); i++ )
//		{
//			
//		}
//		return null;
		
		return this;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for( Object element : this )
		{
			sb.append(' ');
			sb.append(element);
		}
		sb.append(' ');
		sb.append(')');
		
		return sb.toString();
	}
}
