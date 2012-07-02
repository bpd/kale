package kaygan;

import java.util.HashMap;
import java.util.Map;

public class Scope
{
	private final Scope parent;
	
	private final Map<Object, Function> bindings = new HashMap<Object, Function>();
	
	public Scope()
	{
		this( null );
	}
	
	public Scope( Scope parent )
	{
		this.parent = parent;
	}
	
	public Function get(Object name)
	{
		Function binding = bindings.get(name);
		if( binding == null && parent != null )
		{
			binding = parent.get(name);
		}
		return binding;
	}
	
	public void set(Object name, Function binding)
	{
		bindings.put(name, binding);
	}
}
