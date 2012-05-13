package org.grview.syntax;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
/**
 * @author gohan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TabCreate {
	
	private String tab[][];
	private BufferedReader in;
	private int nlines;
	
	public int getNLines(){
		return nlines;
	}

	public String[][] getTab() {
		return tab;
	}
	
	public void TabCreateFromFile(String grammarFile) {
		TabCreate(chargeFile(grammarFile));
	}
	
	public void TabCreateFromString(String grammar) {
		TabCreate(chargeString(grammar));
	}
	
	
	private void TabCreate(int nlines) {
		int lin;
		int col;
		int reading;
		char cReading;
		String str;
		this.nlines = nlines;
		/*A variável firstTime é utilizada para ignorar um conjunto de caracteres 
		 * em branco seguidos...
		 */
		boolean firstTime = false;
		tab = new String[nlines][7];
		lin = col = 0;
		try {
			/*lê o primeiro caractere */
			reading = in.read();
			cReading = (char) reading;
			str = new String();
			while (reading != -1) {
				//leitura = in.read();
				//cleitura = (char) leitura;
				if (cReading == ' ') {
					if (firstTime) {
						//System.out.print(str);
						tab[lin][col] = str;
						++col;
					}
					firstTime = false;
					str = new String();
				} else {
					if (cReading == '\n'|| cReading == '\r') {
						if (firstTime) {
							//System.out.print(str);
							tab[lin][col] = str;
							++lin;
							col = 0;
						}
						firstTime = false;
						str = new String();
					} else {
						str =
							str.concat(
								new String(Character.toString(cReading)));
						firstTime = true;
					}
				}
				reading = in.read();
				cReading = (char) reading;
			}
		} catch (IOException e) {
			System.out.println("\nERRO NA LEITURA\n");
		}
	}
	
	public TabCreate(String grammar, boolean isFile) {
		if (isFile) {
			TabCreateFromFile(grammar);
		}
		else {
			TabCreateFromString(grammar);
		}
	}

	private int chargeString(String grammar) {
		BufferedReader aux = null;
		int nLines = 0;
		in = new BufferedReader(new StringReader(grammar));
		aux = new BufferedReader(new StringReader(grammar));
		try {
			while(aux.readLine() != null) ++nLines;
		}
		catch (IOException e) {
			System.out.println("Erro, leitura de string");
		}
		return nLines;
	}
	private int chargeFile(String grammarFile) {
		File fileIn = null;
		BufferedReader aux = null;
		int nLines = 0;
		String path = grammarFile;
		fileIn = new File(path);
		try {
			in = new BufferedReader(new FileReader(fileIn));
			aux = new BufferedReader(new FileReader(fileIn));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		nLines = 0;
		try {
			if (aux != null) {
				while (aux.readLine() != null)
					++nLines;
			}
		} catch (IOException e) {
			System.out.println("Erro de leitura");
		}
		return nLines;
	}
}
