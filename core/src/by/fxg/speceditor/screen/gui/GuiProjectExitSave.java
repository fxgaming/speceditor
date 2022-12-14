package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public final class GuiProjectExitSave extends Gui {
	private UButton buttonClose;
	private UHoldButton buttonSaveExit, buttonExit;
	
	public GuiProjectExitSave() {
		super(null);
		this.buttonClose = new UButton("Cancel").setColor(UColor.greenblack);
		this.buttonSaveExit = new UHoldButton("Save", UHoldButton.NO_KEY, 30).setColor(UColor.yellowblack);
		this.buttonExit = new UHoldButton("Don't save", UHoldButton.NO_KEY, 60).setColor(UColor.redblack);
		
		this.resize(Utils.getWidth(), Utils.getHeight());
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) this.closeGui();
		if (this.buttonSaveExit.isPressed()) {
			ProjectManager.currentProject.saveConfiguration();
			if (ProjectManager.currentProject.saveProject()) {
				SpecEditor.get.tools.exitExplicitly();
			}
		}
		if (this.buttonExit.isPressed()) SpecEditor.get.tools.exitExplicitly();
		this.buttonSaveExit.update();
		this.buttonExit.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
		
		int x = width / 2 - 210, y = height / 2 - 35, boxWidth = 320, boxHeight = 70;
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxWidth, boxHeight);
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(x, y, boxWidth, boxHeight, 2f);

		foster.setString("Are you sure you want to exit?").draw(x + boxWidth / 2, y + boxHeight - 20);
		foster.setString("Do you want to save your project before exiting?").draw(x + boxWidth / 2, y + boxHeight - 32);
		
		this.buttonClose.render(shape, foster);
		this.buttonSaveExit.render(shape, foster);
		this.buttonExit.render(shape, foster);
		batch.end();
	}
	
	public void resize(int width, int height) {
		int x = width / 2 - 210, y = height / 2 - 35, boxWidth = 320;
		this.buttonClose.setTransforms(x + boxWidth - 60, y + 10, 50, 13);
		this.buttonSaveExit.setTransforms(x + boxWidth - 120, y + 10, 50, 13);
		this.buttonExit.setTransforms(x + boxWidth - 200, y + 10, 70, 13);
	}
}
