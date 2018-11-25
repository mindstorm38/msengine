package msengine.common.osf.serializer;

import msengine.common.osf.OSFNode;

public interface OSFDeserializationContext {

	<T extends Object> T deserialize(OSFNode node, Class<T> clazz);
	
}
