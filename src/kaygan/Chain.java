package kaygan;

public class Chain extends Sequence
{
	
	@Override
	public Function bind(Function f)
	{
		System.out.println("chain.bind(): " + f);
		
		if( f instanceof Sequence
			&& size() == 0 )
		{
			System.out.println("found function");
			return new Function()
			{
				@Override
				public Function bind(Function f)
				{
					return f;
				}
				
				@Override
				public Function eval()
				{
					return this;
				}
				
				@Override
				public String toString()
				{
					return "<Function>";
				}
			};
		}
		
		return super.bind(f);
	}

//	public void add(Function element)
//	{
//		if( element instanceof Sequence
//			&& size() == 0 )
//		{
//			// first element is a sequence,
//			// this turns this chain into a function
//			
//			//Scope subScope = new Scope(this.scope);
//			
//			Sequence arguments = (Sequence)element;
//			
//			for( Function argument : arguments )
//			{
//				if( argument instanceof Symbol )
//				{
//					// TODO new empty binding instead of null?
//					//subScope.set( (Symbol)argument, null );
//				}
//				else if( argument instanceof Pair )
//				{
//					Pair pair = (Pair)argument;
//					//subScope.set( pair.symbol, pair.value );
//				}
//				else
//				{
//					throw new RuntimeException("Expected Symbol or Pair");
//				}
//			}
//		}
//		else
//		{
//			super.add( element );
//		}
//	}
//	
//	public Binding bind(Bindable parent)
//	{
//		Bindable head = elements.get(0);
//		if( head instanceof Sequence )
//		{
//			// head is a sequence, this is a function
//			
//			Sequence arguments = (Sequence)head;
//			
//			for( Bindable argument : arguments )
//			{
//				
//				
//				
//			}
//			
//		}
//		else
//		{
//			// not a function, so evaluate each element of the chain
//			// in the context of the previous element
//			
//			Bindable previous = this;
//			
//			for( Bindable element : this.elements )
//			{
//				element.bind(previous);
//				previous = element;
//			}
//			
//		}
//		
//		return null;
//	}
	
	
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
