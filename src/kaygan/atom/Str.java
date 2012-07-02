package kaygan.atom;

import kaygan.Bindable;
import kaygan.Binding;
import kaygan.Scope;

public class Str implements Bindable
{
	private final String value;
	
	public Str(String value)
	{
		this.value = value;
	}

	@Override
	public Binding bind(Bindable parent)
	{
		return invokable;
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
