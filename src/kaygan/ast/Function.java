package kaygan.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.FunctionType;
import kaygan.type.Type;

public class Function extends Exp
{
	public final Token open;
	public final Token close;
	
	public final List<Exp> args;
	
	public final List<Exp> contents;
	
	// TODO this really needs to be moved somewhere else, after
	//      the interpreter is more fleshed out
	public Scope scope;
	
	private Type type;
	
	public Function(Token open, Token close, List<Exp> args, List<Exp> contents)
	{
		this.open = open;
		this.close = close;
		this.args = args;
		this.contents = contents;
		
		// build the initial type information based
		// on what we know from the arguments
		Type[] argTypes = new Type[args.size()];
		for( int i=0; i<argTypes.length; i++ )
		{
			argTypes[i] = args.get(i).getType();
		}
		
		Type retType = Type.ANY;
		if( contents.size() > 0 )
		{
			retType = contents.get( contents.size() - 1 ).getType();
		}
		
		this.type = new FunctionType(argTypes, retType);
	}
	
	@Override
	public int getOffset()
	{
		return open.beginOffset;
	}

	@Override
	public int getLength()
	{
		return close.endOffset - open.beginOffset;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		for( Exp exp : args )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		for( Exp exp : contents )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		return overlaps(offset) ? this : null;
	}
	
	@Override
	public void verify()
	{
		final Map<String, Symbol> argNames = new HashMap<String, Symbol>();
		
		for( Exp arg : args )
		{
			Symbol symbol = null;
			if( arg instanceof Bind )
			{
				Bind bind = (Bind)arg;
				symbol = bind.symbol;
			}
			else if( arg instanceof Symbol )
			{
				symbol = (Symbol)arg;
			}
			
			// verify the symbol hasn't already been used for an
			// argument in this function
			if( symbol != null )
			{
				String key = symbol.symbol();
				
				if( argNames.containsKey(key) )
				{
					symbol.error( "Duplicate argument name" );
				}
				else
				{
					argNames.put( key, symbol );
				}
			}
		}
		
		for( Exp e : contents )
		{
			e.verify();
		}
	}
	
	@Override
	public Type inferType(Scope scope)
	{
		// create a scope for this function
		scope = scope.newSubScope();
		
		// infer argument types
		Type[] argTypes = new Type[args.size()];
		for( int i=0; i<argTypes.length; i++ )
		{
			Exp arg = args.get(i);
			if( arg instanceof Symbol )
			{
				argTypes[i] = Type.ANY;
				scope.set( ((Symbol)arg).symbol(), Type.ANY );
			}
			else if( arg instanceof Bind )
			{
				argTypes[i] = arg.inferType(scope);
			}
		}
		
		// process any binds in this function, since
		// the return type could depend on intermediary types
		for( Exp exp : contents )
		{
			if( exp instanceof Bind )
			{
				((Bind)exp).inferType(scope);
			}
		}
		
		// infer return type
		Type retType = Type.ANY;
		if( contents.size() > 0 )
		{
			retType = contents.get( contents.size() -1 ).inferType(scope);
		}
		
		this.type = new FunctionType( argTypes, retType );
		
		return this.type;
	}
	
	@Override
	public Type getType()
	{
		return type;
	}


	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Function: ");
		sb.append("args:").append(args);
		sb.append(' ').append(" contents:").append(contents);
		sb.append('}');
		return sb.toString();
	}
	
}
