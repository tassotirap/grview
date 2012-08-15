package org.grview.util;

import javax.swing.JOptionPane;

public abstract class ErrorPop
{

	public static void ErrorRegistered(Object message)
	{
		JOptionPane.showMessageDialog(null, message, "An Erro Occurred", JOptionPane.ERROR_MESSAGE);
	}

}
