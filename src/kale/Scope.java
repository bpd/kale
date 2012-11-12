package kale;

import java.util.HashMap;
import java.util.Map;

import kale.ast.ASTNode;
import kale.ast.Id;

public class Scope implements ReadOnlyScope
{
	private final Scope parent;
	
	private final Map<String, ASTNode> bindings = new HashMap<String, ASTNode>();
	
	/** the number of local variables */
	int localCount;
	
	public Scope(Scope parent)
	{
		this.parent = parent;
	}
	
	public Scope()
	{
		this.parent = ROOT;
	}
	
	public Scope withLexical( Scope lexical )
	{
		return new MergedScope( this, lexical );
	}
	
	public int getLocalCount()
	{
		return localCount;
	}
	
	public int newLocal()
	{
		return localCount++;
	}
	
	public ASTNode get(Id id)
	{
		return get(id.toString());
	}
	
	public ASTNode get(String symbol)
	{
		ASTNode binding = bindings.get(symbol);
		if( binding == null )
		{
			binding = parent.get(symbol);
		}
		return binding;
	}
	
	public boolean containsSymbol(String symbol)
	{
		return get(symbol) != null;
	}
	
//	public boolean containsLocalSymbol(String symbol)
//	{
//		return getLocal(symbol) != null;
//	}
	
//	public ASTNode getLocal(String symbol)
//	{
//		return bindings.get(symbol);
//	}
	
	public void set(String symbol, ASTNode value)
	{
		bindings.put(symbol, value);
	}
	
	public Scope newSubScope()
	{
		return new Scope(this);
	}
	
	public Scope newFrameSubScope()
	{
		Scope scope = new Scope(this);
		
		// frames extend the locals[] of their parent
		scope.localCount = this.getLocalCount();
		
		return scope;
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
		public ASTNode get(String symbol)
		{
			return null;
		}
		
		@Override
		public void set(String symbol, ASTNode value)
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
