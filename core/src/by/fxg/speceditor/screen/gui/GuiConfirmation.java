package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class GuiConfirmation extends Gui {
	protected UButton buttonClose;
	protected UHoldButton buttonConfirm;
	protected String[] text;
	protected int textWidth = 0;
	
	public GuiConfirmation(String... text) {
		super(null);
		this.buttonClose = new UButton("Cancel").setColor(UColor.greenblack);
		this.text = text;
		
		for (String str : text) {
			if (SpecEditor.fosterNoDraw.setString(str).getWidth() > textWidth) {
				this.textWidth = (int)SpecEditor.fosterNoDraw.getWidth();
			}
		}
		
		this.resize(Utils.getWidth(), Utils.getHeight());
	}
	
	public GuiConfirmation setButton(String text, int key, int awaitTime, Color color) {
		this.buttonConfirm = new UHoldButton(text, key, awaitTime).setColor(color);
		this.resize(Utils.getWidth(), Utils.getHeight());
		return this;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) this.closeGui();
		if (this.buttonConfirm.isPressed()) {
			this.closeGui();
			this.onConfirm();
		}
		this.buttonConfirm.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
		
		int boxWidth = Math.max(this.textWidth + 20, 132), boxHeight = 45 + this.text.length * 12;
		int x = width / 2 - boxWidth / 2, y = height / 2 - boxHeight / 2;
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxWidth, boxHeight);
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(x, y, boxWidth, boxHeight, 2f);

		int textY = y + boxHeight - 8;
		for (int i = 0; i != this.text.length; i++) {
			foster.setString(this.text[i]).draw(x + boxWidth / 2, textY -= 12);
		}

		this.buttonClose.render(shape, foster);
		this.buttonConfirm.render(shape, foster);
		batch.end();
	}
	
	public void resize(int width, int height) {
		int boxWidth = Math.max(this.textWidth + 20, 132), boxHeight = 45 + this.text.length * 12;
		int x = width / 2 - boxWidth / 2, y = height / 2 - boxHeight / 2;
		this.buttonClose.setTransforms(x + boxWidth - 60, y + 10, 50, 13);
		if (this.buttonConfirm != null) this.buttonConfirm.setTransforms(x + boxWidth - 120, y + 10, 50, 13);
	}
	
	abstract public void onConfirm();
}
