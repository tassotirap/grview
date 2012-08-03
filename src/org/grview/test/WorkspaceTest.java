package org.grview.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JComboBox;

import junit.framework.Assert;

import org.grview.ui.WorkspaceChooser;
import org.junit.Before;
import org.junit.Test;

public class WorkspaceTest
{

	WorkspaceChooser workspaceChooser;

	@Before
	public void setUp()
	{
		workspaceChooser = WorkspaceChooser.getInstance();
	}

	@Test
	public void cancelClick()
	{
		Method method;
		try
		{
			method = WorkspaceChooser.class.getDeclaredMethod("btnCancelActionPerformed");
			method.setAccessible(true);
			method.invoke(workspaceChooser);

			Assert.assertEquals(workspaceChooser.isCanceled(), true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	
	@Test
	public void addAndLoadDirectory()
	{
		Method addDirToList, readDirsFromList;
		Field field;
		
		try
		{
			addDirToList = WorkspaceChooser.class.getDeclaredMethod("addDirToList", String.class);
			addDirToList.setAccessible(true);
			addDirToList.invoke(workspaceChooser, "C:/TESTE");

			field = WorkspaceChooser.class.getDeclaredField("ckbWorkspace");
			field.setAccessible(true);
			JComboBox<String> ckbWorkspace = (JComboBox<String>)field.get(workspaceChooser);
			
			ckbWorkspace.removeAllItems();
			
			readDirsFromList = WorkspaceChooser.class.getDeclaredMethod("readDirsFromList");
			readDirsFromList.setAccessible(true);
			readDirsFromList.invoke(workspaceChooser);
			ckbWorkspace.setSelectedItem("C:/TESTE");
			
			
			Assert.assertTrue(ckbWorkspace.getSelectedIndex() != -1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
