package io.msengine.client.option;

import com.google.gson.JsonElement;

@Deprecated
public interface OptionTypeAdapter<T extends Option> {
	
	JsonElement write(T obj) throws Exception;
	
	void read(JsonElement json, T obj) throws Exception;
	
}
