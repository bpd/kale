package kale.ast;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import kale.Scope;
import kale.codegen.ClassName;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Invocation extends Expression
{
	FunctionDecl.ThisVar thisType;
	
	// Id | Invocation (the result of an Invocation)
	final ASTNode target;
	
	final Expression[] args;
	
	public Invocation(ASTNode target, Expression[] args)
	{
		this.target = target;
		this.args = args;
		
		addChild( target );
		addChildren( args );
	}
	
	public ASTNode getTarget()
	{
		return target;
	}
	
	public Expression[] getArgs()
	{
		return args;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		ASTNode thisNode = scope.get("this");
		if( thisNode != null )
		{
			if( thisNode instanceof FunctionDecl.ThisVar )
			{
				thisType = (FunctionDecl.ThisVar) thisNode;
			}
		}

		target.inferType(scope);
		
		for( Expression arg : args )
		{
			arg.inferType(scope);
		}
		
		Type type = target.getType();
		if( type instanceof FunctionDecl )
		{
			FunctionDecl functionTarget = (FunctionDecl)type;
			
			// this invocation will return whatever the return type
			// of the target is
			setType( functionTarget.getSignature().getReturnType().getType() );
			
			int expectedArgs = functionTarget.getSignature().getParams().length;
			int foundArgs = this.args.length;
			if( expectedArgs != foundArgs )
			{
				error("Expected " + expectedArgs + ", found " + foundArgs);
				return;
			}
			
			// TODO verify type equivalence of arguments
			ParamDecl[] params = functionTarget.getSignature().getParams();
			for( int i=0; i < this.args.length; i++ )
			{
				Type paramType = params[i].getType();
				Type argType = this.args[i].getType();
				
				if( paramType == null )
				{
					error("Unresolved param type");
				}
				
				if( argType == null )
				{
					error("Unresovled arg type");
				}
				
				if( paramType != null && argType != null )
				{
					if( !paramType.isEquivalent(argType) )
					{
						// TODO: replace with 'encountered ... expected'
						error("Type mismatch: " + paramType.getName() 
								+ " != " + argType.getName());
					}
				}
			}
			
		}
		else if( type instanceof TypeDecl )
		{
			setType(type);
			
			for( Expression arg : this.args )
			{
				arg.error("Constructors take no arguments");
			}
			
		}
		else if( type instanceof FunctionSignature )
		{
			FunctionSignature sigTarget = (FunctionSignature)type;
			
			// this invocation will return whatever the return type
			// of the target is
			setType( sigTarget.getSignature().getReturnType().getType() );
			
			int expectedArgs = sigTarget.getSignature().getParams().length;
			int foundArgs = this.args.length;
			if( expectedArgs != foundArgs )
			{
				error("Expected " + expectedArgs + ", found " + foundArgs);
				return;
			}
			
			// TODO verify type equivalence of arguments
			ParamDecl[] params = sigTarget.getSignature().getParams();
			for( int i=0; i < this.args.length; i++ )
			{
				Type paramType = params[i].getType();
				Type argType = this.args[i].getType();
				
				if( !paramType.isEquivalent(argType) )
				{
					error("Type mismatch: " + paramType + " != " + argType);
				}
			}
		}
		else
		{
			error("Expected function, found: " + type);
			return;
		}
		
	}

	@Override
	public ASTNode findNode(int offset)
	{
		if( target.overlaps(offset) )
		{
			return target.findNode(offset);
		}
		
		for( Expression arg : args )
		{
			if( arg.overlaps(offset) )
			{
				return arg.findNode(offset);
			}
		}
		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(target.toString());
		sb.append('(');
		for( int i=0; i<args.length; i++ )
		{
			if( i > 0 )
				sb.append(", ");
			
			sb.append( args[i].toString() );
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		Type targetType = target.getType();
		if( targetType instanceof FunctionDecl )
		{
			target.emitBytecode(mv);
			
			// if we are calling a function within the same type as us,
			//
			FunctionDecl targetFunction = (FunctionDecl) targetType;
			
			if( thisType != null )
			{
				if( targetFunction.getParentType() == thisType.getType()
					&& !targetFunction.isStatic() )
				{
					mv.visitIntInsn( Opcodes.ALOAD, 0 );
				}
				else
				{
					System.out.println("static or not the same type, thisType: " + thisType);
				}
			}
			
			// push params
			for( Expression arg : args )
			{
				arg.emitBytecode(mv);
			}
			
			emitInvocation( (FunctionDecl)target.getType(), mv);
			
			// FIXME: if the method returns a value, but the parent
			//        AST node of this Invocation doesn't "do" anything
			//        (store it, invoke with it, etc... something that would
			//        pop it off the stack), then we need to POP it
			//        the return value so the stack isn't messed up
		}
		else if( targetType instanceof TypeDecl )
		{
			String targetTypeName = ClassName.toJavaFriendly( targetType.getFullName() );
			mv.visitTypeInsn(Opcodes.NEW, targetTypeName);
			mv.visitInsn(Opcodes.DUP);
			
			// necessary?
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, targetTypeName, "<init>", "()V");
			
			// PUTFIELD 0,1,...,n
		}
		else if( targetType instanceof FunctionSignature )
		{
			target.emitBytecode(mv);
			
			// push params
			for( Expression arg : args )
			{
				arg.emitBytecode(mv);
			}
			
			emitDynamicInvocation( (FunctionSignature) targetType, mv );
		}
	}
	
	public static void emitDynamicInvocation(FunctionSignature sig, MethodVisitor mv)
	{
		MethodType mt = MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class,
				String.class // target typeName
		);
		
		Handle handle = new Handle(	Opcodes.H_INVOKESTATIC,
				"kale/runtime/Bootstrap",
				"bootstrap",
				mt.toMethodDescriptorString() );
		
		mv.visitInvokeDynamicInsn(	sig.getName(),
									sig.toDescriptor(),
									handle,
									sig.getQualifiedPath() );
	}
	
	public static void emitInvocation(FunctionDecl targetType, MethodVisitor mv)
	{
		if( targetType == null )
		{
			throw new IllegalArgumentException("targetType cannot be null");
		}
		
		Type parentType = targetType.getParentType();
		if( parentType != null && parentType instanceof InterfaceDecl )
		{
			
		}
		else
		{
			int invokeOpcode = Opcodes.INVOKEVIRTUAL;
			
			if( targetType.isStatic() )
			{
				invokeOpcode = Opcodes.INVOKESTATIC;
			}
			
			
			
			// if target is not an interface, just do an invokevirtual
			mv.visitMethodInsn(	invokeOpcode,
								ClassName.toJavaFriendly( parentType.getFullName() ),
								ClassName.toJavaFriendly( targetType.getName() ),
								targetType.toDescriptor() );
		}
		
	}
	
	
}
