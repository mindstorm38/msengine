package io.msengine.common.asset.metadata;

public class MetadataParseException extends RuntimeException {
	
	public MetadataParseException() {
		super();
	}
	
	public MetadataParseException(String message) {
		super(message);
	}
	
	public MetadataParseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MetadataParseException(Throwable cause) {
		super(cause);
	}
	
}
