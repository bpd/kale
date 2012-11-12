package kale.codegen.jvm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

public class FieldPointer
{
	static class A
	{
		B b;
	}
	
	static class B
	{
		C c;
		int value;
	}
	
	static class C
	{
		int value;
	}

	/**
	 * Given arrays of field names and corresponding field types,
	 * build a sequence of nested MethodHandles that each return
	 * the result of their respective getfield operations.
	 * 
	 * @param target
	 * @param path - the path of the field lookup, for instance 'nested.value'
	 * @return
	 */
	public static MethodHandle buildGetter( Class<?> target, String[] fieldNames, Class<?>[] fieldTypes )
		throws Throwable
	{
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		
		if( fieldNames.length != fieldTypes.length )
		{
			throw new IllegalArgumentException("fieldNames != fieldTypes");
		}
		
		if( fieldNames.length == 0 )
		{
			throw new IllegalArgumentException("length must be greater than zero");
		}
		
		MethodHandle handle = lookup.findGetter( target, fieldNames[0], fieldTypes[0] );
		
		for( int i=1; i < fieldNames.length; i++ )
		{
			handle = MethodHandles.filterReturnValue(
				handle,
				lookup.findGetter( fieldTypes[ i - 1 ], fieldNames[i], fieldTypes[i] )
			);
		}
		
		return handle;
	}
	
	public static MethodHandle buildSetter( Class<?> target, String[] fieldNames, Class<?>[] fieldTypes )
			throws Throwable
		{
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			
			if( fieldNames.length != fieldTypes.length )
			{
				throw new IllegalArgumentException("fieldNames != fieldTypes");
			}
			
			if( fieldNames.length == 0 )
			{
				throw new IllegalArgumentException("length must be greater than zero");
			}
			
			if( fieldNames.length == 1 )
			{
				return lookup.findSetter( target, fieldNames[0], fieldTypes[0]);
			}
			else
			{
				int setterIndex = fieldNames.length - 1;
				
				// the only 'setter' we really need is the last field,
				// since intermediary elements before the set are getters
				MethodHandle handle = buildGetter( 
						target,
						Arrays.copyOfRange(fieldNames, 0, setterIndex ),
						Arrays.copyOfRange(fieldTypes, 0, setterIndex )
				);
				
				MethodHandle setter = lookup.findSetter(
						fieldTypes[setterIndex - 1],
						fieldNames[setterIndex],
						fieldTypes[setterIndex]
				);
				
				return MethodHandles.filterReturnValue(
					handle,
					setter
				);
			}
		}
	
	public static void main(String[] args) throws Throwable
	{
		B b = new B();
		
		C c = new C();
		c.value = 7;
		
		A a = new A();
		a.b = b;
		b.c = c;
		
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		
//		System.out.println( "combiner: " + nestedHandle.toString() );
//		System.out.println( (Nested)nestedHandle.invokeExact(t) );
		
//		System.out.println( "target: " + valueHandle.toString() );
//		System.out.println( (int)valueHandle.invokeExact(n) );
		
		// pointer dereference could call into bootstrap method
		// that returns a CallSite powered by a filtered 
		
		//
		// Getters
		//
		{
		MethodHandle filterHandle = MethodHandles.filterReturnValue(
				lookup.findGetter( A.class, "b", B.class ),	// target
				MethodHandles.filterReturnValue(
						lookup.findGetter( B.class, "c", C.class ),	// target
						lookup.findGetter( C.class, "value", int.class )		// filter
				)
		);
		System.out.println( filterHandle.toString() );
		System.out.println("filter: " + (int)filterHandle.invokeExact( a ) );
		}
		
		{
		MethodHandle filterHandle = buildGetter( A.class, new String[]{ "b", "c", "value" }, new Class[]{ B.class, C.class, int.class } );
		System.out.println( filterHandle.toString() );
		System.out.println("filter: " + (int)filterHandle.invokeExact( a ) );
		}
		
		//
		// Setters
		//
		
		{
		MethodHandle filterHandle = buildSetter( A.class, new String[]{ "b", "c", "value" }, new Class[]{ B.class, C.class, int.class } );
		System.out.println( filterHandle.toString() );
		
		// call the setter
		filterHandle.invokeExact( a, 42 );
		
		// and check the resulting value
		System.out.println("filter: " + a.b.c.value );
		}
		
//		{
//			
//		
//			
//		MethodHandle filterHandle = MethodHandles.filterReturnValue(
//				lookup.findSetter( Type.class, "nested", Nested.class ),	// target
//				lookup.findSetter( Nested.class, "value", int.class )		// filter
//		);
//		System.out.println( filterHandle.toString() );
//		System.out.println("filter: " + (int)filterHandle.invokeExact( t ) );
//		}
		
	}
}
