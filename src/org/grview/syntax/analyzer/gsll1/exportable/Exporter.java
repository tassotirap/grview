package org.grview.syntax.analyzer.gsll1.exportable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import org.grview.project.Project;
import org.grview.project.ProjectManager;
import org.grview.syntax.SyntacticLoader;
import org.grview.util.IOUtilities;

public class Exporter
{

	private String rootPath;
	private Analyzer.TabGraphNode[] tbG;
	private Analyzer.TableNode[] tbNt;
	private Analyzer.TableNode[] tbT;

	public Exporter(SyntacticLoader sl, String rootPath)
	{
		this.tbG = new Analyzer.TabGraphNode[sl.tabGraph().length];
		for (int i = 0; i < tbG.length; i++)
		{
			tbG[i] = new Analyzer.TabGraphNode();
			if (sl.tabGraph()[i] != null)
			{
				tbG[i].setAlt(sl.tabGraph()[i].getAlternativeIndex());
				tbG[i].setSuc(sl.tabGraph()[i].getSucessorIndex());
				tbG[i].setSim(sl.tabGraph()[i].getNodeReference());
				tbG[i].setSem(sl.tabGraph()[i].getSemanticRoutine());
				tbG[i].setTerm(sl.tabGraph()[i].IsTerminal());
			}
		}
		this.tbT = new Analyzer.TableNode[sl.tabT().length];
		for (int i = 0; i < tbT.length; i++)
		{
			if (sl.tabT()[i] != null)
			{
				tbT[i] = new Analyzer.TableNode(sl.tabT()[i].getFlag(), sl.tabT()[i].getName());
			}
		}
		this.tbNt = new Analyzer.TableNode[sl.tabNt().length];
		for (int i = 0; i < tbNt.length; i++)
		{
			if (sl.tabNt()[i] != null)
			{
				tbNt[i] = new Analyzer.TableNode(sl.tabNt()[i].getFlag(), sl.tabNt()[i].getName(), sl.tabNt()[i].getFirstNode());
			}
		}
		this.rootPath = rootPath;
	}

	public void export() throws IOException, FileNotFoundException
	{
		byte[] StbG = write(tbG);
		byte[] StbT = write(tbT);
		byte[] StbNt = write(tbNt);
		File dir = new File(rootPath, "export_code");
		if (!dir.exists())
		{
			dir.mkdir();
		}
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/Yytoken.txt"), new File(rootPath, "export_code/Yytoken.java"));
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/ParseStackNode.txt"), new File(rootPath, "export_code/ParseStackNode.java"));
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/SemanticRoutines.txt"), new File(rootPath, "export_code/SemanticRoutines.java"));
		File libDir = new File(rootPath, "export_code/lib");
		if (!libDir.exists())
		{
			libDir.mkdir();
		}
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/lib/groovy-1.5.6.jar"), new File(rootPath, "export_code/lib/groovy-1.5.6.jar"));
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/lib/asm-2.2.3.jar"), new File(rootPath, "export_code/lib/asm-2.2.3.jar"));
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/lib/antlr-2.7.6.jar"), new File(rootPath, "export_code/lib/antlr-2.7.6.jar"));
		IOUtilities.copyFileFromInputSteam(getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/lib/license.txt"), new File(rootPath, "export_code/lib/license.txt"));
		Project p = ProjectManager.getProject();
		File semFile = p.getSemFile();
		FileReader fr;
		BufferedReader br;
		String line = "";
		String result = "";
		fr = new FileReader(semFile);
		br = new BufferedReader(fr);
		while ((line = br.readLine()) != null)
		{
			if (line.contains("import org.grview.syntax.model.ParseNode"))
			{
				line = line.replace("import org.grview.syntax.model.ParseNode", "import org.grview.syntax.analyzer.gsll1.exportable.ParseNode");
			}
			result += line + "\n";
		}
		br.close();
		fr.close();
		FileWriter fw = new FileWriter(new File(rootPath, "export_code/srs.groovy"));
		fw.write(result);
		fw.close();
		line = "";
		result = "";
		File yylex = new File(rootPath, "generated_code/Yylex.java");
		if (yylex.exists())
		{
			result += "package org.grview.syntax.analyzer.gsll1.exportable;\n";
			fr = new FileReader(yylex);
			br = new BufferedReader(fr);
			while ((line = br.readLine()) != null)
			{
				if (line.contains("/*The line below can not be removed, otherwise the plugin do not works */"))
				{
				}
				else if (line.contains("import org.grview.lexical.*;"))
				{
				}
				else if (line.contains("import org.grview.syntax.model.*;"))
				{
				}
				else if (line.contains("TableNode"))
				{
					result += line.replace("TableNode", "Analyzer.TableNode") + "\n";
				}
				else if (line.matches(".*implements org.grview.lexical.Yylex.*"))
				{
					result += line.replaceFirst("implements org.grview.lexical.Yylex", "") + "\n";

				}
				else
				{
					result += line + "\n";
				}
			}
			br.close();
			fr.close();
			File yylex2 = new File(rootPath, "export_code/Yylex.java");
			if (!yylex2.exists())
				yylex2.createNewFile();
			fw = new FileWriter(yylex2);
			fw.write(result);
			fw.close();
		}

		InputStream af = getClass().getResourceAsStream("/org/grview/syntax/analyzer/gsll1/exportable/Analyzer.txt");
		StringReader sr = new StringReader(IOUtilities.readInputStreamAsString(af));
		br = new BufferedReader(sr);
		line = "";
		result = "";
		while ((line = br.readLine()) != null)
		{
			if (line.trim().replace("\t", "").startsWith("public final static byte[] StabGraph = new byte[0];"))
			{
				line = "public final static byte[] StabGraph = new byte[] {";
				for (byte b : StbG)
				{
					line += b + ", ";
				}
				line = line.substring(0, line.length() - 2);
				line += "};";
			}
			else if (line.trim().replace("\t", "").startsWith("public final static byte[] StabT = new byte[0];"))
			{
				line = "public final static byte[] StabT = new byte[] {";
				for (byte b : StbT)
				{
					line += b + ", ";
				}
				line = line.substring(0, line.length() - 2);
				line += "};";
			}
			else if (line.trim().replace("\t", "").startsWith("public final static byte[] StabNT = new byte[0];"))
			{
				line = "public final static byte[] StabNT = new byte[] {";
				for (byte b : StbNt)
				{
					line += b + ", ";
				}
				line = line.substring(0, line.length() - 2);
				line += "};";
			}
			line += "\n";
			result += line;
		}
		br.close();
		fr.close();
		File af2 = new File(rootPath, "export_code/Analyzer.java");
		if (!af2.exists())
			af2.createNewFile();
		fw = new FileWriter(af2);
		fw.write(result);
		fw.close();
	}

	public byte[] write(Object object)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			new ObjectOutputStream(bos).writeObject(object);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
}
