package org.grview.syntax.analyzer.gsll1;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.Scanner;

import org.grview.lexical.YyFactory;
import org.grview.lexical.Yylex;
import org.grview.semantics.SemanticRoutinesIvoker;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

public class Main
{

	public static void main(String[] args)
	{
		String source = null;		
		Yylex yylex = YyFactory.getYylex("F:/Export/", null, new StringReader(""));
		
		while (true)
		{
			System.out.print("> ");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			source = scanner.next();
			try
			{
				
				
				yylex.yyreset(new StringReader(source));
				
				
				TableGraphNode[] tabGraphNodes = toFileTabGraphNodes("F:/Export/tabGraphNodes.dat");
				TableNode[] nTerminalTab = toFileTnTerminalTab("F:/Export/nTerminalTab.dat");
				TableNode[] termialTab = toFileTerminalTab("F:/Export/termialTab.dat");
				
				new SemanticRoutinesIvoker(new File("F:/Export/semantic.sem"));
				
				Analyzer analyzer = new Analyzer(tabGraphNodes, termialTab, nTerminalTab, null, yylex);
				
				
				analyzer.run();
			}
			catch (Exception e)
			{
				System.err.println("Could not create and run the analyzer");
				e.printStackTrace();
			}
		}		
	}
	
	private static TableNode[] toFileTerminalTab(String file)
	{
		TableNode[] tableNode = null;
		try
		{
			
			try {
			    FileInputStream fin = new FileInputStream(file);
			    ObjectInputStream ois = new ObjectInputStream(fin);
			    tableNode = (TableNode[]) ois.readObject();
			    ois.close();
			    }
			   catch (Exception e) { e.printStackTrace(); }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return tableNode;

	}

	private static TableNode[] toFileTnTerminalTab(String file)
	{
		TableNode[] tableNode = null;
		try
		{
			
			try {
			    FileInputStream fin = new FileInputStream(file);
			    ObjectInputStream ois = new ObjectInputStream(fin);
			    tableNode = (TableNode[]) ois.readObject();
			    ois.close();
			    }
			   catch (Exception e) { e.printStackTrace(); }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return tableNode;

	}

	private static TableGraphNode[] toFileTabGraphNodes(String file)
	{
		TableGraphNode[] tableGraphNode = null;
		try
		{
			
			try {
			    FileInputStream fin = new FileInputStream(file);
			    ObjectInputStream ois = new ObjectInputStream(fin);
			    tableGraphNode = (TableGraphNode[]) ois.readObject();
			    ois.close();
			    }
			   catch (Exception e) { e.printStackTrace(); }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return tableGraphNode;

	}

}

