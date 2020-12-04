package io.msengine.client.gui;

import io.msengine.common.util.Color;

import java.util.Objects;
import java.util.function.Function;

@Deprecated
public class GuiColorCustom extends GuiColorBase {
	
	private final Function<Integer, Color> cornerColorSupplier;
	
	public GuiColorCustom(Function<Integer, Color> cornerColorSupplier) {
		this.cornerColorSupplier = Objects.requireNonNull(cornerColorSupplier);
	}
	
	@Override
	public Color getCornerColor(int corner) {
		return this.cornerColorSupplier.apply(corner);
	}
	
	@Override
	public void updateColors() {
		super.updateColors();
	}
	
}
