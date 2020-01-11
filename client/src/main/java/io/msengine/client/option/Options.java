package io.msengine.client.option;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.lwjgl.glfw.GLFW;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.registry.Registry;

import static io.msengine.common.util.GameLogger.LOGGER;

public class Options {
	
	// Singleton \\
	
	public static Options INSTANCE = null;
	
	// Constants \\
	
	public static final OptionBoolean FULLSCREEN		= new OptionBoolean( "fullscreen", false );
	public static final OptionKey TOGGLE_FULLSCREEN		= new OptionKey( "toggle_fullscreen", GLFW.GLFW_KEY_F11 );
	public static final OptionBoolean VSYNC				= new OptionBoolean( "vsync", false );
	public static final OptionKey TAKE_SCREENSHOT		= new OptionKey( "take_screenshot", GLFW.GLFW_KEY_F2 );
	
	// Class \\
	
	private final Gson gson;
	
	private final Registry<Class<? extends Option>, OptionTypeAdapter<?>> typeAdapters;
	
	private final File file;
	private final Set<Option> options;
	
	public Options(File optionsFile) {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( Options.class );
		INSTANCE = this;
		
		this.typeAdapters = new Registry<>();
		
		this.registerDefaultTypeAdapters();
		
		this.gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
		
		// this.file = new File( Game.getInstance().getAppData(), "options.json" );
		
		this.file = optionsFile;
		
		this.options = new HashSet<>();
		
		this.addOption( FULLSCREEN );
		this.addOption( TOGGLE_FULLSCREEN );
		this.addOption( VSYNC );
		this.addOption( TAKE_SCREENSHOT );
		 
	}
	
	private void registerDefaultTypeAdapters() {
		
		this.registerTypeAdapter( OptionKey.class, new OptionKey.Serializer() );
		this.registerTypeAdapter( OptionRange.class, new OptionRange.Serializer() );
		this.registerTypeAdapter( OptionBoolean.class, new OptionBoolean.Serializer() );
		
	}
	
	public File getFile() {
		return this.file;
	}
	
	public Set<Option> getOptions() {
		return Collections.unmodifiableSet( this.options );
	}
	
	public void addOption(Option option) {
		this.options.add( option );
	}
	
	public Option getOption(String identifier) {
		for ( Option option : this.options )
			if ( option.getIdentifier().equals( identifier ) )
				return option;
		return null;
	}
	
	public <T extends Option> void registerTypeAdapter(Class<T> clazz, OptionTypeAdapter<T> adapter) {
		this.typeAdapters.register( clazz, adapter );
	}
	
	public <T extends Option> OptionTypeAdapter<? extends Option> getTypeAdapter(Class<T> clazz) {
		return this.typeAdapters.get( clazz );
	}
	
	public OptionTypeAdapter<? extends Option> getTypeAdapter(Option option) {
		return this.getTypeAdapter( option.getClass() );
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public void save(boolean log) throws IOException {
		
		if ( log ) LOGGER.info("Saving options ...");
		
		JsonObject json = new JsonObject();
		
		for ( Option option : this.options ) {
			
			Class<? extends Option> clazz = option.getClass();
			
			if ( clazz.isAnonymousClass() ) {
				
				try {
					clazz = (Class<? extends Option>) clazz.getSuperclass();
				} catch (ClassCastException e) {}
				
			}
			
			OptionTypeAdapter<Option> adapter = (OptionTypeAdapter<Option>) this.getTypeAdapter( clazz );
			
			if ( adapter == null ) {
				
				LOGGER.warning( "Saving : Unable to find an adapter for the option '" + option.getIdentifier() + "' with the class '" + clazz.toString() + "'" );
				continue;
				
			}
			
			try {
				
				json.add( option.getIdentifier(), adapter.write( option ) );
				
			} catch (Exception e) {
				
				LOGGER.log( Level.WARNING, "Saving : Error while writing option '" + option.getIdentifier() + "' with the adapter '" + clazz.toString() + "'", e );
				continue;
				
			}
			
		}
		
		if ( !this.file.isFile() ) this.file.createNewFile();
		
		try ( JsonWriter writer = this.gson.newJsonWriter( new FileWriter( this.file ) ) ) { 
			
			writer.setIndent("\t");
			this.gson.toJson( json, writer );
			
		}
		
		if ( log ) LOGGER.info("Options saved");
		
	}
	
	public void save() throws IOException {
		this.save( true );
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public void load() throws IOException {
		
		LOGGER.info("Loading options ...");
		
		if ( !this.file.isFile() ) this.file.createNewFile();
		
		try ( JsonReader reader = this.gson.newJsonReader( new FileReader( this.file ) ) ) {
			
			reader.setLenient( true );
			
			JsonObject json = this.gson.fromJson( reader, JsonObject.class );
			if ( json == null ) json = new JsonObject();
			
			for ( Option option : this.options ) {
				
				Class<? extends Option> clazz = option.getClass();
				
				if ( clazz.isAnonymousClass() ) {
					
					try {
						clazz = (Class<? extends Option>) clazz.getSuperclass();
					} catch (ClassCastException e) {}
					
				}
				
				OptionTypeAdapter<Option> adapter = (OptionTypeAdapter<Option>) this.getTypeAdapter( clazz );
				
				if ( adapter == null ) {
					
					LOGGER.warning( "Loading : Unable to find an adapter for the option '" + option.getIdentifier() + "' with the class '" + clazz.toString() + "'" );
					continue;
					
				}
				
				if ( json.has( option.getIdentifier() ) ) {
					
					try {
						
						adapter.read( json.get( option.getIdentifier() ), option );
						
					} catch (Exception e) {
						
						LOGGER.log( Level.WARNING, "Loading : Error while reading option '" + option.getIdentifier() + "' with the adapter '" + clazz.toString() + "'", e );
						continue;
						
					}
					
				}
				
			}
			
		}
		
		this.save( false );
		
		LOGGER.info("Options loaded");
		
	}
	
}
