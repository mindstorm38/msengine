package io.msengine.common.asset.metadata;

public interface Metadata {
	
	<T> T getMetadataSection(MetadataSection<T> section, boolean refresh) throws MetadataParseException;
	
	default <T> T getMetadataSection(MetadataSection<T> section) throws MetadataParseException {
		return this.getMetadataSection(section, false);
	}
	
	Metadata NO_META = new Metadata() {
		
		@Override
		public <T> T getMetadataSection(MetadataSection<T> section, boolean refresh) {
			return null;
		}
		
	};
	
}
