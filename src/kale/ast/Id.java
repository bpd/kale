package kale.ast;

import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import kale.ReadOnlyScope;
import kale.Scope;
import kale.Token;
import kale.codegen.ClassName;

public class Id extends Expression
{
	public enum Mode
	{
		Load,
		Store
	}
	
	final Expression previous;
	
	// TODO should be an Id
	final String id;
	
	ASTNode ref;
	
	Mode mode = Mode.Load;
	
	final int beginOffset;
	
	final int endOffset;
	
	public Id(Token token)
	{
		this( token, null );
	}
	
	public Id(Token token, Expression previous)
	{
		this.id = token.value;
		this.previous = previous;
		
		this.beginOffset = token.beginOffset;
		this.endOffset = token.endOffset;
		
		if( previous != null )
		{
			addChild( previous );
		}
	}
	
	public Id(String id)
	{
		this( id, null );
	}
	
	public Id(String id, Expression previous)
	{
		this.id = id;
		this.previous = previous;
		
		beginOffset = 0;
		endOffset = Integer.MAX_VALUE;
		
		if( previous != null )
		{
			addChild( previous );
		}
	}
	
	public ASTNode getRef()
	{
		return ref;
	}
	
	@Override
	public boolean isInvocable()
	{
		return true;
	}
	
	@Override
	public int getBeginOffset()
	{
		return beginOffset;
	}
	
	@Override
	public int getEndOffset()
	{
		return endOffset;
	}
	
	
	@Override
	public void inferType(Scope parentScope)
	{
		ReadOnlyScope scope = parentScope;
		
		if( previous != null )
		{
			// this will walk the 'previous' linked list back to the initial element
			// in this qualified id
			//
			// then this element in the list should be inferred within the scope
			// of the previous element
			previous.inferType( parentScope );
			
			Type previousType = previous.getType();
			if( previousType instanceof Type )
			{
				scope = ((Type)previousType).getScope();
			}
			else
			{
				error("Expected IType, found: " + previousType);
			}
		}
		
		// TODO each element in the QualifiedId chain
		//      uses the scope of the type of the previous element
		//      (not the local or lexical scope)
		
		// each type is set on the Id, then each Id is responsible
		// for pushing its resulting value onto the stack for
		// the next Id to handle, unless it's a PackageDecl
		
		// if the first token is a valid symbol in this scope,
		// treat as member access
		
		// if the first token is not defined in the scope,
		// treat the entire QualifiedId as qualified name for
		// a node in another package
		
		ASTNode target = scope.get( id );
		if( target != null )
		{
			setType( target.getType() );
			this.ref = target;
		}
		else
		{
			// couldn't find the symbol in the scope... not okay
			// unless we're storing
			if( mode != Mode.Store )
			{
				//setType( Type.ERROR );
				error("Unresolved symbol: " + toString());
			}
		}
	}
	
	
	
	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		if( ref == null )
		{
			throw new IllegalStateException("Missing target");
		}
		
		if( previous != null )
		{
			previous.emitBytecode(mv);
		}
		
		if( mode == Mode.Load )
		{
		
			// Id can be a local variable or refer to a field in the enclosing scope
			if( ref instanceof LocalVariable )
			{
				String desc = ref.getType().toDescriptor();
				
				// figure out the proper 'load' variant to use for
				// the type of symbol we're referencing
				int instr = Opcodes.ALOAD;
				if( desc.equals("I") || desc.equals("Z") )
				{
					instr = Opcodes.ILOAD;
				}
				
				mv.visitVarInsn( instr, ((LocalVariable)ref).getLocalIndex() );
			}
			else if( ref instanceof FieldDecl )
			{
				FieldDecl field = (FieldDecl)ref;
				
				mv.visitFieldInsn(	Opcodes.GETFIELD,
									ClassName.toJavaFriendly( field.getParentType().getFullName() ),
									field.getName(),
									field.getType().toDescriptor() );
				// GETFIELD (or just invokedynamic and bind it at runtime)
			}
			else if( ref instanceof FunctionDecl )
			{
				// no action needed
			}
			else if( ref instanceof FunctionSignature )
			{
				// no action needed
			}
			else
			{
				throw new IllegalStateException("Unknown Id.ref: " + ref);
			}
		}
		
	}

	public String toString()
	{
		if( previous == null )
		{
			return id;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(previous.toString());
		sb.append('.');
		sb.append(id);
		return sb.toString();
	}

}
