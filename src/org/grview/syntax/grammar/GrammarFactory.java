package org.grview.syntax.grammar;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.grview.output.AppOutput;
import org.grview.output.HtmlViewer.TOPIC;
import org.grview.syntax.command.AsinEditor;
import org.grview.syntax.grammar.model.AbstractNode;
import org.grview.syntax.grammar.model.LambdaAlternative;
import org.grview.syntax.grammar.model.NodeLabel;
import org.grview.syntax.grammar.model.SimpleNode;
import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.grview.syntax.grammar.model.SyntaxModel;
import org.grview.syntax.grammar.model.SyntaxSubpart;


/** Compiles a grammar textual representation from the designed graph **/
public class GrammarFactory {

	private SyntaxModel ld;
	private int cont;
	private String hout; //html formated output
	private Grammar absGrammar;

	/**
	 * Constructor
	 * @param part 
	 */
	public GrammarFactory(AsinEditor part) {
		ld = part.getLogicDiagram();
	}

	protected boolean calculateEnabled() {
		return canPerformAction();
	}

	private boolean canPerformAction() {
		return true;
	}

	PrintWriter out;
	String path = new String("");

	public String run(boolean isFile) throws Exception{
		StringBuffer grammar = new StringBuffer();
		if (isFile) {
		/*Display display = Display.getDefault();
		FileDialog dialog = new FileDialog(, SWT.SAVE);
		String name = dialog.open();
		path = name;		
		
		
		/*para cima é so teste*/
		/*try {
			FileWriter fileOut = new FileWriter(path);
			out = new PrintWriter(new BufferedWriter(fileOut));
		} catch (IOException e) {
			AppOutput.displayText("File writer exception.....");
			out = null;
		}*/
		}
		List children;
		ArrayList start = new ArrayList();
		children = ld.getChildrenNodes();
		Object o;
		
		AppOutput.clearGeneratedGrammar();
		AppOutput.displayHorizontalLine(TOPIC.Output);
		AppOutput.displayText("<a>Run grammar generate...</a><br>", TOPIC.Output);
		/*Com o laço a seguir colocarei todos os nós LeftSide, que apontam para o começo
		 * de uma produção no vetor start. Esse vetor então guardará todos as cabeças das
		 * produções.*/
		for (int i = 0; i < children.size(); i++) {
			o = children.get(i);
			/*Colocando false no flag, para fazer a busca em profundidade*/
			if (o instanceof SyntaxSubpart)
				 ((SyntaxSubpart) o).setFlag(false);
			/*Se o nó for cabeça de uma produção, coloco-o no vetor start.*/
			if (o instanceof AbstractNode && ((AbstractNode)o).getType().equals(AbstractNode.LEFTSIDE)) {
				start.add(o);
			}
			if (o instanceof AbstractNode && ((AbstractNode)o).getType().equals(AbstractNode.START)) {
				start.add(0, (o));
			}
		}
		SyntaxSubpart first = null;
		for (int i = 0; i < start.size(); i++) {
			cont = 0;
			SyntaxModel headCandidate = ((SyntaxModel)start.get(i));
			if (headCandidate == null) {
				throw new Exception("Could not find the grammar start node.");
			}
			first = headCandidate.getSucessor();
			if (first == null) {
				throw new Exception("Could not find the successor node of the grammar head.");
			}
			first.setNumber(++cont);
			List listChildren = ((SyntaxModel) start.get(i)).getChildrenAsLabels();
			NodeLabel label = (NodeLabel) listChildren.iterator().next();
			
			AppOutput.displayText("<a>>>Begining a new leftside..." + label.getLabelContents() + "</a><br>", TOPIC.Output);
			/*preparando a string 'sout', que será escrita no arquivo*/
			hout = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"1px\" width=\"100%\">";
			hout += "<tr style=\"background-color: #EEEEEE; font-weight: bold;\"><td></td><td>Node</td><td>Number</td><td>Alternative</td><td>Successor</td><td>Semantic Rout.</td></tr>";
			if (((AbstractNode)start.get(i)).getType().equals(AbstractNode.START)) {
				hout += "<tr><td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_s2.png\" alt=\"Initial Node\"></td><td style=\"font-weight: bold;\" ><a href=\"" + ((SyntaxModel)start.get(i)).getID() + "\">" + label.getLabelContents() + "</a></td><td align=\"center\">-1</td><td align=\"center\">-</td><td align=\"center\">-</td><td align=\"center\">-</td></tr>";
				GrComp gh = new GrComp(label.getLabelContents(), ((SyntaxModel)start.get(i)).getID());
				gh.setHead(true);
				if (i == 0) //it's the first found
					absGrammar = new Grammar(gh);
				absGrammar.setHead(gh);
				absGrammar.setCurrent(gh);
			}
			else {
				hout += "<tr><td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_H2.png\" alt=\"Left Side\"></td><td style=\"font-weight: bold;\" ><a href=\"" + ((SyntaxModel)start.get(i)).getID() + "\">" + label.getLabelContents() + "</a></td><td align=\"center\">-1</td><td align=\"center\">-</td><td align=\"center\">-</td><td align=\"center\">-</td></tr>";
				GrComp gl = new GrComp(label.getLabelContents(), ((SyntaxModel)start.get(i)).getID());
				gl.setLeftHand(true);
				if (i == 0)
					absGrammar = new Grammar(gl);
				absGrammar.addLeftHand(gl);
				absGrammar.setCurrent(gl);
			}
			String sout = ((SyntaxModel)start.get(i)).getID() + " H " + label.getLabelContents() + " -1 -1 -1 -1\n";
			if (isFile && out != null) {
				out.write(sout);
				out.flush();
			}
			grammar.append(sout);
			/*chamando a busca em profundidade para o primeiro nó da produção*/
			String[] name = nameAndRsExtractor((SyntaxModel) first);
			GrComp firstGuy = new GrComp(name[0], name[2]); 
			if (((AbstractNode)first).getType().equals(AbstractNode.NTERMINAL)) {
				firstGuy.setNonterminal(true);
			}
			else if (((AbstractNode)first).getType().equals(AbstractNode.TERMINAL)) {
				firstGuy.setTerminal(true);
			}
			else if (((AbstractNode)first).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE)) {
				firstGuy.setLambda(true);
			}
			absGrammar.addSuccessor(firstGuy);
			grammar.append(dfs(isFile, first));
			hout += "</table>";
			AppOutput.displayGeneratedGrammar(hout);
		}
		absGrammar.finalize();
		return grammar.toString();
	}

	public String dfs(boolean isFile, SyntaxSubpart o) {
		StringBuffer grammar = new StringBuffer();
		String name_smRoutine[];
		name_smRoutine = new String[2];
		name_smRoutine[0] = new String("-1");
		name_smRoutine[1] = new String("-1");
		if (o instanceof SyntaxModel ||o instanceof LambdaAlternative ) {
			/*Marcando o nó, para que ele não seja percorrido mais de uma vez.*/
			o.setFlag(true);
			/*O Sucessor do nó atual (o) é preenchido*/
			SyntaxSubpart suc = o.getSucessor();
			/*A alternativa do nó atual é preenchida*/
			SyntaxSubpart alt = o.getAlternative();
			GrComp thisGuy = new GrComp();
			
			
			/*se o sucessor existir e ainda não foi percorrido, ganha um número
			 * de indentificação*/
			if (suc != null && suc.getFlag() == false) {
				suc.setNumber(++cont);
			}
			/*se o nó alternativo existir e ainda não foi percorrido, ganha um número 
			 * de indentificação*/
			if (alt != null && alt.getFlag() == false) {
				alt.setNumber(++cont);
			}
			/*inicializando a string que será utilizada para guardar os dados do nó atual*/
			String sout = "";
			hout = hout + "<tr>";
			
			/*Nesse ponto temos que entrar no conteúdo do nó terminal ou não terminal,
			 * para saber qual é a sua entrada. Com esse dado, preenchere-mos o campo 
			 * nomer*/
			name_smRoutine = nameAndRsExtractor((SyntaxModel) o);
			
			/*O nó é um não terminal?*/
			if (o instanceof AbstractNode 
					&& ((AbstractNode)o).getType().equals(AbstractNode.NTERMINAL)) {
				sout = sout + name_smRoutine[2] + " N";
				hout = hout + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_nt2.png\" alt=\"Non-Terminal\"></td>";
				thisGuy = new GrComp(name_smRoutine[0], name_smRoutine[2]);
				thisGuy.setNonterminal(true);
			}
			else if (o instanceof AbstractNode 
					&& ((AbstractNode)o).getType().equals(AbstractNode.TERMINAL)){
				sout = sout + name_smRoutine[2] + " T";
				hout = hout + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_t2.png\" alt=\"Terminal\"></td>";
				thisGuy = new GrComp(name_smRoutine[0], name_smRoutine[2]);
				thisGuy.setTerminal(true);
			}
			else if (o instanceof AbstractNode 
					&& ((AbstractNode)o).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE)) {
				sout = sout + name_smRoutine[2] + " T";
				hout = hout + "<td style=\"background-color: #EEEEEE;\"><img src=\"images/icon_l.png\" alt=\"Lambda Alternative\"></td>";
				thisGuy = new GrComp(null, name_smRoutine[2]);
				thisGuy.setLambda(true);
			}
			
			sout = sout + " " + name_smRoutine[0];
			hout = hout + "<td style=\"font-weight: bold;\"><a href=\"" + name_smRoutine[2] + "\">" + name_smRoutine[0] + "</a></td>";
			
			/*Agora está na hora de preencher o campo numno, que indica qual o número desse nó*/
			sout = sout + " " + o.getNumber();
			hout = hout + "<td align=\"center\">" + o.getNumber() + "</td>";
			
			/*Colocando um valor no campo Altr, que indica o nó alternativo ao nó "o".*/
			if (alt == null ) {
				sout = sout + " 0";
				hout = hout + "<td align=\"center\">-</td>";
			}
			else {
				sout = sout + " " + alt.getNumber();
				hout = hout + "<td align=\"center\">" + alt.getNumber() + "</td>";
			}
			/*Colocando um valor no campo Sucr, que indica o nó sucessor ao nó "o"*/
			if (suc == null ) {
				sout = sout + " 0";
				hout = hout + "<td align=\"center\">-</td>";
			}
			else {
				sout = sout + " " + suc.getNumber();
				hout = hout + "<td align=\"center\">" + suc.getNumber() + "</td>";
			}
				
			/*Este número é referente à rotina semântica.*/
			sout = sout + " "+name_smRoutine[1]+"\n";
			hout = hout + "<td align=\"center\">" + ((name_smRoutine[1].equals("-1"))?"-":"<a href=\"name_smRoutine[1]\">" + name_smRoutine[1] + "</a>") + "</td>";
			
			/*imprimindo os dados do nó atual (o) no arquivo...*/
			if (out != null && isFile) {
				out.write(sout);
				out.flush();
			}
			grammar.append(sout);
			/*imprimindo os dados do nó atual (o) na tela...*/
			hout += "</tr>";
			
			/*chamando a recursão para o nó sucessor (busca em profundidade)*/
			absGrammar.setCurrent(thisGuy);
			if (suc != null && suc.getFlag() == false) {
				String[] name = nameAndRsExtractor((SyntaxModel) suc);
				GrComp nextGuy = new GrComp(name[0], name[2]);
				if (((AbstractNode)suc).getType().equals(AbstractNode.NTERMINAL)) {
					nextGuy.setNonterminal(true);
				}
				else if (((AbstractNode)suc).getType().equals(AbstractNode.TERMINAL)) {
					nextGuy.setTerminal(true);
				}
				else if (((AbstractNode)suc).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE)) {
					nextGuy.setLambda(true);
				}
				absGrammar.addSuccessor(nextGuy);
				grammar.append(dfs(isFile,suc));
			}
			/*chamando a recursão para o nó alternativo (busca em profundidade)*/
			if (alt != null && alt.getFlag() == false) {
				String[] name = nameAndRsExtractor((SyntaxModel) alt);
				GrComp nextGuy = new GrComp(name[0], name[2]); 
				if (((AbstractNode)alt).getType().equals(AbstractNode.NTERMINAL)) {
					nextGuy.setNonterminal(true);
				}
				else if (((AbstractNode)alt).getType().equals(AbstractNode.TERMINAL)) {
					nextGuy.setTerminal(true);
				}
				else if (((AbstractNode)alt).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE)) {
					nextGuy.setLambda(true);
				}
				absGrammar.addAlternative(nextGuy);
				grammar.append(dfs(isFile, alt));
			}
		}
		return grammar.toString();
	}
	/*This function extracts the name and the routine semantic number from one string*/
	private String[] nameAndRsExtractor(SyntaxModel sm){
		String name;
		String sr;
		String[] str = new String[2];
		String[] ret = new String[3];
		SimpleNode sn;
		List<NodeLabel> listChildren = sm.getChildrenAsLabels();
		NodeLabel label = listChildren.iterator().next();
		str = label.getLabelContents().split("#");
		if (sm instanceof AbstractNode &&
				((AbstractNode) sm).getType().equals(AbstractNode.LAMBDA_ALTERNATIVE)) {
			name = SyntaxDefinitions.EmptyNodeLabel;
		}
		else {
			name = str[0];
		}
		if(str.length >= 2){
			sr = str[1];
		}
		else if ((sn = sm.getSemanticNode()) != null) {
			sr = sn.getLabel().getLabelContents();
		}
		else {
			sr = "-1";
		}
		ret[0]= new String(name);
		ret[1]= new String(sr);
		ret[2]= sm.getID();
		return ret;
	}

	public Grammar getAbsGrammar() {
		return absGrammar;
	}

	public void setAbsGrammar(Grammar absGrammar) {
		this.absGrammar = absGrammar;
	}

}