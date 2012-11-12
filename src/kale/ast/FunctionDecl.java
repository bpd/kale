package kale.ast;

import java.util.List;

import kale.ReadOnlyScope;
import kale.Scope;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Class File Format Restrictions
 * ==============================
 * The JLS specifies that methods, fields, and local variables
 * are stored as "unqualified names", which can't contain the Unicode
 * code points corresponding to the ASCII characters:  '.' ';' '[' '/'
 *       
 * Method names must not contain '<' or '>'
 * ( except for the built-in '<init>' and '<clinit>' )
 *      
 * 
 * How this affects operator overloading
 * =====================================
 * These restrictions partially correspond to the restrictions on symbols,
 * except for '/' '<' and '>'.  These would be division, less than, and
 * greater than.  When these are encountered as symbols, they will be
 * converted to '#div' '#lt' and '#gt', respectively.
 * 
 * Reference:
 * - http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.2.2
 * 
 * @author Brian Diekelman
 *
 */
public class FunctionDecl extends Type
{
	Type parentType;
		
	final Id name;
	
	final Signature signature;
	
	final Expression[] statements;
	
	final Scope scope = new Scope();
	
	public FunctionDecl( Id name, Signature signature, Expression[] statements )
	{
		this.name = name;
		this.signature = signature;
		this.statements = statements;
		
		scope.set( name.id, this );
		
		// return type of this function is the declared (or inferred)
		// return type of the signature
		setType( this );
		
		addChild( name );
		addChild( signature );
		addChildren( statements );
	}
	
	public String getName()
	{
		return name.id;
	}
	
	public Signature getSignature()
	{
		return signature;
	}
	
	public Expression[] getStatements()
	{
		return statements;
	}
	
	public String toDescriptor()
	{
		return signature.toDescriptor();
	}
	
	public Type getParentType()
	{
		return parentType;
	}
	
	public void setParentType(Type parentType)
	{
		this.parentType = parentType;
	}
	
	public String getQualifiedPath()
	{
		return parentType == null ? "" : parentType.getFullName();
	}
	
	@Override
	public String getFullName()
	{
		return getName() + '.' + getQualifiedPath();
	}
	
	@Override
	public ReadOnlyScope getScope()
	{
		return scope;
	}
	
	public boolean isStatic()
	{
		return getSignature().isOperator()
				|| parentType == null
				|| (parentType != null && parentType.getName().equals("__functions"));
	}
	
	class ThisVar extends ASTNode implements LocalVariable
	{
		{
			setType( FunctionDecl.this.parentType );
		}
		
		@Override
		public int getLocalIndex()
		{
			return 0;
		}
	};
	
	@Override
	public void inferType(Scope scope)
	{
		signature.inferType(scope);
		
		// TODO set type?
	}
	
	public void inferBody(Scope scope)
	{
		scope = scope.newSubScope();
		
		// as long as we're not an operator (which implies static)
		// reserve the 'this' var
		// TODO support for static methods that aren't operators ('static' keyword?)
		if( !isStatic() )
		{
			ThisVar thisVar = new ThisVar();
			scope.set( "this", thisVar );
			
			// set aside local[0] for 'this'
			scope.newLocal();
		}
		
		// assign each parameter a local index
		for( ParamDecl param : signature.getParams() )
		{
			param.setLocalIndex( scope.newLocal() );
			
			scope.set( param.getName().id, param );
		}
		
		// only statements that return will result in a type that
		// isn't NONE
		for( Expression statement : statements )
		{
			statement.inferType(scope);
		}
		
		// TODO verify return type matches signature return type
		
		// TODO infer return type from return statements?
		
		// Note: walk children to collect ReturnStatement children
		
		List<ASTNode> returnStatements = collectChildren( ReturnStatement.class );
		
		if( returnStatements.size() == 0
			&& !signature.getReturnType().toString().equals("void") )
		{
			error("Function must return type " + signature.getReturnType());
		}
		
		// all return statement types have to agree
		// with the signature
		if( returnStatements.size() > 0 )
		{
			Type sigReturnType = signature.getReturnType().getType();
			
			boolean returnTypeError = false;
			
			for( ASTNode returnStatement : returnStatements )
			{
				Type stmtReturnType = returnStatement.getType();
				
				if( stmtReturnType == null )
				{
					returnStatement.error("Unable to determine return type");
				}
				else if( !returnStatement.getType().isEquivalent(sigReturnType) )
				{
					returnStatement.error("Return type " + stmtReturnType + " does not match signature: " + signature);
					if( !returnTypeError )
					{
						this.error("Bad return type in body: " + stmtReturnType);
						returnTypeError = true;
					}
				}
			}
			
		}
		
	}
	
	@Override
	public boolean isEquivalent(Type type)
	{
		// function names, parameters, and parameter types have to be equivalent
		// in order for the Function/FunctionSignature to be equivalent
		if( type instanceof FunctionDecl )
		{
			FunctionDecl other = (FunctionDecl)type;
			if( other.getName().equals(getName()) )
			{
				if( signaturesMatch( this.getSignature(), other.getSignature() ) )
				{
					return true;
				}
			}
		}
		else if( type instanceof FunctionSignature )
		{
			FunctionSignature other = (FunctionSignature)type;
			
			if( other.getName().equals(getName()) )
			{
				if( signaturesMatch( this.getSignature(), other.getSignature() ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean signaturesMatch( Signature a, Signature b )
	{
		ParamDecl[] otherArgs = a.getParams();
		ParamDecl[] foundArgs = b.getParams();
		
		int expectedArgCount = otherArgs.length;
		int foundArgCount = foundArgs.length;
		
		if( expectedArgCount == foundArgCount )
		{
			// number of arguments match, now go through each one and verify
			// type equivalence
			for( int i=0; i < foundArgCount; i++ )
			{
				ParamDecl otherArg = otherArgs[i];
				ParamDecl foundArg = foundArgs[i];
				
				if( !otherArg.getType().isEquivalent( foundArg.getType() ) ) 
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(signature);
		sb.append("\n{\n");
		for( Expression statement : statements )
		{
			sb.append("  ").append(statement);
		}
		sb.append("\n}\n");
		return sb.toString();
	}
	
	public void emitBytecode(MethodVisitor mv)
	{
		mv.visitCode();
		
		for( Expression statement : statements )
		{
			statement.emitBytecode(mv);
			
			if( statement instanceof Invocation )
			{
				if( !((Invocation)statement).getType().toDescriptor().equals("V") )
				{
					mv.visitInsn(Opcodes.POP);
				}
			}
		}
		
		if( statements.length == 0
			|| !(statements[ statements.length - 1 ] instanceof ReturnStatement) )
		{
			// if we can't prove the function returns,
			// emit a RETURN opcode at the end of the function
			// to guarantee control returns to the caller
			mv.visitInsn(Opcodes.RETURN);
		}
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end code
	}
}
