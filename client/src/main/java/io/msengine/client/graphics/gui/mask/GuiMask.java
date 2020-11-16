package io.msengine.client.graphics.gui.mask;

import io.msengine.client.graphics.gui.GuiManager;
import io.msengine.client.graphics.gui.GuiObject;
import io.msengine.client.graphics.gui.GuiSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;

public abstract class GuiMask extends GuiObject {
	
	private MaskTracker maskTracker;
	
	@Override
	protected void init() {
		this.maskTracker = this.getManager().acquireSingleton(MaskTrackerSingleton.INSTANCE);
		this.initMask();
	}
	
	@Override
	protected void stop() {
		this.stopMask();
		this.getManager().releaseSingleton(MaskTrackerSingleton.INSTANCE);
		this.maskTracker = null;
	}
	
	@Override
	protected void render(float alpha) { }
	
	@Override
	protected void update() { }
	
	protected abstract void initMask();
	protected abstract void stopMask();
	protected abstract void draw();
	
	@Override
	protected boolean updateCursorOver(float x, float y) {
		super.updateCursorOver(x, y);
		// Do not block the mouse for neighbors behind the mask.
		return false;
	}
	
	/**
	 * Use this mask, the returned tracker is a manager singleton and implement {@link AutoCloseable},
	 * so this method can be used with <code>try-with-resource</code> block like :
	 * <code><pre>try (GuiMask.MaskTracker ignored = maskObj.mask()) { ... }</pre></code>
	 * @return The mask tracker.
	 */
	public MaskTracker mask() {
		return this.maskTracker.start(this);
	}
	
	/**
	 * Use this mask in a consumer and close it automatically after.
	 * @param consumer The code block where the mask is applied.
	 */
	public void mask(Consumer<MaskTracker> consumer) {
		consumer.accept(this.maskTracker.start(this));
		this.maskTracker.close();
	}
	
	public static class MaskTracker implements AutoCloseable {
		
		private final List<GuiMask> masks = new ArrayList<>();
		
		private static void preStencilDraw() {
			glStencilFunc(GL_ALWAYS, 1, 0xFF);
			glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
			glColorMask(false, false, false, false);
		}
		
		private static void postStencilDraw(int size) {
			glStencilMask(0);
			glStencilFunc(GL_EQUAL, size, 0xFF);
			glColorMask(true, true, true, true);
		}
		
		public MaskTracker start(GuiMask mask) {
			
			this.masks.add(0, mask);
			int size = this.masks.size();
			
			if (size == 1) {
				glEnable(GL_STENCIL_TEST);
				glClearStencil(0);
				glStencilMask(0xFF);
				glClear(GL_STENCIL_BUFFER_BIT);
			} else { // > 1
				glStencilMask(0xFF);
			}
			
			preStencilDraw();
			mask.draw();
			postStencilDraw(size);
			
			// this.redraw();
			return this;
			
		}
		
		public void redraw() {
			
			glStencilMask(0xFF);
			glClear(GL_STENCIL_BUFFER_BIT);
			//glStencilFunc(GL_ALWAYS, 1, 0xFF);
			//glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
			//glColorMask(false, false, false, false);
			
			preStencilDraw();
			this.masks.forEach(GuiMask::draw);
			postStencilDraw(this.masks.size());
			
			//glStencilFunc(GL_EQUAL, this.masks.size(), 0xFF);
			//glColorMask(true, true, true, true);
		
		}
		
		@Override
		public void close() {
			if (!this.masks.isEmpty()) {
				this.masks.remove(0);
				if (this.masks.isEmpty()) {
					glDisable(GL_STENCIL_TEST);
				} else {
					this.redraw();
				}
			}
		}
		
		private void closeAll() {
			this.masks.clear();
			glDisable(GL_STENCIL_TEST);
		}
		
	}
	
	public static class MaskTrackerSingleton extends GuiSingleton<MaskTracker> {
		
		public static final MaskTrackerSingleton INSTANCE = new MaskTrackerSingleton();
		
		@Override
		public MaskTracker supply(GuiManager manager) {
			return new MaskTracker();
		}
		
		@Override
		public void release(MaskTracker tracker) {
			tracker.closeAll();
		}
		
	}
	
}
