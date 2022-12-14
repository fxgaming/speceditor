package by.fxg.speceditor.std.viewport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselEnvironment;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.ColoredInputField.Builder;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneGLTFViewportRenderer extends EditorPane implements ISTDInputFieldListener {
	private final String[] cameraParams = {"FOV", "Far", "Near"}, colors = {"R", "G", "B", "A"};
	private final Color[] fieldColors = {UColor.redblack, UColor.greenblack, UColor.blueblack, UColor.black025alpha};
	private GLTFRenderer renderer;
	
	protected STDInputField[] cameraSettings = new STDInputField[3];
	protected STDInputField[] bufferColor = new STDInputField[4];
	
	private ViewportFeaturesBlock viewportFeatures;
	private EditorPaneMatselEnvironment environmentMatsel;
	
	public EditorPaneGLTFViewportRenderer(GLTFRenderer renderer) {
		this.renderer = renderer;
		
		ColoredInputField.Builder builder = (Builder)new ColoredInputField.Builder().setFoster(SpecEditor.fosterNoDraw).setAllowFullfocus(false).setNumeralInput(true).setMaxLength(12);
		for (int i = 0; i != 3; i++) this.cameraSettings[i] = builder.setBackgroundColor(UColor.yellowblack).setListener(this, "camera").build();
		for (int i = 0; i != 4; i++) this.bufferColor[i] = builder.setBackgroundColor(this.fieldColors[i]).setListener(this, "bufferColor").build();
		builder.addToLink(this.cameraSettings).addToLink(this.bufferColor).linkFields();
		
		UIElement._convertVector3ToText(this.renderer.cameraSettings, this.cameraSettings[0], this.cameraSettings[1], this.cameraSettings[2], true);
		UIElement._convertColorToText(this.renderer.bufferColor, this.bufferColor[0], this.bufferColor[1], this.bufferColor[2], this.bufferColor[3], true);
		this.viewportFeatures = new ViewportFeaturesBlock(this);
		this.environmentMatsel = (EditorPaneMatselEnvironment)new EditorPaneMatselEnvironment("Environment", renderer.gltfScene.environment).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		foster.setString("Camera settings:").draw(x + 5, yOffset -= foster.getHeight() + 5, Align.left);
		
		float inputX = this.getLongestStringWidth(foster, cameraParams);
		yOffset -= 20;
		for (int i = 0; i != 3; i++) {
			foster.setString(this.cameraParams[i]).draw(x + 10, yOffset + foster.getHalfHeight(), Align.left);
			this.cameraSettings[i].setTransforms(x + inputX + 15, yOffset, width - inputX - 20, 15).setFoster(foster).update();
			this.cameraSettings[i].render(batch, shape);
			yOffset -= 18;
		}
		
		int sizePerPart = (width - 30 - (int)foster.setString(this.colors[0]).getWidth() * 2) / 2;
		foster.setString("Buffer(screen) color:").draw(x + 5, yOffset, Align.left);
		yOffset -= 20;
		for (int i = 0; i != 2; i++) {
			foster.setString(this.colors[i]).draw(x + 15 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
			this.bufferColor[i].setTransforms(x + 15 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
			this.bufferColor[i].render(batch, shape);
		}
		yOffset -= 16;
		for (int i = 0, k = 2; i != 2; i++, k++) {
			foster.setString(this.colors[k]).draw(x + 15 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
			this.bufferColor[k].setTransforms(x + 15 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
			this.bufferColor[k].render(batch, shape);
		}
		
		yOffset = this.viewportFeatures.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		yOffset = this.environmentMatsel.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		if (this.environmentMatsel.dropdownArea.isFocused()) this.environmentMatsel.dropdownArea.render(shape, foster);
		return yOffset;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "camera": this.renderer.setCameraValues(this.cameraSettings[0].getTextAsNumber(this.renderer.cameraSettings.x), this.cameraSettings[1].getTextAsNumber(this.renderer.cameraSettings.y), this.cameraSettings[2].getTextAsNumber(this.renderer.cameraSettings.z)); break;
			case "bufferColor": UIElement._convertTextToColor(this.renderer.bufferColor, this.bufferColor[0], this.bufferColor[1], this.bufferColor[2], this.bufferColor[3]); break;
		}
	}
	
	public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
		try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
	}
	
	public void setEnvironment(Environment environment) {
		this.environmentMatsel.setEnvironment(environment);
	}

	private class ViewportFeaturesBlock extends URenderBlock implements ISTDInputFieldListener {
		private EditorPaneGLTFViewportRenderer parent;
		protected UCheckbox featureHitboxDepth, featureRenderGrid;
		protected STDInputField featureHitboxWidth;
		
		private ViewportFeaturesBlock(EditorPaneGLTFViewportRenderer parent) {
			super("Features");
			this.parent = parent;
			
			this.featureHitboxDepth = new UCheckbox(this.parent.renderer.featureHitboxDepth);
			this.featureRenderGrid = new UCheckbox(this.parent.renderer.featureRenderGrid);
			this.featureHitboxWidth = new ColoredInputField().setBackgroundColor(UColor.greengray).setAllowFullfocus(false).setNumeralInput(true).setMaxLength(12).setListener(this, "hitboxWidth");
			this.featureHitboxWidth.setFoster(SpecEditor.fosterNoDraw).setTextWithPointer(String.valueOf(this.parent.renderer.featureHitboxWidth)).dropOffset();
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			yOffset -= 8; //8-5, idk
			int x = this.x + 5;
			float longestText = this.parent.getLongestStringWidth(foster, "Hitbox depth", "Hitbox line width", "Draw grid");		
			foster.setString("Hitbox depth").draw(x, yOffset -= foster.getHeight(), Align.left);
			this.featureHitboxDepth.setTransforms(x + longestText + 5, yOffset - 2, 12, 12).update();
			this.featureHitboxDepth.render(shape);
			this.parent.renderer.featureHitboxDepth = this.featureHitboxDepth.getValue();
			foster.setString("Hitbox line width").draw(x, (yOffset -= foster.getHeight() + 9) - 1, Align.left);
			this.featureHitboxWidth.setTransforms(x + longestText + 5, yOffset - foster.getHalfHeight(), this.width - longestText - 15, 15).setFoster(foster).update();
			this.featureHitboxWidth.render(batch, shape);
			foster.setString("Draw grid").draw(x, yOffset -= foster.getHeight() + 10, Align.left);
			this.featureRenderGrid.setTransforms(x + longestText + 5, yOffset - 2, 12, 12).update();
			this.featureRenderGrid.render(shape);
			this.parent.renderer.featureRenderGrid = this.featureRenderGrid.getValue();
			return yOffset - 5;
		}
		
		public void whileInputFieldFocused(STDInputField inputField, String id) {
			switch (id) {
				case "hitboxWidth": this.parent.renderer.featureHitboxWidth = this.featureHitboxWidth.getTextAsNumber(this.parent.renderer.featureHitboxWidth); break;
			}
		}
		
		public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
			try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
		}
	}
	
	public void updatePane(ITreeElementSelector<?> selector) {}
	public boolean acceptElement(ITreeElementSelector<?> selector) { return false; }
}
