package kaygan;

/**
 * Represents an entity that accepts
 * bind requests and returns a binding
 * that may be invoked for a value
 * 
 */
public interface Bindable
{
	/**
	 * Takes something that needs to be bound,
	 *   and the scope that 
	 * 
	 * 
	 * For instance:
	 * 
	 *  z: 3
	 *  y: 5
	 * 
	 *  a: ([b c] b + c)
	 *
	 *  b: ([f] f 1 2)
	 *  
	 *  (a 1 2)
	 *  
	 *  (b a)
	 *  
	 *  c: [ y z ]
	 *  
	 *  When the Pair<b, Chain{f...}> is bound,
	 *    the chain bound to 'b' will have
	 *    bind() invoked on it with Symbol<f>
	 *    as the 'caller' argument (representing the 'f'
	 *    inside the function, not the argument 'f')
	 *    and the parent scope of that chain
	 *    (the implicit sequence of the environment)
	 *    as the 'scope' argument
	 *    
	 *  The chain bound to 'b', when bind() is invoked
	 *    with (Symbol<f>, Sequence<Implicit>),
	 *    will realize that it has a local symbol
	 *    of 'f' (the argument to the function)
	 *    and return the Bindable for that
	 *  
	 *  
	 *    
	 * 
	 * 
	 * @param caller
	 * @return
	 */
	Binding bind(Bindable parent);
}
