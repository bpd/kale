package kale.ast;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;

import kale.Scope;

public abstract class ASTNode
{
	private List<String> errors = null;
	
	private Type type = null;
	
	public final List<ASTNode> children = new ArrayList<ASTNode>();
	
	public void addChild( ASTNode child )
	{
		children.add( child );
	}
	
	public void addChildren( ASTNode[] children )
	{
		for( ASTNode child : children )
		{
			this.children.add( child );
		}
	}
	
	public List<ASTNode> collectChildren( Class<?> clazz )
	{
		List<ASTNode> collection = new ArrayList<ASTNode>();
		
		collectChildren( this, clazz, collection );
		
		return collection;
	}
	
	protected static void collectChildren( ASTNode node, Class<?> clazz, List<ASTNode> collection )
	{
		for( ASTNode child : node.children )
		{
			if( clazz.isInstance(child) )
			{
				collection.add( child );
			}
			
			collectChildren( child, clazz, collection );
		}
	}
	
	public final Type getType()
	{
		return type;
	}
	
	public final void setType(Type type)
	{
		this.type = type;
	}
	
	public boolean isInvocable()
	{
		return false;
	}
	
	public void emitBytecode(MethodVisitor mv)
	{
		throw new UnsupportedOperationException();
	}
	
	int beginOffset = -1;
	int endOffset = -1;
	
	public int getBeginOffset()
	{
		if( beginOffset == -1 )
		{
			beginOffset = findSmallestBeginOffset( this.children );
		}
		return beginOffset;
	}
	
	protected int findSmallestBeginOffset( List<ASTNode> nodes )
	{
		int smallestBeginOffset = Integer.MAX_VALUE;
		for( ASTNode node : nodes )
		{
			int nodeBeginOffset = node.getBeginOffset();
			if( nodeBeginOffset < smallestBeginOffset
				&& nodeBeginOffset >= 0 )
			{
				smallestBeginOffset = nodeBeginOffset;
			}
		}
		return smallestBeginOffset;
	}
		
	public int getEndOffset()
	{
		if( endOffset == -1 )
		{
			endOffset = findLargestEndOffset( this.children );
		}
		return endOffset;
	}
	
	protected int findLargestEndOffset( List<ASTNode> nodes )
	{
		int largestEndOffset = 0;
		for( ASTNode node : nodes )
		{
			int nodeEndOffset = node.getEndOffset();
			if( nodeEndOffset > largestEndOffset
				&& nodeEndOffset < Integer.MAX_VALUE )
			{
				largestEndOffset = nodeEndOffset;
			}
		}
		return largestEndOffset;
	}
	
	public final int getLength()
	{
		return getEndOffset() - getBeginOffset() - 1;
	}
	
	public boolean hasLocalErrors()
	{
		return errors != null && errors.size() > 0;
	}
	
	public boolean hasErrors()
	{
		boolean hasLocalErrors = hasLocalErrors();
		if( !hasLocalErrors )
		{
			// no local errors, check children
			for( ASTNode child : this.children )
			{
				if( child != null && child.hasErrors() )
				{
					return true;
				}
			}
		}
		return hasLocalErrors;
	}
	
	public List<String> getErrors()
	{
		return getErrors( this, new ArrayList<String>() );
	}
	
	protected static List<String> getErrors(ASTNode node, List<String> errors)
	{
		// first collect this node's errors
		if( node.errors != null )
		{
			for( String error : node.errors )
			{
				errors.add( error );
			}
		}
		
		// then collect the errors of its children
		for( ASTNode child : node.children )
		{
			getErrors( child, errors );
		}
		return errors;
	}
	
	public void error(String message)
	{
		if( errors == null )
		{
			errors = new ArrayList<String>(2);
		}
		errors.add( message );
	}
	
	public boolean overlaps(int offset)
	{
		return getBeginOffset() <= offset
				&& getEndOffset() > offset;
	}
	
	public ASTNode findNode(int offset)
	{
		return findNode( this, offset );
	}
	
	protected static ASTNode findNode(ASTNode node, int offset)
	{
		if( node.overlaps(offset) )
		{
			// an error node short circuits the node locator
			if( node.hasLocalErrors() )
			{
				return node;
			}
			
			for( ASTNode child : node.children )
			{
				if( child.overlaps(offset) )
				{
					return findNode(child, offset);
				}
			}
			return node;
		}
		return null;
	}
	
	public void inferType(Scope scope)
	{
		setType( null );
	}

}
