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
	 * A variavel a seguir, contém o indice do nó alternativo à esse nó
	 */
	private int alt;
	/* rotina semantica desse nó */
	private String sem;
	/*
	 * A variável abaixo contém uma referencia para este nó na tabela de
	 * simbolos terminais ou não terminais.
	 */
	private int sim;
	/*
	 * A variavel a seguir, contém o indice do nó sucessor à esse nó
	 */
	private int suc;
	/*
	 * Quando a variável abaixo for true, node se trata de um nó terminal
	 */
	private boolean term;

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

	/* atribui um valor à term */
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
