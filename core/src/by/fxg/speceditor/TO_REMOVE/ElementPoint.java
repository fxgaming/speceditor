package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementPoint extends __TreeElement {
	private TreeElementRenderable<ElementPoint> renderable;
	public ElementPointArray parent = null;

	protected Vector3 position = new Vector3();
	
	public ElementPoint() { this("New point"); }
	public ElementPoint(String name) {
		this.name = name;
		this.renderable = new TERPoint(this);
	}
	
	public void onInteract(SpecObjectTree list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.deselectElement(this);
				else list.selectElement(this);
			}
		}
	}
	
	public void addDropdownParameters(SpecObjectTree pmoe, Array<__TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
		}
		super.addDefaultDropdownParameters(pmoe, selected, array);
	}
	
	public void processDropdown(SpecObjectTree pmoe, Array<__TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (__TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (__TreeElement element : selected) element.setVisible(true);
			} return;
		}
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public Vector3 getOffsetTransform(GizmoTransformType transformType) {
		if (this.parent != null) {
			switch (transformType) {
				case TRANSLATE: return localTempVector.set(this.parent.getTransform(GizmoTransformType.TRANSLATE));
				default:
			}
		}
		return localTempVector.set(0, 0, 0);
	}
	
	public boolean isTransformable(GizmoTransformType transformType) { return transformType == GizmoTransformType.TRANSLATE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.point.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public __TreeElement clone() {
		ElementPoint element = new ElementPoint(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.position.set(this.position);
		if (this.parent != null) element.parent = this.parent;
		return element;
	}
}
