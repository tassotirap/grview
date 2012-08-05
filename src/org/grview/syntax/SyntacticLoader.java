package org.grview.syntax;

import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.grview.syntax.model.TabGraphNode;
import org.grview.syntax.model.TabNode;
 /*
 * Created on 11/08/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author gohan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SyntacticLoader {
	String Flag;
	char Tipo;
	String Nomer;
	int NumNo;
	int AltR;
	int SucR;
	String SemR;
	int MaxT;
	int MaxNt;
	int IndPrim;
	int NoMax;

	TabGraphNode TabGraph[];
	TabNode TabT[];
	TabNode TabNt[];

	public TabGraphNode[] tabGraph() {
		return TabGraph;
	}

	public TabNode[] tabT() {
		return TabT;
	}

	public TabNode[] tabNt() {
		return TabNt;
	}

	public SyntacticLoader(TableCreate argTab) {
		MaxT = 0;
		MaxNt = 0;
		IndPrim = 1;
		NoMax = 0;
		TableCreate t = argTab;
		String tab[][] = t.getTab();
		TabT = new TabNode[t.getNLines() + 1];
		TabNt = new TabNode[t.getNLines() + 1];
		TabGraph = new TabGraphNode[t.getNLines() + 1];
		int registrosLidos;
		int iterator;
		int indicesTabNtEncontrados[] = new int[t.getNLines() + 1];
		int indiceEncontrado;
		/*2     Para todos os registros (linhas )presentes na tabela de entrada, faço: */
		for (registrosLidos = 0;
			registrosLidos < t.getNLines();
			registrosLidos++) {
			Flag = tab[registrosLidos][0];
			Tipo = tab[registrosLidos][1].toCharArray()[0];
			Nomer = tab[registrosLidos][2];
			NumNo = Integer.parseInt(tab[registrosLidos][3]);
			AltR = Integer.parseInt(tab[registrosLidos][4]);
			SucR = Integer.parseInt(tab[registrosLidos][5]);
			SemR = tab[registrosLidos][6];

			/* A tabela indicesTabNtEncontrados será util mais adiante, porém,
			   preciso inicializá-la, toda vez que eu mudar de registro*/
			for (int i = 0; i < indicesTabNtEncontrados.length; i++) {
				indicesTabNtEncontrados[i] = -1;
			}
			/*2         Se Tipo for igual Cabeça:*/
			if (Tipo == 'H') {
				/*Para facilitar a busca, faço todos os valores de indicesTabNtEncontrados serem iguais a -1 */

				/*2             Faço IndPrim <- IndPrim + NoMax, */
				IndPrim = IndPrim + NoMax;
				/*2             NoMax <- 0*/
				NoMax = 0;
				/*2             verifico se Nomer se encontra em TABNT*/
				/*iterador e aux serão uteis apenas para a localização de um simbolo na tabela de não terminais*/
				iterator = 1;
				int aux = 0;
				/*enquanto existir entradas em TABNT*/
				while (TabNt[iterator] != null) {
					/*Se eu encontrar um simbolo igual à Nomer...*/
					if ((TabNt[iterator].getName()).equals(Nomer)) {
						/*Guardo o indice do simbolo encontrado em uma tabela especial*/
						indicesTabNtEncontrados[aux] = iterator;
						aux = aux + 1;
					}
					iterator = iterator + 1;
				}
				/*2             se Nomer não se encontra na TABNT */
				if (indicesTabNtEncontrados[0] == -1) {
					MaxNt = MaxNt + 1;
					/*2             Coloco na TABNT um o não-terminal TabNT */
					TabNt[MaxNt] = new TabNode(Flag, Nomer, IndPrim);
				} else {
					/*2                 Se existe E tal que 1<=E<=MaxNt e TabNt[E].name() = Nomer */
					for (int j = 0; j < MaxNt; j++) {
						if (indicesTabNtEncontrados[j] != -1)
							/*2                         TabNt[E]->prim = 0 ? */
							if (TabNt[indicesTabNtEncontrados[j]].getPrim() == 0)
								/*2                             Se sim */
								TabNt[indicesTabNtEncontrados[j]].setPrim(IndPrim);
							else {
								/*2                             Se não*/
								System.out.println(
									"Erro!!Duas cabeças para um mesmo não-terminal");
							}
					}
				} // } else {...
			} //if(Tipo == 'H')...
			/*2         Se Tipo for diferente de H*/
			else {
				/*2             I <- IndPrim + NumNo -1 */
				int I = IndPrim + NumNo - 1;
				/*O nó que será inserido em TabGraph é criado agora*/
				TabGraph[I] = new TabGraphNode();
				/*IndiceEncontrado será utilizado na localização de uma simbolo na Tabela de simbolos Terminais*/
				indiceEncontrado = -1;
				/*2             se Tipo for igual a T e Nomer não for um lambda-nó*/
				if (Tipo == 'T') {
					TabGraph[I].setTerm(true);

					if (!Nomer.equals(new String("-1")) && !Nomer.equals(SyntaxDefinitions.EmptyNodeLabel)) {
						iterator = 1;
						/*2                 verifico se Nomer se encontra em TABT*/
						while (TabT[iterator] != null) {
							if ((TabT[iterator].getName()).equals(Nomer)) {
								indiceEncontrado = iterator;
								break;
							}
							iterator = iterator + 1;
						}
						if (indiceEncontrado == -1) {
							/*2                     Se não foi encontrado, a entrada na tabela de simbolos terminais terá de ser criada*/
							MaxT = MaxT + 1;
							TabT[MaxT] = new TabNode(Flag, Nomer);
							indiceEncontrado = MaxT;
						}
					}
				}
				/*2             Se Tipo for igoal a N*/
				else if (Tipo == 'N') {
					iterator = 1;
					/*2                 verifico se Nomer se encontra em TABT*/
					while (TabNt[iterator] != null) {
						if ((TabNt[iterator].getName()).equals(Nomer)) {
							indiceEncontrado = iterator;
							break;
						}
						iterator = iterator + 1;
					}
					if (indiceEncontrado == -1) {
						/*2                     Se não for encontrado, uma entrada na tabela TabNt, será criada*/
						MaxNt = MaxNt + 1;
						TabNt[MaxNt] = new TabNode(Flag, Nomer, 0);
						indiceEncontrado = MaxNt;
					}
					TabGraph[I].setTerm(false);
				}
				if (Nomer.equals("-1") || Nomer.equals(SyntaxDefinitions.EmptyNodeLabel)) {
					TabGraph[I].setSim(0);
				} else {
					TabGraph[I].setSim(indiceEncontrado);
				}
				if (AltR != 0) {
					TabGraph[I].setAlt(IndPrim + AltR - 1);
				} else {
					TabGraph[I].setAlt(0);
				}
				if (SucR != 0) {
					TabGraph[I].setSuc(IndPrim + SucR - 1);
				} else {
					TabGraph[I].setSuc(0);
				}
				/*Colocando o valor da rotina semantica*/
				TabGraph[I].setSem(SemR);
				
				if (NoMax < NumNo)
					NoMax = NumNo;
			}
		} //while (registrosLidos < t.linhas()) ...
	} //public CarregadorSintatico() ...	
}
