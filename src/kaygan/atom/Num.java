package kaygan.atom;

import kaygan.Function;

public class Num implements Function
{
	private final Number value;
	
	public Num(Number value)
	{
		this.value = value;
	}
	
	public Function eval()
	{
		return this;
	}

	@Override
	public Function bind(Function f)
	{
		
		
		
		
//		if( caller instanceof Symbol )
//		{
//			Symbol symbolRef = (Symbol)caller;
//			if( symbolRef.equals("+") )
//			{
//				return new Binding()
//				{
//					@Override
//					public Binding bind(Bindable parent)
//					{
//						if( !(caller instanceof Num) )
//						{
//							return null; //new Error("'+' requires Num");
//						}
//						return caller.bind(null);
//					}
//
//					@Override
//					public Object invoke(Scope scope) {
//						// TODO Auto-generated method stub
//						return null;
//					}
//				};
//			}
//		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return value.toString();
	}
}
