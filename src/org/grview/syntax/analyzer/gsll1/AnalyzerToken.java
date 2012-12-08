package org.grview.syntax.analyzer.gsll1;

import java.io.IOException;

import org.grview.lexical.Yylex;
import org.grview.lexical.Yytoken;
import org.grview.output.AppOutput;

public class AnalyzerToken
{	
	private Yytoken currentToken;
	
	private String currentSemanticSymbol;
	private String lastSemanticSymbol;
	private String currentSymbol;
	private String lastSymbol;	
	private Yylex yylex;

	private Yytoken lastToken;
	
	private static AnalyzerToken instance;
	
	public static AnalyzerToken getInstance()
	{
		return instance;
	}
	
	public static AnalyzerToken setInstance(Yylex yylex)
	{
		instance = new AnalyzerToken(yylex);
		return instance;
	}
	
	private AnalyzerToken(Yylex yylex)
	{
		this.yylex = yylex;
	}
	
	public String getCurrentSemanticSymbol()
	{
		return currentSemanticSymbol;
	}

	public String getCurrentSymbol()
	{
		return currentSymbol;
	}

	public Yytoken getCurrentToken()
	{
		return currentToken;
	}

	public String getLastSymbol()
	{
		return lastSymbol;
	}

	public Yylex getYylex()
	{
		return yylex;
	}

	public void readNext()
	{
		try
		{
			setLastToken(getCurrentToken());
			
			setLastSymbol(getCurrentSymbol());
			setLastSemanticSymbol(getCurrentSemanticSymbol());
			
			setCurrentToken(getYylex().yylex());
			
			if (getCurrentToken().type.equals("Res") || getCurrentToken().type.equals("Esp") || getCurrentToken().type.equals("EOF"))
			{
				setCurrentSymbol(getCurrentToken().text);
			}
			else
			{
				setCurrentSymbol(getCurrentToken().type);
			}
			
			setCurrentSemanticSymbol(getCurrentToken().text);
		}
		catch (IOException e)
		{
			AppOutput.printlnToken("Token read error\n");
		}
		AppOutput.printToken("Current Token: " + getCurrentToken());
	}

	private void setLastToken(Yytoken token)
	{
		this.lastToken = token;		
	}

	public void setCurrentSemanticSymbol(String currentSemanticSymbol)
	{
		this.currentSemanticSymbol = currentSemanticSymbol;
	}

	public void setCurrentSymbol(String currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}

	public void setCurrentToken(Yytoken currentToken)
	{
		this.currentToken = currentToken;
	}

	public void setLastSymbol(String lastSymbol)
	{
		this.lastSymbol = lastSymbol;
	}

	public Yytoken getLastToken()
	{
		return lastToken;
	}

	public String getLastSemanticSymbol()
	{
		return lastSemanticSymbol;
	}
	
	public void setLastSemanticSymbol(String lastSemanticSymbol)
	{
		this.lastSemanticSymbol = lastSemanticSymbol;
	}

}
