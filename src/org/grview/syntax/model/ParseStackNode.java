/*
 * Created on 24/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.grview.syntax.model;

/**
 * @author Fernando
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/*ParseStackNode objects are pushed on the parseStack*/
public class ParseStackNode {
	/*The flag, a unique identifier for presentation and debug */
	private String flag;
	/*The type of the token*/
	private String syn;
	/*The semantic symbol of the token*/
	private Object sem;

	public ParseStackNode(String flag, String str){
		this(flag, str, null);
	}
	public ParseStackNode(String flag, String str,Object obj){
		this.flag = flag;
		syn = str;
		sem = obj;
	}
	public String getSyn() {
		return syn;
	}
	public void setSyn(String str) {
		syn = str;
	}
	public Object getSem() {
		return sem;
	}
	public String stringSem(){
		return sem.toString();
	}
	public int intSem(){
		return Integer.parseInt(stringSem());
	}	
	public void setSem(Object obj) {
		sem = obj;
	}
	public String getFlag() {
		return flag;
	}
	@Override
	public String toString(){
		return "Syn: "+getSyn()+" Sem: "+getSem();
	}

}