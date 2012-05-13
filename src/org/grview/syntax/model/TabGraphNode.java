package org.grview.syntax.model;
/*
 * Created on 11/08/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 *  * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @author gohan
 *
 */
public class TabGraphNode {
	/*Quando a variável abaixo for true, node se trata de um nó
	 * terminal*/
	private boolean term;
	/*A variável abaixo contém uma referencia para este nó na
	tabela de simbolos terminais ou não terminais. */
	private int sim;
	/* A variavel a seguir, contém o indice do nó alternativo 
	* à esse nó*/
	private int alt;
	/* A variavel a seguir, contém o indice do nó sucessor à 
	* esse nó*/
	private int suc;
	/*rotina semantica desse nó*/
	private String sem;
	
	public TabGraphNode(){
		
	}

	/*atribui um valor à term*/
	public void setTerm(boolean bool) {
		term = bool;
	}

	/*retorna o valor de term*/
	public boolean isTerm() {
		return term;
	}

	/*atribui um valor a sim*/
	public void setSim(int node) {
		sim = node;
	}

	/*retorna o valor de sim*/
	public int getSim() {
		return sim;
	}

	/*atribui um valor a alt*/
	public void setAlt(int node) {
		alt = node;
	}

	/*retorna o valor da alternativa*/
	public int getAlt() {
		return alt;
	}

	/*atribui um valor a suc*/
	public void setSuc(int node) {
		suc = node;
	}

	/*retorna o valor de suc*/
	public int getSuc() {
		return suc;
	}
	
	/*atribui um valor a sem*/
	public void setSem(String routine) {
		sem = routine;
	}

	/*retorna o valor de sem*/
	public String getSem() {
		return sem;
	}
	
	@Override
	public String toString(){
		return term+" "+sim+" "+alt+" "+suc+" "+sem;
	}

}
