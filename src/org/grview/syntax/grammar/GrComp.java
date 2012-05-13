package org.grview.syntax.grammar;

/** Just any component on a grammar; an abstract representation **/
public class GrComp {

	private boolean nonterminal;
	private boolean terminal;
	private boolean lambda;
	private boolean head;
	private boolean leftHand;
	private Object contents;
	private Object id;
	
	public GrComp() {}
	
	public GrComp(Object contents, Object id) {
		this.contents = contents;
		this.id = id;
	}
	
	public boolean isNonterminal() {
		return nonterminal;
	}
	public void setNonterminal(boolean nonterminal) {
		this.nonterminal = nonterminal;
	}
	public boolean isTerminal() {
		return terminal;
	}
	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
	}
	public boolean isLambda() {
		return lambda;
	}
	public void setLambda(boolean lambda) {
		this.lambda = lambda;
	}
	public boolean isHead() {
		return head;
	}
	public void setHead(boolean head) {
		this.head = head;
	}
	public boolean isLeftHand() {
		return leftHand;
	}
	public void setLeftHand(boolean leftHand) {
		this.leftHand = leftHand;
	}
	public void setContents(Object contents) {
		this.contents = contents;
	}
	public Object getContents() {
		return contents;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public Object getId() {
		return this.id;
	}
}
