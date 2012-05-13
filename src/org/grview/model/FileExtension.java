package org.grview.model;

public class FileExtension {

	public final static String TXT_FILE = ".txt";
	public final static String SEM_FILE = ".sem";
	public final static String GRAM_FILE = ".gram";
	public final static String LEX_FILE = ".lex";
	public final static String XML_FILE = ".xml";
	public final static String IN_FILE = ".in";
	public final static String OUT_FILE = ".out";
	public final static String JAVA_FILE = ".java";
	
	private String extension;
	
	public FileExtension(String extension) {
		this.extension = extension;
	}
	
	public String getExtension() {
		return extension;
	}
}
