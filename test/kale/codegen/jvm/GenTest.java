package kale.codegen.jvm;

import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class GenTest
{
	public static class Person
	{
		String name;
		int age;
		boolean cond;
		
		public static void sayHello( Person p )
		{
			System.out.println(p.name);
		}
		
		public void emitTryCatch( Person p )
		{
			try
			{
				if( p.age < 30 )
				{
					throw new IllegalArgumentException("");
				}
				
				if( p.cond == true )
				{
					throw new IllegalStateException("true");
				}
			}
			catch(IllegalArgumentException iae )
			{
				
			}
			catch(RuntimeException e)
			{
				throw new IllegalStateException("");
			}
		}
		
		public void emitWhileLoop(  )
		{
			int i = 8;
			while( i > 8 )
			{
				i--;
			}
		}
		
		public int emitIfStatement(  )
		{
			int i = 8;
			if( i < 7 )
			{
				return 2;
			}
			else if( i > 8 )
			{
				return 3;
			}
			else
			{
				return 4;
			}
		}
		
		public static boolean emitBooleanStatement( boolean a, boolean b )
		{
			return a || b;
		}
		
		public static int emitStoreLocal()
		{
			int a = 2;
			return a;
		}
		
		public String emitToString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(2);
			sb.append(new Object());
			return sb.toString();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		ClassReader cr = new ClassReader("kale/codegen/jvm/GenTest$Person");
		
		int flags = ClassReader.SKIP_DEBUG;

		cr.accept(new TraceClassVisitor(null,
            new ASMifier(),
            new PrintWriter(System.out)), flags);
		
		cr.accept(new TraceClassVisitor(null,
	            new Textifier(),
	            new PrintWriter(System.out)), flags);
	}
}
