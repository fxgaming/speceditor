package by.fxg.speceditor.prefabs;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.api.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.api.std.objectTree.ITreeElementHandler;
import by.fxg.speceditor.api.std.objectTree.TreeElement;
import by.fxg.speceditor.std.g3d.IModelProvider;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.std.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.render.IRendererType;

public class PrefabObjectTreeHandler implements ITreeElementHandler {
	private PrefabProject prefabProject;
	
	public PrefabObjectTreeHandler(PrefabProject prefabProject) {
		this.prefabProject = prefabProject;
	}
	
	public boolean onDropdownClick(SpecObjectTree objectTree, String id) {
		this.prefabProject.renderer.clear(true); //idk check required XXX
		return false;
	}
	
	public void onRefresh(SpecObjectTree objectTree) {
		this.prefabProject.projectScreen.subEditorPane.updateSelectableEditorPane(objectTree.elementSelector);

		this.prefabProject.renderer.clear(true);
		this.searchRenderables(this.prefabProject.renderer, objectTree, objectTree.getStack().getElements(), true);
	}
	
	private void searchRenderables(IRendererType renderer, SpecObjectTree objectTree, Array<TreeElement> elements, boolean parentVisible) { 
		for (TreeElement element : elements) {
			if (element != null) {
				//if (element instanceof ElementLight) renderer.addLight((ElementLight)element, parentVisible && element.isVisible(), objectTree.selectedItems.contains(element, true));
				
				if ((parentVisible && element.isVisible() || objectTree.elementSelector.isElementSelected(element))) {
					if (element instanceof IModelProvider || element instanceof IDebugDraw) renderer.add(element);
					//if (element instanceof ElementDecal) renderer.add(((ElementDecal)element).decal);
				}
				if (element instanceof ITreeElementFolder) {
//					if (element instanceof ElementMultiHitbox && (parentVisible && element.isVisible() || objectTree.selectedItems.contains(element, true))) {
//						for (__TreeElement element$ : element.getStack().getElements()) {
//							if (element$.isVisible() || objectTree.selectedItems.contains(element$, true)) {
//								if (element$ instanceof IDebugDraw) renderer.add(element$);
//							}
//						}
//					} else
					this.searchRenderables(renderer, objectTree, ((ITreeElementFolder)element).getFolderStack().getElements(), parentVisible ? element.isVisible() : parentVisible);
				}
			}
		}
	}
}
