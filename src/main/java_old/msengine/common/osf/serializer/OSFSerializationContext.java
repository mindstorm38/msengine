package msengine.common.osf.serializer;

import msengine.common.osf.OSFNode;

public interface OSFSerializationContext {
	
	<T extends Object> OSFNode serialize(T obj);
	
}
