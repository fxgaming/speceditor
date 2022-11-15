package by.fxg.speceditor.scenes;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.ITreeElementHandler;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.viewport.IViewportRenderer;

public class ScenesObjectTreeHandler implements ITreeElementHandler {
	private ScenesProject prefabProject;
	
	public ScenesObjectTreeHandler(ScenesProject prefabProject) {
		this.prefabProject = prefabProject;
	}
	
	public void onRefresh(SpecObjectTree objectTree) {
		this.prefabProject.projectScreen.subEditorPane.updateSelectableEditorPane(objectTree.elementSelector);
		this.prefabProject.projectScreen.subViewport.gizmosModule.updateSelectorMode(objectTree.elementSelector);

		this.prefabProject.renderer.clear();
		this.searchRenderables(this.prefabProject.renderer, objectTree, objectTree.getStack().getElements(), true);
	}
	
	private void searchRenderables(IViewportRenderer renderer, SpecObjectTree objectTree, Array<TreeElement> elements, boolean parentVisible) { 
		for (int i = 0; i != elements.size; i++) {
			TreeElement element = elements.get(i);
			if (element != null) {
				if ((parentVisible && element.isVisible() || objectTree.elementSelector.isElementSelected(element))) {
					renderer.add(objectTree, element);
				}
				if (element instanceof ITreeElementFolder) {
					this.searchRenderables(renderer, objectTree, ((ITreeElementFolder)element).getFolderStack().getElements(), parentVisible ? element.isVisible() : parentVisible);
				}
			}
		}
	}
}
