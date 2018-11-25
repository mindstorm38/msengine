package msengine.common.osf.serializer;

import msengine.common.osf.OSFNode;

public interface OSFTypeAdapter<T> {
	
	OSFNode serialize(T obj, OSFSerializationContext context);
	
	T deserialize(OSFNode node, OSFDeserializationContext context);
	
}
