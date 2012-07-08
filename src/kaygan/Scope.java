package kaygan;

import java.util.HashMap;
import java.util.Map;

import kaygan.ast.Exp;

public class Scope
{
	private final Scope parent;
	
	private final Map<String, Object> bindings = new HashMap<String, Object>();
	
	public Scope(Scope parent)
	{
		this.parent = parent;
	}
	
	public Scope()
	{
		this.parent = ROOT;
	}
	
	public Object get(String symbol)
	{
		Object binding = bindings.get(symbol);
		if( binding == null )
		{
			binding = parent.get(symbol);
		}
		System.out.println(symbol + " resolved to " + binding);
		return binding;
	}
	
	public void set(String symbol, Object value)
	{
		System.out.println(symbol + " => " + value);
		
		bindings.put(symbol, value);
	}
	
	public Scope newSubScope()
	{
		return new Scope(this);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("local: ").append(bindings);
		sb.append("parent: [").append( parent.toString()).append(']');
		return sb.toString();
	}
	
	private static final Scope ROOT = new Scope(null)
	{
		@Override
		public Exp get(String symbol)
		{
			return null;
		}
		
		@Override
		public void set(String symbol, Object value)
		{
			// no-op
		}
		
		@Override
		public String toString()
		{
			return "";
		}
	};
}
