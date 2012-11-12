package kale.codegen;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;

public class JarWriterHandler implements ClassHandler
{
	private final JarOutputStream jos;
	
	public JarWriterHandler( OutputStream os ) throws IOException
	{
		jos = new JarOutputStream( new BufferedOutputStream( os ) );
	}
	
	@Override
	public void handle(String className, byte[] bytecode)
	{
		// TODO need to emit main(args String[]) method
		//      that calls whatever main() method is declared in Kale,
		//      since Kale can't specify array types
		//jos.
		//jos.putNextEntry(ze)
	}
}
