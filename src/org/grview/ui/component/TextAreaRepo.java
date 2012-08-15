package org.grview.ui.component;

import java.util.HashMap;

import org.grview.editor.TextArea;

public class TextAreaRepo
{
	private static HashMap<AbstractComponent, TextArea> textAreaByComponent = new HashMap<AbstractComponent, TextArea>();
	private static HashMap<TextArea, FileComponent> componentByTextArea = new HashMap<TextArea, FileComponent>();

	public static FileComponent getComponent(TextArea ta)
	{
		if (componentByTextArea.containsKey(ta))
		{
			return componentByTextArea.get(ta);
		}
		return null;
	}

	public static TextArea getTextArea(AbstractComponent comp)
	{
		if (textAreaByComponent.containsKey(comp))
		{
			return textAreaByComponent.get(comp);
		}
		return null;
	}

	public static void register(AbstractComponent component, TextArea textArea)
	{
		textAreaByComponent.put(component, textArea);
		if (component instanceof FileComponent)
		{
			componentByTextArea.put(textArea, (FileComponent) component);
		}
	}

	public static void remove(AbstractComponent comp)
	{
		if (textAreaByComponent.containsKey(comp))
		{
			TextArea ta = textAreaByComponent.get(comp);
			textAreaByComponent.remove(comp);
			componentByTextArea.remove(ta);
		}
	}

	public static void remove(TextArea ta)
	{
		if (componentByTextArea.containsKey(ta))
		{
			FileComponent comp = componentByTextArea.get(ta);
			componentByTextArea.remove(ta);
			textAreaByComponent.remove(comp);
		}
	}

}
