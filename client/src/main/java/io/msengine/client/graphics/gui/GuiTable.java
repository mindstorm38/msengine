package io.msengine.client.graphics.gui;

import java.util.List;

/**
 * An auto sized table.
 */
public class GuiTable extends GuiParent {
	
	private static GuiObject EMPTY_CELL;
	
	private int columns, rows;
	
	public void setDimensions(int columns, int rows) {
		this.setColumns(columns);
		this.setRows(rows);
	}
	
	public void setColumns(int columns) {
		if (this.columns != columns) {
			this.columns = columns;
			this.updateCells();
		}
	}
	
	public void setRows(int rows) {
		if (this.rows != rows) {
			this.rows = rows;
			this.updateCells();
		}
	}
	
	private void updateCells() {
		
		int targetCount = this.columns * this.rows;
		int currentCount = this.children.size();
		
		if (currentCount < targetCount) {
			for (; currentCount < targetCount; ++currentCount) {
				this.addChild(this.new Cell());
			}
		} else if (currentCount > targetCount) {
			int idxToRemove = targetCount;
			for (; targetCount < currentCount; ++targetCount) {
				this.removeChild(idxToRemove);
			}
		}
		
	}
	
	@Override
	protected void updateChildrenXOffset() {
		
		float[] offsets = new float[this.columns - 1];
		
		List<GuiObject> children = this.children;
		GuiObject child;
		
		for (int i = 0, size = children.size(); i < size; ++i) {
			
			int x = i / this.columns;
			child = children.get(i);
			
			if (x == 0) {
				child.setXPos(0);
			} else {
				child.setXPos(offsets[x - 1]);
			}
			
			float width = child.getRealWidth();
			
			
		}
		
	}
	
	@Override
	protected void updateChildrenYOffset() {
		List<GuiObject> children = this.children;
		GuiObject child;
		for (int i = 0, size = children.size(); i < size; ++i) {
			child = children.get(i);
			
		}
	}
	
	private class Cell extends GuiParent {
	
	}
	
}
