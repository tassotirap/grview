package org.grview.lexical;

import java.io.File;

import org.grview.util.Log;
import org.grview.util.dynacode.DynaCode;



public class YyFactory {
	
	private static YyFactory instance;
	
	private YyFactory() {
		
	}
	
	public static YyFactory getInstance() {
		if (instance == null) {
			instance = new YyFactory();
		}
		return instance;
	}
	public static void createYylex(String baseDir, String path, String scanner) {
		try {
			File targetDir = new File(baseDir + "/" + path);
			if (!targetDir.exists()) {
				if (!targetDir.mkdir()) {
					Log.log(Log.ERROR, null, "Could not generate Yylex.java");
					return;
				}
			}
			String[] arguments = new String[] {"-d", baseDir + "/" + path, scanner};
			JFlex.Main.main(arguments);
		}
	catch (Exception e) {
		e.printStackTrace();
	}
	}
	
	public static Yylex getYylex(String baseDir, String path, java.io.Reader in) {
		DynaCode dynacode = new DynaCode();
		dynacode.addSourceDir(new File(baseDir + "/"), new File("dynacode"));
		Yylex yl = (Yylex) dynacode.newProxyInstance(Yylex.class,
				(path == null) ? "Yylex" : path + ".Yylex");
		yl.setReader(in);
		return yl;
	}
}
