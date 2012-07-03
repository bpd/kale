package kaygan;

public interface Function
{
	// Scope getScope();
	
	/**
	 * 
	 * In a Sequence:
	 * 
	 *   a: [ 1 2 3 b c d ]
	 *   
	 *   a = sequence.bind(1).bind(2).bind(3).bind('b').bind('c').bind('d')
	 *   
	 *     the Sequence returns itself each with with the new Function added
	 * 
	 * In a Chain:
	 * 
	 *   f: ( a b c )
	 *   g: ([a b] a + b)
	 *   
	 *   f = chain.bind('a').bind('b').bind('c')
	 *   g = chain.bind( sequence.bind('a').bind('b') ).bind('a').bind('+').bind('b')
	 *   
	 *     the Chain returns the result of binding the 'left' to the 'right' Function
	 *       ( whatever is returned by left.bind(right) )
	 * 
	 * 
	 * @param f
	 * @return
	 */
	Function bind(Function f);
	
	Function eval();
}
