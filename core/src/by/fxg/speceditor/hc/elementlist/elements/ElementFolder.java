package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.ElementStack;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERFolder;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementFolder extends TreeElement {
	private TreeElementRenderable<ElementFolder> renderable;
	private ElementStack elementStack;
	
	public ElementFolder() { this("New folder"); }
	public ElementFolder(String name) {
		this.name = name;
		this.elementStack = new ElementStack(this);
		this.renderable = new TERFolder(this);
	}
	
	public void onInteract(PMObjectExplorer list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isOpened = !this.isOpened;
					list.elementUnselect(this);
				} else {
					if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.elementUnselect(this);
					else list.elementSelect(this);
					this.lastClickTime = Game.get.getTick();
				}
			}
		}
	}

	public void addDropdownParameters(PMObjectExplorer pmoe, Array<TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
			array.add(new UDAElement(this.isOpened ? "basic.collapse" : "basic.open", this.isOpened ? "Collapse" : "Open"));
			super.addDefaultDropdownParameters(pmoe, selected, array);
			
			array.add(new UDAElement());
			UDAElement elementAddStorage = new UDAElement("folder.addstorage", "Create storage");
			elementAddStorage.addElement(new UDAElement("folder.add.folder", "Folder"));
			elementAddStorage.addElement(new UDAElement("folder.add.multihitbox", "Multi hitbox"));
			elementAddStorage.addElement(new UDAElement("folder.add.pointarray", "Point array"));
			
			UDAElement elementAddElement = new UDAElement("folder.addelement", "Create element");
			elementAddElement.addElement(new UDAElement("folder.add.model", "Model"));
			elementAddElement.addElement(new UDAElement("folder.add.light", "Light"));
			elementAddElement.addElement(new UDAElement("folder.add.hitbox", "Hitbox"));
			elementAddElement.addElement(new UDAElement("folder.add.meshhitbox", "Mesh hitbox"));
			elementAddElement.addElement(new UDAElement("folder.add.decal", "Decal"));
			array.add(elementAddStorage);
			array.add(elementAddElement);
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
			array.add(new UDAElement(this.isOpened ? "basic.collapse" : "basic.open", this.isVisible ? "Collapse all" : "Open all"));
			super.addDefaultDropdownParameters(pmoe, selected, array);
		}
	}
	
	public void processDropdown(PMObjectExplorer pmoe, Array<TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (TreeElement element : selected) element.setVisible(true);
			} return;
			case "basic.collapse": {
				for (TreeElement element : selected) element.setOpened(false);
			} return;
			case "basic.open": {
				for (TreeElement element : selected) element.setOpened(true);
			} return;
			
			case "folder.add.folder": this.elementStack.add(new ElementFolder()); return;
			case "folder.add.multihitbox": this.elementStack.add(new ElementMultiHitbox()); return;
			case "folder.add.pointarray": this.elementStack.add(new ElementPointArray()); break;
			
			case "folder.add.model": this.elementStack.add(new ElementModel()); return;
			case "folder.add.light": this.elementStack.add(new ElementLight()); return;
			case "folder.add.hitbox": this.elementStack.add(new ElementHitbox()); return;
			case "folder.add.meshhitbox": this.elementStack.add(new ElementMeshHitbox()); return;
			case "folder.add.decal": this.elementStack.add(new ElementDecal()); return;
		}
	}
	
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.folder.%b.%b", this.isVisible, this.isOpened)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public boolean hasStack() { return true; }
	public boolean stackAccepting(TreeElement element) { return true; }
	public ElementStack getStack() { return this.elementStack; }
	
	public TreeElement clone() {
		ElementFolder element = new ElementFolder(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.isOpened = this.isOpened;
		for (TreeElement element$ : this.elementStack.getItems()) {
			element.elementStack.add(element$.clone());
		}
		return element;
	}
}
