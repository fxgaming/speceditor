package by.fxg.speceditor.screen.project.map;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenExplorer extends BaseSubscreen {
	public ScreenProject parent;
	
	public SubscreenExplorer(ScreenProject parent) {
		this.parent = parent;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {

	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 2, y + 1, width - 3, height - 3, 1);
		foster.setString("Explorer").draw(x + width / 2, y + height / 2);
		batch.end();
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
	}
}