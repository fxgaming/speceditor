package by.fxg.speceditor.std.ui;

import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement.Type;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class STDDropdownArea extends UIElement implements IFocusable {
	protected Array<STDDropdownAreaElement> elements = new Array<>();
	protected int dropHeight;
	protected long tickOpen = -1L;
	protected STDDropdownAreaElement shownChild = null;
	
	public STDDropdownArea(int dropHeight) {
		this.dropHeight = dropHeight;
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		if (this.isFocused()) {
			int yPos = this.y + 1;
			for (int i = 0; i != this.elements.size; i++) {
				yPos -= this.elements.get(i).render(this, null, shape, foster, this.x, yPos, this.width);
			}
			if (this.tickOpen < SpecEditor.get.getTick() && SpecEditor.get.getInput().isMouseDown(0, false)) this.close();
			shape.setColor(UColor.elementBoundsClicked);
			shape.rectangle(this.x, yPos - 1, this.width, this.y - yPos + 1);
			//shape.line(this.x, this.y, this.x + this.width, this.y);
			//shape.line(this.x, yPos, this.x + this.width, yPos);
		}
	}
	
	public STDDropdownArea add(STDDropdownAreaElement element) { return this.add(element, true); }
	public STDDropdownArea add(STDDropdownAreaElement element, boolean notify) {
		if (element != null) {
			if (element != null && (notify && this.actionListener != null ? this.actionListener.onDropdownAreaAddElement(this, this.actionListenerID, null, element) : true)) {
				this.elements.add(element.setDropdownArea(this));
				if (element.type != Type.LINE) {
					for (int i = 0; i != this.elements.size; i++) {
						STDDropdownAreaElement element$ = this.elements.get(i);
						if (element$.name != null) {
							int size = (int)SpecEditor.fosterNoDraw.setString(element$.name).getWidth() + (element$.type == Type.SUBWINDOW ? 25 : 10);
							if (this.width < size) {
								this.width = size;
							}
						}
					}
				}
			}
		}
		return this;
	}
	
	public STDDropdownArea setElements(Array<STDDropdownAreaElement> elements, Foster foster) {
		this.elements = new Array<>();
		for (int i = 0; i != elements.size; i++) {
			STDDropdownAreaElement element = elements.get(i);
			if (this.actionListener != null ? this.actionListener.onDropdownAreaAddElement(this, this.actionListenerID, null, element) : true) {
				this.elements.add(element.setDropdownArea(this));
				if (element.name != null) {
					int size = (int)SpecEditor.fosterNoDraw.setString(element.name).getWidth() + (element.type == Type.SUBWINDOW ? 25 : 10);
					if (this.width < size) {
						this.width = size;
					}
				}
			}
		}
		elements.clear(); // XXX may cause lags
		return this;
	}
	
	public STDDropdownArea open() { return this.open(GDXUtil.getMouseX(), GDXUtil.getMouseY()); }
	public STDDropdownArea open(int x, int y) {
		this.x = x;
		this.y = y - this.height;
		this.setFocused(true);
		this.tickOpen = SpecEditor.get.getTick() + 1;
		return this;
	}
	
	public STDDropdownArea close() {
		this.setFocused(false);
		return this;
	}
	
	public STDDropdownArea setTransforms(float dropHeight) {
		this.dropHeight = dropHeight > 0 ? (int)dropHeight : 0;
		return this;
	}
	
	public Array<STDDropdownAreaElement> getElementsArrayAsEmpty() {
		this.elements.size = 0;
		return this.getElements();
	}
	
	public Array<STDDropdownAreaElement> getElements() {
		return this.elements;
	}
}
