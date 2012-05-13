package org.grview.test;

import java.lang.reflect.Method;

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
	public void okClick()
	{
		Method method;
		try
		{
			method = WorkspaceChooser.class.getDeclaredMethod("btnOkActionPerformed");
			method.setAccessible(true);
			method.invoke(workspaceChooser);

			Assert.assertEquals(workspaceChooser.isDone(), true);
			Assert.assertNotNull(workspaceChooser.getWorkspaceDir());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
