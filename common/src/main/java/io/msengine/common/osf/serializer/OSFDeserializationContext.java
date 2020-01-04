package io.msengine.common.osf.serializer;

import io.msengine.common.osf.OSFNode;

/**
 * 
 * The OSF deserialization context
 * 
 * @author Mindstorm38
 *
 */
public interface OSFDeserializationContext {

	/**
	 * Deserialize a node using a type adapter
	 * @param node The node to deserialize
	 * @param clazz The class used to find the type adapter
	 * @return The deserialized object
	 */
	<T extends Object> T deserialize(OSFNode node, Class<T> clazz);
	
}
