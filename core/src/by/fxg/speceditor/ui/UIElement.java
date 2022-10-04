package by.fxg.speceditor.ui;

import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.Game;

abstract class UIElement {
	protected int x, y, width, height;
	
	public boolean isMouseOver(int x, int y, int width, int height) {
		return GDXUtil.isMouseInArea(x, y, width, height);
	}
	
	public boolean isMouseOver() {
		return this.isMouseOver(this.x, this.y, this.width, this.height);
	}
	
	public GInputProcessor getInput() {
		return Game.get.getInput();
	}
}
