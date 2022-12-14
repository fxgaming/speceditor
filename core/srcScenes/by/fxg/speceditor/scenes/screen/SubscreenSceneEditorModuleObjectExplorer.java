package by.fxg.speceditor.scenes.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.editorPane.EditorPaneDecal;
import by.fxg.speceditor.std.editorPane.EditorPaneGLTFLight;
import by.fxg.speceditor.std.editorPane.EditorPaneHitbox;
import by.fxg.speceditor.std.editorPane.EditorPaneHitboxMesh;
import by.fxg.speceditor.std.editorPane.EditorPaneHitboxStack;
import by.fxg.speceditor.std.editorPane.EditorPaneLight;
import by.fxg.speceditor.std.editorPane.EditorPaneModel;
import by.fxg.speceditor.std.editorPane.EditorPaneMultipleGizmoTransform;
import by.fxg.speceditor.std.editorPane.EditorPaneStandardRename;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenSceneEditorModuleObjectExplorer extends BaseSubscreen {
	private Vector2 scroll = new Vector2(); //scroll, lastHeight
	private Array<EditorPane> editorPanes = new Array<>();
	private EditorPane currentEditorPane = null;
	
	public SubscreenSceneEditorModuleObjectExplorer(boolean useLegacyRenderer) {
		this.editorPanes.addAll(new EditorPaneStandardRename(), new EditorPaneMultipleGizmoTransform());
		this.editorPanes.addAll(new EditorPaneHitbox(), new EditorPaneHitboxMesh(), new EditorPaneHitboxStack());
		
		this.editorPanes.addAll(new EditorPaneDecal(), new EditorPaneLight(), new EditorPaneModel());
		if (!useLegacyRenderer) {
			this.editorPanes.addAll(new EditorPaneGLTFLight());
		}
		this.editorPanes.reverse();
	}
	
	@Override
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (GDXUtil.isMouseInArea(x, y, width, height) && SpecInterface.isFocused(this)) {
			if (SpecEditor.get.getInput().isMouseScrolled(true) && this.scroll.x < this.scroll.y) {
				this.scroll.x = Math.min(this.scroll.x + 50, this.scroll.y);
			} else if (SpecEditor.get.getInput().isMouseScrolled(false) && this.scroll.x > 0) {
				this.scroll.x = Math.max(0, this.scroll.x - 50);
			}
		}
		if (this.scroll.x > this.scroll.y) this.scroll.x = this.scroll.y;
	}

	@Override
	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (this.currentEditorPane != null) {
			batch.flush();
			if (PilesosScissorStack.instance.peekScissors(x, y, width, height)) {
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
		} else foster.setString("Editor pane not found for element(s)").draw(x + width / 2, y + height / 2 - foster.getHalfHeight());
	}
	
	protected void updateEditorPane(ITreeElementSelector<?> treeElementSelector) {
		EditorPane pane = this.searchAvailablePane(treeElementSelector);
		if (pane != null) {
			pane.updatePane(treeElementSelector);
			this.currentEditorPane = pane;
		} else this.currentEditorPane = null;
	}

	protected EditorPane searchAvailablePane(ITreeElementSelector<?> treeElementSelector) {
		for (EditorPane editorPane : this.editorPanes) {
			if (editorPane.acceptElement(treeElementSelector)) {
				return editorPane;
			}
		}
		return null;
	}
	
	@Override
	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}