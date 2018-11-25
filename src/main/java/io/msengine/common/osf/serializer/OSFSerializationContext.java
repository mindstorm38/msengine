package io.msengine.common.osf.serializer;

import io.msengine.common.osf.OSFNode;

/**
 * 
 * The OSF serialization context, implemented in OSF class
 * 
 * @author Mindstorm38
 *
 */
public interface OSFSerializationContext {
	
	/**
	 * Serialize an object to a node using a type adapter
	 * @param obj The object to serialize
	 * @param clazz The type adapter class
	 * @return The node of the serialized object
	 */
	<T extends Object> OSFNode serialize(T obj, Class<T> clazz);
	
	/**
	 * Serialize an object to a node using a type adapter
	 * @param obj The object to serialize
	 * @return The node of the serialized object
	 */
	<T extends Object> OSFNode serialize(T obj);
	
}
