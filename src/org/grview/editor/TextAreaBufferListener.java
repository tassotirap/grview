package org.grview.editor;

import org.grview.editor.buffer.BufferListener;
import org.grview.editor.buffer.JEditBuffer;
import org.grview.ui.component.AbstractComponent;


public class TextAreaBufferListener implements BufferListener {

	private AbstractComponent parent;
	
	public TextAreaBufferListener(AbstractComponent parent) {
		this.parent = parent;
	}
	
	@Override
	public void bufferLoaded(JEditBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contentInserted(JEditBuffer buffer, int startLine, int offset,
			int numLines, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
			int numLines, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void foldHandlerChanged(JEditBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void foldLevelChanged(JEditBuffer buffer, int startLine, int endLine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preContentInserted(JEditBuffer buffer, int startLine,
			int offset, int numLines, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preContentRemoved(JEditBuffer buffer, int startLine,
			int offset, int numLines, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transactionComplete(JEditBuffer buffer) {
		parent.fireContentChanged();
		
	}

}
