package kale.runtime.test;

public class InstanceOfPerformance
{
	static class Person
	{
		
	}
	
	static class Dog
	{
		
	}
	
	public static void testInstanceOf(Object[] receivers)
	{
		long start, stop;
		
		start = System.currentTimeMillis();
		int count=0;
		
		for(int i=0; i<100000; i++)
		{
			for(int j=0; j<receivers.length; j++)
			{
				Object receiver = receivers[j];
				
				if( receiver instanceof Person )
				{
					count++;
				}
				else if( receiver instanceof Dog )
				{
					count++;
				}
			}
		}
		stop = System.currentTimeMillis();
		
		System.out.println(count);
		
		System.out.println("test instanceof took " + (stop-start) + "ms");
	}
	
	public static void testVirtualInstanceOf(Object[] receivers)
	{
		long start, stop;
		
		start = System.currentTimeMillis();
		int count=0;
		
		for(int i=0; i<100000; i++)
		{
			for(int j=0; j<receivers.length; j++)
			{
				Object receiver = receivers[j];
				
				if( Person.class.isInstance(receiver) )
				{
					count++;
				}
				else if( Dog.class.isInstance(receiver) )
				{
					count++;
				}
			}
		}
		stop = System.currentTimeMillis();
		
		System.out.println(count);
		
		System.out.println("test instanceof took " + (stop-start) + "ms");
	}
	
	public static void testVirtualInstanceOf2(Object[] receivers)
	{
		long start, stop;
		
		start = System.currentTimeMillis();
		int count=0;
		
		for(int i=0; i<100000; i++)
		{
			for(int j=0; j<receivers.length; j++)
			{
				Object receiver = receivers[j];
				
				if( Person.class.isInstance(receiver) )
				{
					count++;
				}
				else if( Dog.class.isInstance(receiver) )
				{
					count++;
				}
			}
		}
		stop = System.currentTimeMillis();
		
		System.out.println(count);
		
		System.out.println("test instanceof took " + (stop-start) + "ms");
	}
	
	public static void testStringEquals(Object[] receivers)
	{
		long start, stop;
		
		start = System.currentTimeMillis();
		int count=0;
		
		for(int i=0; i<100000; i++)
		{
			for(int j=0; j<receivers.length;j++)
			{
				Object receiver = receivers[j];
				String className = receiver.getClass().getSimpleName();
				if( className.equals("Person") )
				{
					count++;
				}
				else if( className.equals("Dog") )
				{
					count++;
				}
			}
		}
		stop = System.currentTimeMillis();
		
		System.out.println(count);
		
		System.out.println("test string equals took " + (stop-start) + "ms");
	}
	
	public static void main(String[] args)
	{
		Object[] receivers = new Object[]{
				new Person(), new Dog(), new Person()
		};
		
		testInstanceOf( receivers );
		testVirtualInstanceOf( receivers );
		testStringEquals( receivers );
	}
	
	
	
}
