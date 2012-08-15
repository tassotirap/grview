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
 */
public class TabNode
{
	private String flag;
	/* nome do n� */
	private String name;
	/*
	 * S� utilizado em n�s n�o terminais, onde prim aponta para o primeiro n� do
	 * n�o terminal
	 */
	private int prim;

	/* Construtuores */
	/* utilizado para construir terminais */
	public TabNode(String flag, String nodeName)
	{
		this.flag = flag;
		getName(nodeName);
		setPrim(-1);
	}

	/* utilizado para construir n�o terminais */
	public TabNode(String flag, String nodeName, int nodePrim)
	{
		this.flag = flag;
		getName(nodeName);
		setPrim(nodePrim);
	}

	public String getFlag()
	{
		return flag;
	}

	/* retorna o nome desse n� */
	public String getName()
	{
		return name;
	}

	/* Atribui o nome presente em nodeName ao nome desse n� */
	public void getName(String nodeName)
	{
		name = nodeName;
	}

	/* retorna o prim desse n� */
	public int getPrim()
	{
		return prim;
	}

	public void setFlag(String flag)
	{
		this.flag = flag;
	}

	/* Atribui o n� presente em nodePrim em prim desse n� */
	public void setPrim(int nodePrim)
	{
		prim = nodePrim;
	}

	/* to string... */
	@Override
	public String toString()
	{
		return this.name;
	}
}
