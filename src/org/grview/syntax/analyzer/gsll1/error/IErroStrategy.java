package org.grview.syntax.analyzer.gsll1.error;

import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.analyzer.gsll1.AnalyzerAlternative;
import org.grview.syntax.analyzer.gsll1.AnalyzerStackRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerTableRepository;
import org.grview.syntax.analyzer.gsll1.AnalyzerToken;
import org.grview.syntax.model.GrViewStack;
import org.grview.syntax.model.NTerminalStack;

public abstract class IErroStrategy
{
	protected AnalyzerTableRepository analyzerTable;
	protected AnalyzerStackRepository analyzerStack;

	protected AnalyzerToken oldToken;
	protected GrViewStack oldGrViewStack;
	protected NTerminalStack oldNTerminalStack;
	protected int oldTop;

	protected AnalyzerAlternative analyzerAlternative;
	protected AnalyzerToken analyzerToken;
	protected SemanticRoutinesRepo semanticRoutinesRepo;

	abstract int tryFix(int UI, int column, int line);

	protected void init()
	{
		this.analyzerTable = AnalyzerTableRepository.getInstance();
		this.analyzerStack = AnalyzerStackRepository.getInstance();
		this.analyzerAlternative = AnalyzerAlternative.getInstance();
		this.analyzerToken = AnalyzerToken.getInstance();
		this.semanticRoutinesRepo = SemanticRoutinesRepo.getInstance();
		oldGrViewStack = analyzerStack.getGrViewStack().clone();
		oldNTerminalStack = analyzerStack.getNTerminalStack().clone();
		oldTop = analyzerStack.getTop();
	}

	protected void restore(boolean restoreToken)
	{
		analyzerStack.setGrViewStack(oldGrViewStack);
		analyzerStack.setNTerminalStack(oldNTerminalStack);
		analyzerStack.setTop(oldTop);
		if (restoreToken)
		{
			analyzerToken.setCurrentToken(analyzerToken.getLastToken());
			analyzerToken.setCurrentSymbol(analyzerToken.getLastSymbol());
			analyzerToken.setCurrentSemanticSymbol(analyzerToken.getLastSemanticSymbol());
			analyzerToken.getYylex().pushback(analyzerToken.getYylex().yylength());
		}
	}
}
