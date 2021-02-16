package io.msengine.client.graphics.gui.render;

import io.msengine.client.EngineClient;
import io.msengine.client.graphics.buffer.BufferArray;
import io.msengine.client.graphics.buffer.IndexedBufferArray;
import io.msengine.client.graphics.util.DataType;
import io.msengine.common.asset.Asset;

public class GuiProgramMask extends GuiStdProgramBase {
	
	protected static final Asset VERTEX_SHADER = EngineClient.ASSETS.getAsset("mse/shaders/gui_mask.vsh");
	protected static final Asset FRAGMENT_SHADER = EngineClient.ASSETS.getAsset("mse/shaders/gui_mask.fsh");
	
	public static final GuiProgramType<GuiProgramMask> TYPE = new GuiProgramType<>("mask", GuiProgramMask::new);
	
	public GuiProgramMask() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}
	
	public IndexedBufferArray createBuffer() {
		return BufferArray.newBuilder(IndexedBufferArray::new)
				.newBuffer().withAttrib(this.attribPosition, DataType.FLOAT, 2).build()
				.withVertexAttrib(this.attribPosition, true)
				.build();
	}
	
}
