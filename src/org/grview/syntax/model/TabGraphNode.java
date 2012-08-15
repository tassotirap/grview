package org.grview.syntax.model;

/*
 * Created on 11/08/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * @author gohan
 * 
 */
public class TabGraphNode
{
	/*
	 * Quando a vari�vel abaixo for true, node se trata de um n� terminal
	 */
	private boolean term;
	/*
	 * A vari�vel abaixo cont�m uma referencia para este n� na tabela de
	 * simbolos terminais ou n�o terminais.
	 */
	private int sim;
	/*
	 * A variavel a seguir, cont�m o indice do n� alternativo � esse n�
	 */
	private int alt;
	/*
	 * A variavel a seguir, cont�m o indice do n� sucessor � esse n�
	 */
	private int suc;
	/* rotina semantica desse n� */
	private String sem;

	public TabGraphNode()
	{

	}

	/* retorna o valor da alternativa */
	public int getAlt()
	{
		return alt;
	}

	/* retorna o valor de sem */
	public String getSem()
	{
		return sem;
	}

	/* retorna o valor de sim */
	public int getSim()
	{
		return sim;
	}

	/* retorna o valor de suc */
	public int getSuc()
	{
		return suc;
	}

	/* retorna o valor de term */
	public boolean isTerm()
	{
		return term;
	}

	/* atribui um valor a alt */
	public void setAlt(int node)
	{
		alt = node;
	}

	/* atribui um valor a sem */
	public void setSem(String routine)
	{
		sem = routine;
	}

	/* atribui um valor a sim */
	public void setSim(int node)
	{
		sim = node;
	}

	/* atribui um valor a suc */
	public void setSuc(int node)
	{
		suc = node;
	}

	/* atribui um valor � term */
	public void setTerm(boolean bool)
	{
		term = bool;
	}

	@Override
	public String toString()
	{
		return term + " " + sim + " " + alt + " " + suc + " " + sem;
	}

}
