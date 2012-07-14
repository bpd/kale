package kaygan;

import kaygan.ast.*;
import kaygan.type.FunctionType;
import kaygan.type.ListType;
import kaygan.type.NamedType;
import kaygan.type.Type;

public class TypeInference
{
	public static void infer(ASTNode node)
	{
		if( node.type != null )
		{
			// we've already inferred this type
		}
		else if( node.hasErrors() )
		{
			node.type = Type.ERROR;
		}
		else if( node instanceof Program )
		{
			for( Exp e : ((Program)node) )
			{
				infer( e );
			}
			node.type = Type.ANY; // FIXME structural type
		}
		else if( node instanceof Array )
		{
			ListType type = new ListType();
			
			for( Exp exp : ((Array)node).contents )
			{
				if( !(exp instanceof Bind) )
				{
					infer(exp);
					type.add( exp.type );
				}
			}
			
			node.type = type;
		}
		else if( node instanceof Bind )
		{
			Bind bind = ((Bind)node);
			
			infer( bind.exp );
			
			bind.type = bind.symbol.type = bind.exp.type;
		}
		else if( node instanceof Callsite )
		{
			for( Exp exp : ((Callsite)node).contents )
			{
				infer( exp );
			}
			
			Exp first = ((Callsite) node).contents.get(0);
			
			if( first instanceof Symbol )
			{
				Symbol symbol = (Symbol)first;
				
				node.type = symbol.type;
			}
			else if( first instanceof Callsite )
			{
				if( first.type instanceof FunctionType )
				{
					node.type = ((FunctionType)first.type).getRetType();
				}
				else
				{
					node.type = first.type;
				}
			}
			else
			{
				node.error("Expected symbol");
				node.type = Type.ERROR;
			}
		}
		else if( node instanceof Function )
		{
			Function f = (Function)node;
			
			Type[] argTypes = new Type[f.args.size()];
			for( int i=0; i<argTypes.length; i++ )
			{
				Exp arg = f.args.get(i);
				
				if( arg instanceof Symbol )
				{
					Symbol symbolArg = (Symbol)arg;
					
					symbolArg.type = new NamedType(
										"Type<"+symbolArg.symbol()+">")
					{
						// generated type for argument
						@Override
						public boolean accept(Type type)
						{
							return false;
						}
					};
				}
				else if( arg instanceof Bind )
				{
					infer( arg );
				}
				else
				{
					arg.error("Expected Symbol | Bind");
					arg.type = Type.ERROR;
				}
				
				argTypes[i] = arg.type;
			}
			
			Type retType = Type.ANY;
			if( f.contents.size() > 0 )
			{
				Exp last = f.contents.get( f.contents.size() -1 );
				
				infer( last );
				retType = last.type;
			}
			
			f.type = new FunctionType( argTypes, retType );
		}
		else if( node instanceof Symbol )
		{
			Symbol symbol = (Symbol)node;

			infer( symbol.ref );
			
			if( symbol.ref instanceof Type )
			{
				symbol.type = (Type)symbol.ref;
			}
			else if( symbol.ref instanceof Function )
			{
				if( symbol.ref.type instanceof FunctionType )
				{
					FunctionType refType = (FunctionType)symbol.ref.type;
					node.type = refType.getRetType();
				}
				else
				{
					node.error("Expected function to have function type");
					node.type = Type.ERROR;
				}
			}
			else
			{
				symbol.type = symbol.ref.type;
			}
		}
		else if( node instanceof Value )
		{
			// values should have set their own type
		}
	}
}
