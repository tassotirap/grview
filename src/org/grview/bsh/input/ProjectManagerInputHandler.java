package org.grview.bsh.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.grview.project.ProjectManager;
import org.grview.project.ProjectManagerBeanShellAction;


public abstract class ProjectManagerInputHandler extends BasicInputHandler<ProjectManagerBeanShellAction>{

	ProjectManager pManager;
	
	public ProjectManagerInputHandler(ProjectManager pManager) {
		this.pManager = pManager;
		
    	setKeyEventInterceptor(new KeyHandler());
    	setMouseEventInterceptor(new MouseHandler());
	}
	
	@Override
	public void invokeAction(ProjectManagerBeanShellAction action) {
		action.invoke(pManager);
	}
	
	protected class KeyHandler implements KeyListener {

		public void keyPressed(KeyEvent evt) {			
		}

		public void keyReleased(KeyEvent arg0) {		
		}

		public void keyTyped(KeyEvent arg0) {
		}
		
	}
	
	protected class MouseHandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {			
		}

		public void mouseExited(MouseEvent e) {			
		}

		public void mousePressed(MouseEvent event) {
		}

		public void mouseReleased(MouseEvent event) {
		}
		
	}
	
	
}
