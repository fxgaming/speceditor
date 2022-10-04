package by.fxg.speceditor.TO_REMOVE;

import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.LISTEN_COLLISION;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.NO_COLLISION;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.NO_FREEZE;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.OBJECT_DYNAMIC;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.OBJECT_STATIC;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.RAYCASTABLE;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.TRIGGER;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.hasFlag;
import static by.fxg.pilesos.specformat.graph.SpecHitbox.SHFlags.invertFlag;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TERMultiHitbox extends TreeElementRenderable<ElementMultiHitbox> {
	public URenderBlock[] blocks = new URenderBlock[3];
	protected UInputField[] input = new UInputField[10];
	protected UCheckbox checkbox;
	
	public TERMultiHitbox(ElementMultiHitbox object) {
		super(object);
		
		String numeral = "0123456789-.";
		for (int i = 0; i != this.input.length; i++) {
			Vector3 vector = i < 3 ? renderable.getTransform(GizmoTransformType.TRANSLATE) : i < 6 ? renderable.getTransform(GizmoTransformType.ROTATE) : renderable.getTransform(GizmoTransformType.SCALE);
			this.input[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(i % 3 == 0 ? vector.x : i % 3 == 1 ? vector.y : vector.z));
		}
		this.input[9] = new UInputField(0, 0, 0, 0).setMaxLength(24).setText(renderable.getName());
		this.checkbox = new UCheckbox(false, 0, 0, 0, 0);

		this.blocks[0] = new URenderBlock("Parameters", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Name:").draw(this.x, (y -= 10) - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				input[9].setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				input[9].render(batch, shape, foster);
				return y;
			}
		}.setDropped(true);
		
		this.blocks[1] = new URenderBlock("Transform", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				for (int i = 0; i != 3; i++) {
					switch(i) {
						case 0: foster.setString("Position:"); break;
						case 1: foster.setString("Rotation:"); break;
						case 2: foster.setString("Scale:"); break;
					}
					foster.draw(this.x, y -= 15, Align.left);
					y -= 8;
					for (int j = 0; j != 3; j++) {
						switch(j) {
							case 0: foster.setString("X:"); break;
							case 1: foster.setString("Y:"); break;
							case 2: foster.setString("Z:"); break;
						}
						foster.draw(this.x + 14, y - 9);
						drawWheelInput(renderable.getTransform(GizmoTransformType.values()[i]), j, shape, this.x + 24, y - 20, 12, 14);
						
						shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : UColor.blueblack);
						shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
						input[j + i * 3].setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
						input[j + i * 3].render(batch, shape, foster);
					}
				}
				y += 8;
				return y;
			}
		}.setDropped(true);
		
		this.blocks[2] = new URenderBlock("Flags", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				y -= 5;
				String[] names = {"No collision", "No freeze", "Static object", "Dynamic object", "Trigger", "Raycastable", "Listen collision"};
				long[] flags = {NO_COLLISION, NO_FREEZE, OBJECT_STATIC, OBJECT_DYNAMIC, TRIGGER, RAYCASTABLE, LISTEN_COLLISION};
				for (int i = 0; i != names.length; i++) {
					checkbox.setTransforms(this.x, (y -= 15) - 5, 12, 12).setValue(hasFlag(renderable.flags, flags[i])).update();
					checkbox.render(shape);
					if (checkbox.getValue() != hasFlag(renderable.flags, flags[i])) renderable.flags = invertFlag(renderable.flags, flags[i]);
					foster.setString(names[i]).draw(this.x + 18, y + 5, Align.left);
				}
				return y += 5;
			}
		}.setDropped(true);
	}
	
	public void resetInputFields() {
		this.renderable.setName(this.input[9].getText().length() == 0 ? "Unnamed" : this.input[9].getText());
		for (int i = 0; i != 10; i++) {
			if (i < 9) {
				Vector3 vector = i < 3 ? this.renderable.getTransform(GizmoTransformType.TRANSLATE) : i < 6 ? this.renderable.getTransform(GizmoTransformType.ROTATE) : this.renderable.getTransform(GizmoTransformType.SCALE);
				if (!this.input[i].isFocused()) {
					this.input[i].setText(String.valueOf(i % 3 == 0 ? vector.x : i % 3 == 1 ? vector.y : vector.z));
				} else {
					try {
						vector.set(i % 3 == 0 ? Float.valueOf(this.input[i].getText()) : vector.x, i % 3 == 1 ? Float.valueOf(this.input[i].getText()) : vector.y, i % 3 == 2 ? Float.valueOf(this.input[i].getText()) : vector.z);
					} catch (Exception e) {}
				}
			}
		}
	}
	
	public void update(int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 19, 15);
		this.resetInputFields();
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int hOffset, int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) hOffset = block.render(hOffset, batch, shape, foster);
		return hOffset;
	}
}
