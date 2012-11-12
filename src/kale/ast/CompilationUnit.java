package kale.ast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kale.Scope;
import kale.ast.builtin.BooleanType;
import kale.ast.builtin.IntType;
import kale.ast.builtin.Println;
import kale.ast.builtin.StringType;
import kale.ast.builtin.VoidType;
import kale.codegen.ClassLoadingHandler;
import kale.codegen.OpenClassLoader;

public class CompilationUnit extends ASTNode
{
	final PackageDecl packageDecl;
	
	boolean typeChecked = false;
	
	/**
	 * Even though we were able to build an AST, that doesn't
	 * necessary meaning we didn't have to skip over some
	 * bad tokens to get there (error recovery, etc)...
	 * so 'valid' indicates whether this AST exists despite
	 * bad underlying source, or if it can enter the
	 * bytecode emission phase
	 */
	boolean valid = true;
	
	public CompilationUnit( PackageDecl packageDecl )
	{
		this.packageDecl = packageDecl;
		
		addChild( packageDecl );
	}
	
	public PackageDecl getPackageDecl()
	{
		return packageDecl;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void setValid( boolean valid )
	{
		this.valid = valid;
	}
	
	@Override
	public boolean hasErrors()
	{
		return !valid || super.hasErrors();
	}
	
	@Override
	public String toString()
	{
		return this.packageDecl.toString();
	}
	
	@Override
	public void inferType(Scope scope)
	{
		packageDecl.inferType(scope);
		
		typeChecked = true;
	}
	
	public static Scope newRootScope()
	{
		Scope scope = new Scope();
		TypeDecl intType = new IntType();
		TypeDecl voidType = new VoidType();
		TypeDecl stringType = new StringType();
		TypeDecl booleanType = new BooleanType();
		
		scope.set("int", intType);
		scope.set("void", voidType);
		scope.set("string", stringType);
		scope.set("boolean", booleanType);
		
		// types can have references to themselves and other built-ins,
		// so infer to resolve those types
		intType.inferType(scope);
		stringType.inferType(scope);
		booleanType.inferType(scope);
		
		// built-in functions
		
		Println println = new Println();
		
		TypeDecl runtime = new TypeDecl(
				new Id("__functions"),
				new FieldDecl[0],
				new FunctionDecl[]
		{
			println
		});
		runtime.setPackageName("kl");
		
		println.setParentType(runtime);
		scope.set("println", println);
		scope.set("#runtime", runtime);
		println.inferType(scope);
		
		return scope;
	}
	
	public Class<?> compile()
	{
		Scope scope = newRootScope();
		
		if( !typeChecked )
		{
			inferType( scope );
		}
		
		if( hasErrors() )
		{
			for( String error : getErrors() )
			{
				System.err.println( error );
			}
			throw new RuntimeException(
					"Compilation errors occured, cannot continue");
		}
		
		//AnonymousClassLoader classLoader = new AnonymousClassLoader();
		OpenClassLoader classLoader = new OpenClassLoader();
		
		// gen built-in types (void omitted)
		classLoader.loadClass( ((TypeDecl)scope.get("int")).emitBytecode() );
		classLoader.loadClass( ((TypeDecl)scope.get("string")).emitBytecode() );
		classLoader.loadClass( ((TypeDecl)scope.get("boolean")).emitBytecode() );
		classLoader.loadClass( ((TypeDecl)scope.get("#runtime")).emitBytecode() );
		
		packageDecl.emitBytecode( new ClassLoadingHandler(classLoader) );
		
		// emitting bytecode to the ClassLoadingHandler should load
		// all the classes, so we should be able to look up the function class
		
		try
		{
			return Class.forName(	packageDecl.getFunctionClassName(),
									true,
									classLoader );
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void test()
	{
		Class<?> clazz = compile();
		
		List<Throwable> errors = new ArrayList<Throwable>();
		
		for( Method method : clazz.getDeclaredMethods() )
		{
			if( method.getName().startsWith("test") )
			{
				try
				{
					System.out.print( method.getName() );
					
					Object o = method.invoke( null );
					if( o instanceof Boolean )
					{
						if( ((Boolean)o).booleanValue() == true )
						{
							System.out.println( " PASS" );
							continue;
						}
					}
					throw new RuntimeException(
							"Test " + method.getName() + " failed (no Boolean return value or value was false)");
				}
				catch(Throwable e)
				{
					System.out.println( " FAIL" );
					errors.add(e);
					throw new RuntimeException(e);
				}
			}
		}
		
		for( Throwable error : errors )
		{
			error.printStackTrace();
		}
	}

	public Object execute()
	{
		try
		{
			Class<?> clazz = compile();
			return execute(clazz);
		}
		catch(Throwable e)
		{
		throw new RuntimeException(e);
		}
	}
	
	public static Object execute( Class<?> clazz )
	{
		try
		{
			Object result = clazz.getDeclaredMethod("main").invoke( null );

			return result;
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	
}
