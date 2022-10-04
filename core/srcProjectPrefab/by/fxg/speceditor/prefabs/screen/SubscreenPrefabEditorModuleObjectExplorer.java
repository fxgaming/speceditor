package by.fxg.speceditor.prefabs.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.api.std.editorPane.EditorPane;
import by.fxg.speceditor.api.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.STDManager;
import by.fxg.speceditor.ui.SpecInterface;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenPrefabEditorModuleObjectExplorer extends BaseSubscreen {
	private Vector2 scroll = new Vector2(); //scroll, lastHeight
	private EditorPane currentEditorPane = null;

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (GDXUtil.isMouseInArea(x, y, width, height) && SpecInterface.isFocused(this)) {
			if (Game.get.getInput().isMouseScrolled(true) && this.scroll.x < this.scroll.y) {
				this.scroll.x = Math.min(this.scroll.x + 50, this.scroll.y);
			} else if (Game.get.getInput().isMouseScrolled(false) && this.scroll.x > 0) {
				this.scroll.x = Math.max(0, this.scroll.x - 50);
			}
		}
		if (this.scroll.x > this.scroll.y) this.scroll.x = this.scroll.y;
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (this.currentEditorPane != null) {
			batch.flush();
			if (PilesosScissorStack.instance.setBounds(2, x, y, width, height).pushScissors(2)) {
				int paneY = y + (int)this.scroll.x;
				this.scroll.y = Math.max(0.01f, this.currentEditorPane.updateAndRender(batch, shape, foster, x, paneY, width - 4, height, paneY + height) - paneY - height * 2);
				float yScrollHeight = Interpolation.linear.apply(3, height, Math.min(height / (height + this.scroll.y), 1));
				float yScrollPosition = Interpolation.linear.apply(height - yScrollHeight, 0, Math.min(this.scroll.x / this.scroll.y, 1));
				shape.rectangle(x, y, width - 4, height);
				shape.setColor(1, 1, 1, 0.4f);
				shape.filledRectangle(x + width - 4, y + yScrollPosition + 1, 3, yScrollHeight);
				batch.flush();
				PilesosScissorStack.instance.popScissors();
			}
		} else foster.setString("Editor pane not found for element(s)").draw(x + width / 2, y + height / 2);
	}
	
	protected void updateEditorPane(ITreeElementSelector<?> treeElementSelector) {
		EditorPane pane = STDManager.INSTANCE.searchAvailablePane(treeElementSelector);
		if (pane != null) {
			pane.updatePane(treeElementSelector);
			this.currentEditorPane = pane;
		} else this.currentEditorPane = null;
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}