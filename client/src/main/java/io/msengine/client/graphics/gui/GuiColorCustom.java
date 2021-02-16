package io.msengine.client.graphics.gui;

import io.msengine.common.util.Color;

import java.util.Objects;

public class GuiColorCustom extends GuiColorBase {
	
	private final CornerColorSupplier supplier;
	
	public GuiColorCustom(CornerColorSupplier supplier) {
		this.supplier = Objects.requireNonNull(supplier);
	}
	
	@Override
	public Color getCornerColor(int corner) {
		return this.supplier.get(corner);
	}
	
	@FunctionalInterface
	public interface CornerColorSupplier {
		Color get(int corner);
	}
	
}
