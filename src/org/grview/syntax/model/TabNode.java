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
 * 
 * @author gohan
 */
public class TabNode {
	private String flag;
	/*nome do nó*/
	private String name;
	/*Só utilizado em nós não terminais, onde prim aponta para 
	 * o primeiro nó do não terminal*/
	private int prim;

	/*Construtuores*/
	/*utilizado para construir terminais*/
	public TabNode(String flag, String nodeName) {
		this.flag = flag;
		getName(nodeName);
		setPrim(-1);
	}
	/*utilizado para construir não terminais*/
	public TabNode(String flag, String nodeName, int nodePrim) {
		this.flag = flag;
		getName(nodeName);
		setPrim(nodePrim);
	}

	/*Atribui o nome presente em nodeName ao nome desse nó*/
	public void getName(String nodeName) {
		name = nodeName;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getFlag() {
		return flag;
	}
	
	/*retorna o nome desse nó*/
	public String getName() {
		return name;
	}

	/*Atribui o nó presente em nodePrim em prim desse nó*/
	public void setPrim(int nodePrim) {
		prim = nodePrim;
	}

	/*retorna o prim desse nó*/
	public int getPrim() {
		return prim;
	}
	
	/*to string...*/
	@Override
	public String toString(){
		return this.name;
	}
}
