package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.STDManager;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownArea;
import by.fxg.speceditor.ui.UDropdownArea.IUDropdownAreaListener;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatsel extends URenderBlock implements IUDropdownAreaListener {
	private Array<Material> materials = null;
	private EditorPaneMatselModule currentModule;
	private UDropdownSelectSingle selectedMaterial, selectedAttribute;
	private UButton buttonAddAttribute;
	private UHoldButton buttonRemoveAttribute;
	
	/** Rendering is not handled for this element! Use following code where you need! <br>
	 * <code> if (matselObj.dropdownArea.isFocused()) matselObj.dropdownArea.render(shapeDrawerObj, fosterObj); </code><br>
	 *  FIXME: URenderBlock should render box before rendering things inside, cache yOffset from prev. frame and use it to render box **/
	public UDropdownArea dropdownArea;
	
	public EditorPaneMatsel(String name) {
		super(name);
		this.materials = new Array<>();
		this.selectedMaterial = new UDropdownSelectSingle(15, "None") {
			public UDropdownSelectSingle setSelectedVariant(int variant) {
				this.selectedVariant = variant;
				EditorPaneMatsel.this.refreshAttributes();
				return this;
			}
		};
		this.selectedAttribute = new UDropdownSelectSingle(15, "None") {
			public UDropdownSelectSingle setSelectedVariant(int variant) {
				this.selectedVariant = variant;
				EditorPaneMatsel.this.onAttributeSelect();
				return this;
			}
		};
		this.buttonAddAttribute = new UButton("+");
		this.buttonRemoveAttribute = new UHoldButton("Remove attribute", UHoldButton.NO_KEY, 30).setColor(UColor.redblack);
		this.dropdownArea = new UDropdownArea(this, 15);
	}

	//FIXME requires UI reworking and nice offsets
	protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
		foster.setString("Material:").draw(this.x, yOffset -= 2, Align.left);
		this.selectedMaterial.setTransforms(this.x + (int)foster.getWidth() + 5, yOffset - 11, this.width - (int)foster.getWidth() - 5, 14);
	
		if (!this.selectedMaterial.isDropped()) {
			Material material = this.getCurrentMaterial();
			if (material != null) {
				shape.setColor(UColor.gray);
				shape.line(this.x, yOffset -= 15, this.x + this.width, yOffset);
				this.buttonAddAttribute.setTransforms(this.x, (yOffset -= 7) - 11, 14, 14).render(shape, foster);
				if (this.buttonAddAttribute.isPressed()) {
					Array<UDAElement> elements = new Array<>();
					STDManager.INSTANCE.getEditorPaneMatselModules().forEach(editorPaneMatselModule -> editorPaneMatselModule.onAttributeCreationPress(elements));
					this.dropdownArea.set(foster, elements).open(this.x + 1, yOffset + 3);
				}
				foster.setString("Attrib:").draw(this.x + 18, yOffset, Align.left);
				this.selectedAttribute.setTransforms(this.x + (int)foster.getWidth() + 23, yOffset - 11, this.width - (int)foster.getWidth() - 23, 15);
				
				if (!this.selectedAttribute.isDropped()) {
					if (this.currentModule != null) {
						shape.setColor(UColor.gray);
						shape.line(this.x, (yOffset -= 5) - 10, this.x + this.width, yOffset - 10);
						yOffset -= 12; //:(
						try {
							yOffset = this.currentModule.renderModule(batch, shape, foster, yOffset, this.x, this.width);
						} catch (Exception e) {
							Utils.logError(e, "EditorPaneMatsel#renderInside", "Unrepeatable bug caused an error");
						}
						yOffset += 6; //:(
					} else if (this.selectedAttribute.getVariant() > 0) {
						shape.setColor(UColor.gray);
						shape.line(this.x, (yOffset -= 5) - 10, this.x + this.width, yOffset - 10);
						foster.setString("Module not found for this attribute").draw(this.x + this.width / 2, yOffset - 12);
						yOffset -= 12; //:(
					}
					Attribute attribute = this.getCurrentAttribute(material);
					if (this.selectedAttribute.getVariant() > 0 && attribute != null) {
						shape.setColor(UColor.gray);
						shape.line(this.x, (yOffset -= 5) - 5, this.x + this.width, yOffset - 5);
						this.buttonRemoveAttribute.setTransforms(this.x, (yOffset -= 10) - 10, this.width, 12).update();
						this.buttonRemoveAttribute.render(shape, foster);
						if (this.buttonRemoveAttribute.isPressed()) {
							material.remove(attribute.type);
							this.refreshAttributes();
						}
					}
				} else yOffset -= this.selectedAttribute.getVariants().length * 15 + 2;
				this.selectedAttribute.update();
				this.selectedAttribute.render(shape, foster);
			}
		} else yOffset -= this.selectedMaterial.getVariants().length * 15 + 2;
		this.selectedMaterial.update();
		this.selectedMaterial.render(shape, foster);
		return yOffset;
	}
	
	public Material getCurrentMaterial() {
		return this.selectedMaterial.getVariant() > 0 && this.materials.size >= this.selectedMaterial.getVariant() ? this.materials.get(this.selectedMaterial.getVariant() - 1) : null;
	}
	
	public Attribute getCurrentAttribute(Material material) {
		Attribute attribute = null;
		if (material != null) {
			//FIXME bad way to search for attribute
			int _index = 0;
			for (Attribute attribute$ : material) if (++_index == this.selectedAttribute.getVariant()) {
				attribute = attribute$;
				break;
			}
		}
		return attribute;
	}

	public void onDropdownClick(String id) {
		STDManager.INSTANCE.getEditorPaneMatselModules().forEach(editorPaneMatselModule -> editorPaneMatselModule.onDropdownClick(this, id));
		this.refreshAttributes();
	}
	
	private void onAttributeSelect() {
		Attribute attribute = this.getCurrentAttribute(this.getCurrentMaterial());
		EditorPaneMatselModule editorPaneMatselModule = STDManager.INSTANCE.searchAvailablePaneMatselModule(this, attribute);
		if (attribute != null && editorPaneMatselModule != null && editorPaneMatselModule.acceptAttribute(this, attribute)) {
			editorPaneMatselModule.onSelect(this, attribute);
			this.currentModule = editorPaneMatselModule;
		} else this.currentModule = null;
	}
	
	private void refreshAttributes() {
		Array<String> attributes = new Array<>();
		attributes.add("None");
		if (this.materials != null && this.selectedMaterial.getVariant() > 0) {
			for (Attribute attribute : this.materials.get(this.selectedMaterial.getVariant() - 1)) {
				attributes.add(Utils.format(attribute.getClass().getSimpleName().replace("Attribute", ""), " - ", Attribute.getAttributeAlias(attribute.type)));
			}
		}
		this.selectedAttribute.setVariants(attributes.toArray(String.class));
		if (attributes.size <= this.selectedAttribute.getVariant()) this.selectedAttribute.setSelectedVariant(attributes.size - 1);
	}
	
	public void update(Array<Material> materials) {
		this.selectedMaterial.setSelectedVariant(0);
		this.selectedAttribute.setSelectedVariant(0);
		this.materials = materials;
		if (materials != null) {
			String[] materialNames = new String[materials.size + 1];
			materialNames[0] = "None";
			for (int i = 0; i != materials.size; i++) materialNames[i + 1] = materials.get(i).id;
			this.selectedMaterial.setVariants(materialNames);
		} else this.selectedMaterial.setVariants("None");
	}
}
