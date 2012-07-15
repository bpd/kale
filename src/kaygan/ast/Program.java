package kaygan.ast;

import kaygan.Scope;
import kaygan.type.ListType;
import kaygan.type.Type;

public class Program extends Block implements Iterable<Exp>
{
	public Program( Exp[] exps )
	{
		super( exps );
	}

	
	public void link()
	{
		Scope scope = new Scope();
		
		scope.set("Num", Num.TYPE);
		scope.set("String", Str.TYPE);
		
		super.link(scope);
	}
	
	@Override
	public Type inferType()
	{
		super.inferType();
		
		if( this.type != null )
		{
			return this.type;
		}
		
		if( this.exps.length == 1 )
		{
			this.type = this.exps[0].inferType();
		}
		else
		{
			// TODO structural vs list type?
			ListType type = new ListType();
			for( Exp e : this )
			{
				type.add( e.inferType() );
			}
			this.type = type;
		}
		
		return this.type;
	}

	
	@Override
	public String toString()
	{
		return "<Program>";
	}
	
}
