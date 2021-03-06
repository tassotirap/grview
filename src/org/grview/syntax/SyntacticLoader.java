package org.grview.syntax;

import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.grview.syntax.model.TableGraphNode;
import org.grview.syntax.model.TableNode;

/*
 * Created on 11/08/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author gohan
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SyntacticLoader
{
	int AltR;
	String Flag;
	int IndPrim;
	int MaxNt;
	int MaxT;
	int NoMax;
	String Nomer;
	int NumNo;
	String SemR;
	int SucR;
	TableGraphNode TabGraph[];

	TableNode TabNt[];
	TableNode TabT[];
	char Tipo;

	public SyntacticLoader(TableCreate argTab)
	{
		MaxT = 0;
		MaxNt = 0;
		IndPrim = 1;
		NoMax = 0;
		TableCreate t = argTab;
		String tab[][] = t.getTab();
		TabT = new TableNode[t.getNLines() + 1];
		TabNt = new TableNode[t.getNLines() + 1];
		TabGraph = new TableGraphNode[t.getNLines() + 1];
		int registrosLidos;
		int iterator;
		int indicesTabNtEncontrados[] = new int[t.getNLines() + 1];
		int indiceEncontrado;
		/*
		 * 2 Para todos os registros (linhas )presentes na tabela de entrada,
		 * fa�o:
		 */
		for (registrosLidos = 0; registrosLidos < t.getNLines(); registrosLidos++)
		{
			Flag = tab[registrosLidos][0];
			Tipo = tab[registrosLidos][1].toCharArray()[0];
			Nomer = tab[registrosLidos][2];
			NumNo = Integer.parseInt(tab[registrosLidos][3]);
			AltR = Integer.parseInt(tab[registrosLidos][4]);
			SucR = Integer.parseInt(tab[registrosLidos][5]);
			SemR = tab[registrosLidos][6];

			/*
			 * A tabela indicesTabNtEncontrados ser� util mais adiante, por�m,
			 * preciso inicializ�-la, toda vez que eu mudar de registro
			 */
			for (int i = 0; i < indicesTabNtEncontrados.length; i++)
			{
				indicesTabNtEncontrados[i] = -1;
			}
			/* 2 Se Tipo for igual Cabe�a: */
			if (Tipo == 'H')
			{
				/*
				 * Para facilitar a busca, fa�o todos os valores de
				 * indicesTabNtEncontrados serem iguais a -1
				 */

				/* 2 Fa�o IndPrim <- IndPrim + NoMax, */
				IndPrim = IndPrim + NoMax;
				/* 2 NoMax <- 0 */
				NoMax = 0;
				/* 2 verifico se Nomer se encontra em TABNT */
				/*
				 * iterador e aux ser�o uteis apenas para a localiza��o de um
				 * simbolo na tabela de n�o terminais
				 */
				iterator = 1;
				int aux = 0;
				/* enquanto existir entradas em TABNT */
				while (TabNt[iterator] != null)
				{
					/* Se eu encontrar um simbolo igual � Nomer... */
					if ((TabNt[iterator].getName()).equals(Nomer))
					{
						/*
						 * Guardo o indice do simbolo encontrado em uma tabela
						 * especial
						 */
						indicesTabNtEncontrados[aux] = iterator;
						aux = aux + 1;
					}
					iterator = iterator + 1;
				}
				/* 2 se Nomer n�o se encontra na TABNT */
				if (indicesTabNtEncontrados[0] == -1)
				{
					MaxNt = MaxNt + 1;
					/* 2 Coloco na TABNT um o n�o-terminal TabNT */
					TabNt[MaxNt] = new TableNode(Flag, Nomer, IndPrim);
				}
				else
				{
					/*
					 * 2 Se existe E tal que 1<=E<=MaxNt e TabNt[E].name() =
					 * Nomer
					 */
					for (int j = 0; j < MaxNt; j++)
					{
						if (indicesTabNtEncontrados[j] != -1)
							/* 2 TabNt[E]->prim = 0 ? */
							if (TabNt[indicesTabNtEncontrados[j]].getFirstNode() == 0)
								/* 2 Se sim */
								TabNt[indicesTabNtEncontrados[j]].setFirstNode(IndPrim);
							else
							{
								/* 2 Se n�o */
								System.out.println("Erro!!Duas cabe�as para um mesmo n�o-terminal");
							}
					}
				} // } else {...
			} // if(Tipo == 'H')...
			/* 2 Se Tipo for diferente de H */
			else
			{
				/* 2 I <- IndPrim + NumNo -1 */
				int I = IndPrim + NumNo - 1;
				/* O n� que ser� inserido em TabGraph � criado agora */
				TabGraph[I] = new TableGraphNode();
				/*
				 * IndiceEncontrado ser� utilizado na localiza��o de uma simbolo
				 * na Tabela de simbolos Terminais
				 */
				indiceEncontrado = -1;
				/* 2 se Tipo for igual a T e Nomer n�o for um lambda-n� */
				if (Tipo == 'T')
				{
					TabGraph[I].setIsTerminal(true);

					if (!Nomer.equals(new String("-1")) && !Nomer.equals(SyntaxDefinitions.EmptyNodeLabel))
					{
						iterator = 1;
						/* 2 verifico se Nomer se encontra em TABT */
						while (TabT[iterator] != null)
						{
							if ((TabT[iterator].getName()).equals(Nomer))
							{
								indiceEncontrado = iterator;
								break;
							}
							iterator = iterator + 1;
						}
						if (indiceEncontrado == -1)
						{
							/*
							 * 2 Se n�o foi encontrado, a entrada na tabela de
							 * simbolos terminais ter� de ser criada
							 */
							MaxT = MaxT + 1;
							TabT[MaxT] = new TableNode(Flag, Nomer);
							indiceEncontrado = MaxT;
						}
					}
				}
				/* 2 Se Tipo for igoal a N */
				else if (Tipo == 'N')
				{
					iterator = 1;
					/* 2 verifico se Nomer se encontra em TABT */
					while (TabNt[iterator] != null)
					{
						if ((TabNt[iterator].getName()).equals(Nomer))
						{
							indiceEncontrado = iterator;
							break;
						}
						iterator = iterator + 1;
					}
					if (indiceEncontrado == -1)
					{
						/*
						 * 2 Se n�o for encontrado, uma entrada na tabela TabNt,
						 * ser� criada
						 */
						MaxNt = MaxNt + 1;
						TabNt[MaxNt] = new TableNode(Flag, Nomer, 0);
						indiceEncontrado = MaxNt;
					}
					TabGraph[I].setIsTerminal(false);
				}
				if (Nomer.equals("-1") || Nomer.equals(SyntaxDefinitions.EmptyNodeLabel))
				{
					TabGraph[I].setNodeReference(0);
				}
				else
				{
					TabGraph[I].setNodeReference(indiceEncontrado);
				}
				if (AltR != 0)
				{
					TabGraph[I].setAlternativeIndex(IndPrim + AltR - 1);
				}
				else
				{
					TabGraph[I].setAlternativeIndex(0);
				}
				if (SucR != 0)
				{
					TabGraph[I].setSucessorIndex(IndPrim + SucR - 1);
				}
				else
				{
					TabGraph[I].setSucessorIndex(0);
				}
				/* Colocando o valor da rotina semantica */
				TabGraph[I].setSemanticRoutine(SemR);

				if (NoMax < NumNo)
					NoMax = NumNo;
			}
		} // while (registrosLidos < t.linhas()) ...
	} // public CarregadorSintatico() ...

	public TableGraphNode[] tabGraph()
	{
		return TabGraph;
	}

	public TableNode[] tabNt()
	{
		return TabNt;
	}

	public TableNode[] tabT()
	{
		return TabT;
	}
}
