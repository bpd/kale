package kaygan.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaygan.type.Type;

public abstract class ASTNode
{
	private List<String> errors = null;
	
	public Type type;
	
	public boolean hasErrors()
	{
		return errors != null && errors.size() > 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getErrors()
	{
		return errors != null ? errors : Collections.EMPTY_LIST;
	}
	
	public void error(String message)
	{
		if( errors == null )
		{
			errors = new ArrayList<String>(2);
		}
		errors.add( message );
	}
	
	public abstract int getOffset();
	
	public abstract int getLength();
	
	public boolean overlaps(int offset)
	{
		int nodeOffset = getOffset();
		return nodeOffset <= offset && (nodeOffset+getLength()) > offset;
	}
	
	public abstract ASTNode findNode(int offset);
	
}
