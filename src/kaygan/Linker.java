package kaygan;

import kaygan.ast.*;
import kaygan.type.Type;

public class Linker
{
	public static void link(Program program)
	{
		Scope scope = new Scope();
		
		scope.set("Num", Num.TYPE);
		scope.set("String", Str.TYPE);
		
		for( Exp exp : program )
		{
			link( exp, scope );
		}
	}
	
	static void link(ASTNode node, Scope scope)
	{
		if( node instanceof Array )
		{
			for( Exp e : ((Array)node).contents )
			{
				link(e, scope);
			}
		}
		else if( node instanceof Bind )
		{
			Bind bind = (Bind)node;
			
			if( bind.exp instanceof Bind )
			{
				bind.exp.error("Cannot bind to a bind");
			}
			else
			{
				// link the expression before we set the scope
				// that way it is not seeing its own value
				link( bind.exp, scope );
				
				String key = bind.symbol.symbol();
				
				Object bound = scope.getLocal(key);
				
				if( bound != null )
				{
					bind.error("Symbol " + key + " already bound to " + bound);
				}
				else
				{
					scope.set( key, bind.exp );
				}
				
				bind.symbol.ref = bind.exp;
			}
		}
		else if( node instanceof Callsite )
		{
			for( Exp e : ((Callsite)node).contents )
			{
				link(e, scope);
			}
		}
		else if( node instanceof Function )
		{
			Function f = (Function)node;
			
			Scope functionScope = scope.newSubScope();
			
			// link arguments
			for( Exp arg : f.args )
			{
				if( arg instanceof Bind )
				{
					link( arg, functionScope );
				}
				else if( arg instanceof Symbol )
				{
					Symbol symbol = (Symbol)arg;
					String key = symbol.symbol();
					
					if( functionScope.getLocal(key) != null )
					{
						symbol.error("Duplicate argument name");
						symbol.ref = Type.ERROR;
					}
					else
					{
						functionScope.set( key, Type.ANY);
						symbol.ref = Type.ANY;
					}
				}
				else
				{
					arg.error(" Expected Bind | Symbol ");
				}
			}
			
			// link contents
			for( Exp exp : f.contents )
			{
				link( exp, functionScope );
			}
			
		}
		else if( node instanceof Value )
		{
			// Num, Str
		}
		else if( node instanceof Symbol )
		{
			Symbol symbol = (Symbol)node;
			Object o = scope.get( symbol.symbol() );
			if( o != null && o instanceof ASTNode )
			{
				symbol.ref = (ASTNode)o;
			}
			else
			{
				symbol.error("Unknown reference");
				symbol.ref = Type.ERROR;
			}
		}
	}
}
