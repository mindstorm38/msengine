package io.msengine.client.graphics.gui.render;

import io.msengine.client.graphics.shader.ShaderProgram;

import java.util.Objects;
import java.util.function.Supplier;

public class GuiProgramType<P extends ShaderProgram> {
	
	private final String name;
	private final Supplier<P> supplier;
	
	public GuiProgramType(String name, Supplier<P> supplier) {
		this.name = Objects.requireNonNull(name);
		this.supplier = Objects.requireNonNull(supplier);
	}
	
	public String getName() {
		return this.name;
	}
	
	public P supplyProgram() {
		return this.supplier.get();
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<'" + this.name + "'>";
	}
	
}
