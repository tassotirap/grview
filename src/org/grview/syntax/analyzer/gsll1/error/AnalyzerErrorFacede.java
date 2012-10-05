package org.grview.syntax.analyzer.gsll1.error;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.TableGraphNode;

public class AnalyzerErrorFacede
{

	private File fileIn;
	private AnalyzerTableRepository analyzerTable;

	private AnalyzerToken syntaxToken;	

	public AnalyzerErrorFacede(File fileIn)
	{
		this.fileIn = fileIn;
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.syntaxToken = AnalyzerToken.getInstance();
	}	

	public boolean dealWithError(int indexNode, int toppsIU, int column, int line)
	{
		int lastIndexNode = indexNode;

		try
		{
			if (fileIn != null)
			{

				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileIn));
				for (int j = 0; j < line; j++)
				{
					bufferedReader.readLine();
				}
				String wrongLine = bufferedReader.readLine();
				bufferedReader.close();
				AppOutput.displayText("\n" + wrongLine + "\n", TOPIC.Output);
			}

		}
		catch (IOException e)
		{
			if (fileIn != null)
			{
				AppOutput.displayText("File not found...", TOPIC.Output);
			}
		}
		AppOutput.displayText("<font color='red'>Error found at the symbol "+ syntaxToken.getCurrentToken().text +" of line: " + line + ", column: " + column + ". </font>", TOPIC.Output);

		Stack<TableGraphNode> nTerminalStack = new Stack<TableGraphNode>();

		while (indexNode != 0)
		{
			if (analyzerTable.getGraphNode(indexNode).IsTerminal())
			{
				AppOutput.displayText(analyzerTable.getTermial(analyzerTable.getGraphNode(indexNode).getNodeReference()).getName() + " expected.", TOPIC.Output);

				indexNode = analyzerTable.getGraphNode(indexNode).getAlternativeIndex();

				if (indexNode == 0 && nTerminalStack.size() > 0)
				{
					indexNode = nTerminalStack.pop().getAlternativeIndex();
				}

			}
			else
			{
				nTerminalStack.push(analyzerTable.getGraphNode(indexNode));
				indexNode = analyzerTable.getNTerminal(analyzerTable.getGraphNode(indexNode).getNodeReference()).getFirstNode();
			}
		}
		
		ArrayList<IErroStrategy> strategyList = new ArrayList<IErroStrategy>();
		strategyList.add(new DeleteStrategy());
		//strategyList.add(new InsertStrategy());
		//strategyList.add(new ChangeStrategy());
		//strategyList.add(new DelimiterSearchStrategy());
		
		boolean fix = false;
		
		for(IErroStrategy errorStrategy : strategyList)
		{
			fix = errorStrategy.tryFix(lastIndexNode, toppsIU, column, line);
			
			if(fix)
				break;			
		}
		
		if(!fix)
		{
			syntaxToken.readNext();
			if (syntaxToken.getCurrentToken().text.equals("$"))
			{
				return false;
			}
			else
			{
				dealWithError(lastIndexNode, toppsIU, syntaxToken.getCurrentToken().charBegin + 1, syntaxToken.getCurrentToken().line + 1);
			}					
		}
			
			/* Tenta corrigir o erro utilizando a estratégia de inserção */
			//if (!estrategiaInsercao(lastIndexNode, toppsIU, column, line))
			//{
				//
				/* Tenta corrigir o erro utilizando a estratégia da troca */
				//if (!estrategiaTroca(lastIndexNode, toppsIU, column, line))
				//{
					//AppOutput.errorRecoveryStatus("Replacing a symbol stategy has not succeeded\n");
					/*
					 * Tenta corrigir o erro utilizando a estratégia da busca de
					 * Delimitador
					 */
					//if (!estrategiaBuscaDelimitador(lastIndexNode, toppsIU, column, line))
					//{
						//AppOutput.errorRecoveryStatus("Searching delimiters strategy has not succeeded\n");
						/*
						 * Até esse ponto nenhuma das estratégias tiveram
						 * sucesso ao corrigir o erro, então o próximo simbolo
						 * da entrada é lido a rotina de tratamento de erros é
						 * chamada novamente. Dessa vez para esse novo simbolo
						 * lido.
						 */
						//syntaxToken.readNext();
						//if (syntaxToken.getCurrentToken().text.equals("$"))
						//{
						//	return false;
						//}
						//else
						//{
						//	dealWithError(lastIndexNode, toppsIU, syntaxToken.getCurrentToken().charBegin, syntaxToken.getCurrentToken().line);
						//}
					//}
				//}
			//}
		return true;
	}
}
