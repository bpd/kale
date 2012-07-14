package kaygan;

import kaygan.ast.*;
import kaygan.type.FunctionType;
import kaygan.type.ListType;
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
			node.type = Type.ANY;
			
			for( Exp exp : ((Callsite)node).contents )
			{
				infer( exp );
			}
		}
		else if( node instanceof Function )
		{
			Function f = (Function)node;
			
			Type[] argTypes = new Type[f.args.size()];
			for( int i=0; i<argTypes.length; i++ )
			{
				Exp arg = f.args.get(i);
				
				infer( arg );
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
			
			
			symbol.type = symbol.ref instanceof Type 
							? (Type)symbol.ref
							: symbol.ref.type;
		}
		else if( node instanceof Value )
		{
			// values should have set their own type
		}
	}
}
