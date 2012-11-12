package kale.ast;

import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import kale.Scope;
import kale.codegen.ClassName;

public class Assignment extends Expression implements LocalVariable
{
	final Id lvalue;
	
	final Expression rvalue;
	
	/** the target of this assignment */
	ASTNode target;
	
	/** if this is a declaration */
	int localIndex;
	
	public Assignment( Id lvalue, Expression rvalue )
	{
		this.lvalue = lvalue;
		this.rvalue = rvalue;
		lvalue.mode = Id.Mode.Store;
		
		addChild( lvalue );
		addChild( rvalue );
	}
	
	public Id getLValue()
	{
		return lvalue;
	}
	
	public Expression getRValue()
	{
		return rvalue;
	}
	
	@Override
	public int getLocalIndex()
	{
		return localIndex;
	}
	
	@Override
	public void inferType( Scope scope )
	{
		lvalue.inferType(scope);
		
		target = lvalue.getRef();
		if( target == null )
		{
			// assign this new variable a local index
			localIndex = scope.newLocal();
						
			// declaration, the scope entry for this symbol
			// is set to point to this assignment to indicate
			// this is a locally declared variable (as opposed to
			//   a type field or a function parameter)
			scope.set( lvalue.toString(), this );
			
			// we are assigning to 'ourselves'
			target = this;
			
			// don't infer lvalue, since it will be unresolved
			
			rvalue.inferType(scope);
			if( rvalue.getType() == null
				|| rvalue.getType().getName().equals("void") )
			{
				String errorMsg = "Cannot assign void";
				error( errorMsg );
				lvalue.error( errorMsg );
			}
			else
			{
				setType(rvalue.getType());
			}
		}
		else
		{
			// assigning pre-declared variable
			
			//lvalue.inferType(scope);
			rvalue.inferType(scope);
			
			// TODO verify 'target' type is equivalent to rvalue
		}
	}
	
	@Override
	public void emitBytecode(MethodVisitor mv)
	{

		if( target instanceof FieldDecl )
		{
			FieldDecl field = (FieldDecl)target;
			
			// need to load any intermediary objects
			lvalue.emitBytecode(mv);
			
			rvalue.emitBytecode(mv);
			
			mv.visitFieldInsn(	Opcodes.PUTFIELD,
								ClassName.toJavaFriendly( field.getParentType().getFullName() ),
								field.getName(),
								field.getType().toDescriptor() );
		}
		else if( target instanceof LocalVariable )
		{
			// push the rvalue to TOS
			rvalue.emitBytecode(mv);
			
			// store to 'local' index			
			String desc = target.getType().toDescriptor();
			
			// figure out the proper 'load' variant to use for
			// the type of symbol we're referencing
			int instr = Opcodes.ASTORE;
			if( desc.equals("I") || desc.equals("Z") )
			{
				instr = Opcodes.ISTORE;
			}
			mv.visitVarInsn( instr, ((LocalVariable)target).getLocalIndex() );
		}
		else
		{
			throw new IllegalStateException("Expected FieldDecl, ParamDecl, or Assignment, found: " 
												+ target == null ? "null" : target.getClass().getName());
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(lvalue);
		sb.append(" = ");
		sb.append(rvalue);
		return sb.toString();
	}
}
