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
			Callsite c = (Callsite)node;
			
			for( Exp exp : c.contents )
			{
				infer( exp );
			}
			
			Exp first = c.contents.get(0);
			
			if( first.type instanceof FunctionType )
			{
				FunctionType funcType = (FunctionType)first.type;
				
				System.out.println("function type: " + funcType);
				
//				Function f = resolveFunction( first );
//
//				if( f == null )
//				{
//					first.error("Expected Symbol | Function");
//					first.type = Type.ERROR;
//					return;
//				}
				
//				System.out.println("refers to function: " + f);
				
				// substitute the arguments
				try
				{
					Type[] argTypes = funcType.getArgTypes();
					
					for( int i=1; i<c.contents.size(); i++ )
					{
						Type senderArgType = c.contents.get(i).type;
						
						Type receiverArgType = argTypes[i-1];
						
						System.out.println("substitution: " + funcType);
						System.out.println("substituting " + receiverArgType + " -> " + senderArgType);
						
						funcType = funcType.substitute(receiverArgType, senderArgType);
						
						System.out.println("substitution result: " + funcType);
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				
				node.type = funcType;
				first.type = funcType;
				
			}
			else
			{
				node.type = first.type;
			}

			
//			if( first instanceof Symbol )
//			{
//				Symbol symbol = (Symbol)first;
//				
//				node.type = symbol.type;
//			}
//			else if( first instanceof Callsite )
//			{
//				Callsite c = (Callsite)first;
//				if( c.type instanceof FunctionType )
//				{
//					node.type = ((FunctionType)c.type).getRetType();
//				}
//				else
//				{
//					node.type = first.type;
//				}
//			}
//			else
//			{
//				node.error("Expected symbol");
//				node.type = Type.ERROR;
//			}
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
							return true;
						}
					};
					symbolArg.ref = symbolArg.type;
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
			
			for( Exp e : f.contents )
			{
				infer(e);
			}
			
			Type retType = Type.ANY;
			if( f.contents.size() > 0 )
			{
				Exp last = f.contents.get( f.contents.size() -1 );
				
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
	
//	protected static Function resolveFunction( ASTNode e )
//	{
//		if( e instanceof Function )
//		{
//			return (Function)e;
//		}
//		else if( e instanceof Callsite )
//		{
//			ASTNode first = ((Callsite)e).contents.get(0);
//			Function f = resolveFunction(first);
//			
//			ASTNode fReturn = f.contents.get( f.contents.size() - 1 );
//			
//			return resolveFunction( fReturn );
//		}
//		else if( e instanceof Symbol )
//		{
//			return resolveFunction( ((Symbol)e).ref );
//		}
//		return null;
//	}
}
