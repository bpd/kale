package kale.runtime;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

public class Bootstrap
{
	/**
	 * Maximum depth of an inline cache before it is considered megamorphic
	 */
	public static final int MAX_CACHE_DEPTH = 5;
	
	static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	
	static class InterfaceCallSite extends MutableCallSite
	{
		final Class<?> ifc;
		
		final String methodName;
		
		final MethodType methodType;
		
		MethodHandle rootHandle = null;
		
		/**
		 * depth of the cache
		 * -1 if this call site is megamorphic
		 */
		int cacheDepth = 0;

		public InterfaceCallSite(	Class<?> ifc,
									String methodName,
									MethodType methodType )
		{
			super(methodType);
			
			this.ifc = ifc;
			this.methodName = methodName;
			this.methodType = methodType;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append( methodName ).append(' ').append( methodType );
			return sb.toString();
		}
		
		public static Object call(	InterfaceCallSite ics,
									Object o,
									Object[] args )
			throws Throwable
		{
			final Class<?> receiverClass = o.getClass();
			
			// drop the 'this' (locals[0]) parameter from the signature
			// since that is implicit in a virtual method
			MethodHandle handle = lookup.findVirtual(
									receiverClass,
									ics.methodName,
									ics.methodType.dropParameterTypes(0, 1) );
			
			// cache receiver type as long as the CallSite
			// is not megamorphic
			if( ics.cacheDepth != -1 )
			{
				// figure out what kind of an upgrade we're doing
				
				if( ics.cacheDepth < MAX_CACHE_DEPTH )
				{
					// upgrade, from either uninitialized or mono/polymorphic,
					// add class to polymorphic tree
					MethodHandle test = lookup.findVirtual(
						Class.class,
						"isInstance",
						MethodType.methodType(boolean.class, Object.class)
					)
					.bindTo(receiverClass);
					
					ics.cacheDepth++;
					
					// the new receiver type becomes the root
					// of the cache tree
					ics.setTarget( MethodHandles.guardWithTest(
						test,
						handle.asType(ics.getTarget().type()),
						ics.getTarget()
					) );
				}
				else
				{
					// call site has become megamorphic.
					// don't use a cache tree anymore, set cacheDepth
					// to indicate megamorphic call site
					ics.setTarget(ics.rootHandle);
					ics.cacheDepth = -1;
					
					// TODO: since rootHandle points to this method,
					//       every megmorphic invocation pays for
					//       the branch above with ics.cacheDepth != -1
					//       really another static method needs to be
					//       created that just does the invocation
					//       without the upgrade logic
				}
			}
			
			// cache is now setup for next invocation, but we still need
			// to handle this invocation, so spread arguments array
			// over resolved receiver method and invoke
			
			handle = MethodHandles
						.insertArguments(handle, 0, o)
						.asSpreader(	Object[].class,
										ics.methodType.parameterCount() - 1 );

			return handle.invoke(args);
		}
		
	}
	
	private static final MethodHandle callHandle = resolveCallHandle();
	
	public static MethodHandle resolveCallHandle()
	{
		try
		{
			return lookup.findStatic(
				InterfaceCallSite.class,
				"call",
				MethodType.methodType(	Object.class,				// return type
										InterfaceCallSite.class,	// interface method being invoked
										Object.class,				// receiver
										Object[].class)				// args
										);
		}
		catch(Throwable e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	public static CallSite bootstrap(	MethodHandles.Lookup caller,
										String name,
										MethodType type,
										String targetType )
		throws	NoSuchMethodException,
				IllegalAccessException,
				ClassNotFoundException
	{
		// lookup the target type within the ClassLoader of the
		// calling class, otherwise this bootstrap class may not
		// have visibility (if loaded through AnonymousClassLoader, etc)
		Class<?> targetClass = Class.forName(	
								targetType,
								true,
								caller.lookupClass().getClassLoader());
		
		InterfaceCallSite ics = new InterfaceCallSite( targetClass, name, type );

		// set the interface class, method name, and method type
		// as the first arguments of the handle, then have
		// it collect whatever arguments are sent at runtime
		// into a trailing Object[]
	
		ics.rootHandle = MethodHandles
							.insertArguments( callHandle, 0, ics )
							.asVarargsCollector( Object[].class )
							.asType(type);
		
		ics.setTarget( ics.rootHandle );
		
		return ics;
	}
}
