package kaygan.atom;

import kaygan.Bindable;
import kaygan.Binding;
import kaygan.Function;
import kaygan.Scope;

public class Symbol implements Bindable, Function
{
	private final String value;
	
	public Symbol(String value)
	{
		this.value = value;
	}

	@Override
	public Binding bind(Bindable parent)
	{
		return parent.bind(this);
	}
	
	@Override
	public Function eval(Scope scope)
	{
		Function f = scope.get(value);
		if( f == null )
		{
			throw new RuntimeException("Symbol not found: " + value);
		}
		return f.eval(scope);
	}
	
	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return value.equals(obj);
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
	
	private final Binding invokable = new Binding()
	{
		@Override
		public Object invoke(Scope scope)
		{
			return value;
		}
	};
}
