package org.grview.ui.component;

import java.util.HashMap;

import org.grview.editor.TextArea;


public class TextAreaRepo {

	private static HashMap<Component, TextArea> taByComp = new HashMap<Component, TextArea>();
	private static HashMap<TextArea, FileComponent> compByTa = new HashMap<TextArea, FileComponent>();
	
	public static void register(Component comp, TextArea ta) {
		taByComp.put(comp, ta);
		if (comp instanceof FileComponent) {
			compByTa.put(ta, (FileComponent) comp);
		}
	}
	
	public static void remove(Component comp) {
		if (taByComp.containsKey(comp)) {
			TextArea ta = taByComp.get(comp);
			taByComp.remove(comp);
			compByTa.remove(ta);
		}
	}
	
	public static void remove(TextArea ta) {
		if (compByTa.containsKey(ta)) {
			FileComponent comp = compByTa.get(ta);
			compByTa.remove(ta);
			taByComp.remove(comp);
		}
	}
	public static TextArea getTextArea(Component comp) {
		if (taByComp.containsKey(comp)) {
			return taByComp.get(comp);
		}
		return null;
	}
	
	public static FileComponent getComponent(TextArea ta) {
		if (compByTa.containsKey(ta)) {
			return compByTa.get(ta);
		}
		return null;
	}
	
}
