package by.fxg.speceditor.std.ui;

import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDragArea;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;

public interface ISTDInterfaceActionListener {
	default void onHoldButtonAction(UHoldButton element, String id, int ticks) {}
	default void onCheckboxAction(UCheckbox element, String id) {}
	default void onDropdownClickAction(UDropdownClick element, String id, int variant) {}
	default void onDropdownSelectSingleAction(UDropdownSelectSingle element, String id, int variant) {}
	default void onDropdownSelectMultipleAction(UDropdownSelectMultiple element, String id, int variant) {}
	default void onDragAreaDrag(UDragArea dragArea, String id, int start, int value, boolean stopFocus) {}
	
	default void onDropdownAreaClick(STDDropdownArea area, String id, STDDropdownAreaElement element, String elementID) {}
	default boolean onDropdownAreaAddElement(STDDropdownArea area, String id, STDDropdownAreaElement parent, STDDropdownAreaElement target) { return true; }
}
