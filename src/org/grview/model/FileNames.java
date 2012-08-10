package org.grview.model;

public class FileNames {

	public final static String TXT_EXTENSION = ".txt";
	public final static String SEM_EXTENSION = ".sem";
	public final static String GRAM_EXTENSION = ".gram";
	public final static String LEX_EXTENSION = ".lex";
	public final static String XML_EXTENSION = ".xml";
	public final static String IN_EXTENSION = ".in";
	public final static String OUT_EXTENSION = ".out";
	public final static String JAVA_EXTENSION = ".java";
	
	public final static String METADATA_FILENAME = ".METADATA";
	public final static String PROPERTIES_FILENAME = "properties.xml";
	
	private String extension;
	
	public FileNames(String extension) {
		this.extension = extension;
	}
	
	public String getExtension() {
		return extension;
	}
}
