package io.msengine.common.osf.serializer;

import io.msengine.common.osf.OSF;
import io.msengine.common.osf.OSFNode;

public interface OSFTypeAdapter<T> {
	
	Class<T> initOSF(OSF osf);
	
	OSFNode serialize(T obj, OSFSerializationContext context);
	
	T deserialize(OSFNode node, OSFDeserializationContext context);
	
}
