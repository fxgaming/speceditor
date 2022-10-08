package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.ui.STDInputField;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class EditorPane {
	/** Combined update and render method. Returns height of rendered elements to calculate scroll value. **/
	abstract public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset);
	
	abstract public void updatePane(ITreeElementSelector<?> selector);
	abstract public boolean acceptElement(ITreeElementSelector<?> selector);
	
	protected void _convertColorToText(Color color, STDInputField fieldR, STDInputField fieldG, STDInputField fieldB, STDInputField fieldA) {
		fieldR.setText(String.valueOf(color.r));
		fieldG.setText(String.valueOf(color.g));
		fieldB.setText(String.valueOf(color.b));
		fieldA.setText(String.valueOf(color.a));
	}
	
	protected void _convertVector3ToText(Vector3 vec, STDInputField fieldX, STDInputField fieldY, STDInputField fieldZ) {
		fieldX.setText(String.valueOf(vec.x));
		fieldY.setText(String.valueOf(vec.y));
		fieldZ.setText(String.valueOf(vec.z));
	}

	protected void _convertVector2ToText(Vector2 vec, STDInputField fieldX, STDInputField fieldY) {
		fieldX.setText(String.valueOf(vec.x));
		fieldY.setText(String.valueOf(vec.y));
	}
	
	protected void _convertTextToColor(Color color, STDInputField fieldR, STDInputField fieldG, STDInputField fieldB, STDInputField fieldA) {
		color.set(this._convertTextToFloat(fieldR, color.r), this._convertTextToFloat(fieldG, color.g), this._convertTextToFloat(fieldB, color.b), this._convertTextToFloat(fieldA, color.a));
	}
	
	protected void _convertTextToVector3(Vector3 vec, STDInputField fieldX, STDInputField fieldY, STDInputField fieldZ) {
		vec.set(this._convertTextToFloat(fieldX, vec.x), this._convertTextToFloat(fieldY, vec.y), this._convertTextToFloat(fieldZ, vec.z));
	}

	protected void _convertTextToVector2(Vector2 vec, STDInputField fieldX, STDInputField fieldY) {
		vec.set(this._convertTextToFloat(fieldX, vec.x), this._convertTextToFloat(fieldY, vec.y));
	}
	
	protected float _convertTextToFloat(STDInputField field, float failValue) {
		try {
			return Float.valueOf(field.getText());
		} catch (NullPointerException | NumberFormatException e) {
			if (!field.isFocused()) field.setText(String.valueOf(failValue));
			return failValue;
		}
	}
}
