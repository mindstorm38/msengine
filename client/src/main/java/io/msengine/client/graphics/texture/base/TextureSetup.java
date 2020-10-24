package io.msengine.client.graphics.texture.base;

import java.util.Objects;
import java.util.function.Consumer;

public class TextureSetup {

	public static final TextureSetup DEFAULT = new TextureSetup();
	
	private final int unit;
	private final boolean unbind;
	private final Consumer<Texture> configurator;

	public TextureSetup(int unit, boolean unbind, Consumer<Texture> configurator) {
		this.unit = unit;
		this.unbind = unbind;
		this.configurator = configurator;
	}
	
	public TextureSetup(boolean unbind, Consumer<Texture> configurator) {
		this(-1, unbind, configurator);
	}
	
	public TextureSetup(int unit, Consumer<Texture> configurator) {
		this(unit, true, configurator);
	}
	
	public TextureSetup(Consumer<Texture> configurator) {
		this(-1, true, configurator);
	}
	
	private TextureSetup() {
		this(-1, true, null);
	}
	
	// Mutate //
	
	public TextureSetup thenConfig(Consumer<Texture> configurator) {
		Objects.requireNonNull(configurator, "Extending configurator is null.");
		return new TextureSetup(this.unit, this.unbind, (this.configurator == null) ? configurator : this.configurator.andThen(configurator));
	}
	
	public TextureSetup withUnit(int unit) {
		return (this.unit == unit) ? this : new TextureSetup(unit, this.unbind, this.configurator);
	}
	
	public TextureSetup withNoUnit() {
		return this.withUnit(-1);
	}
	
	public TextureSetup withUnbind(boolean unbind) {
		return (this.unbind == unbind) ? this : new TextureSetup(this.unit, unbind, this.configurator);
	}
	
	// Apply //
	
	public void bind(Texture tex) {
		if (this.unit != -1) {
			tex.bind(this.unit);
		} else {
			tex.bind();
		}
	}
	
	public void unbind(Texture tex) {
		if (this.unbind) {
			tex.unbind();
		}
	}
	
	public void config(Texture tex) {
		if (this.configurator != null) {
			this.configurator.accept(tex);
		}
	}
	
}
