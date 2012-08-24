package org.grview.ioadapter;

import java.util.ArrayList;

public class InputAdapterStub
{

	private String _class = "Input";
	private String _package;
	private String header = "/**Self gerated input adapter class stub\n" + "Warning: If you change the name of the class, the existing fields, constructors or methods\n" + "except for the body of init method, then you must manually make all necessary corrections in\n" + "the generated code file. Besides that the runtime execution may fail.\n**/";
	private ArrayList<String> imports = new ArrayList<String>();
	private int initMethodPos;
	private ArrayList<String> interfaces = new ArrayList<String>();
	private String objectClass;

	private String superclass;

	public void addImport(String _import)
	{
		imports.add(_import);
	}

	public void addInterface(String _interface)
	{
		interfaces.add(_interface);
	}

	public String get_Class()
	{
		return _class;
	}

	public String getHeader()
	{
		return header;
	}

	public ArrayList<String> getImports()
	{
		return imports;
	}

	public int getInitMethodPos()
	{
		return initMethodPos;
	}

	public ArrayList<String> getInterfaces()
	{
		return interfaces;
	}

	public String getObjectClass()
	{
		return objectClass;
	}

	public String getPackage()
	{
		return _package;
	}

	public String getStub()
	{
		StringBuffer stub = new StringBuffer();
		if (objectClass != null && !objectClass.equals(""))
		{
			stub.append("///~" + objectClass);
			stub.append("\n/*Do not erase the line above*/\n");
		}
		stub.append(((_package != null && !_package.equals("")) ? "package " + _package + ";\n\n" : ""));
		for (String i : imports)
		{
			stub.append("import " + i + ";\n");
		}
		stub.append("\n\n");
		stub.append((header != null && !header.equals("")) ? header + "\n" : "");
		stub.append("public class " + _class);
		stub.append((superclass != null && !superclass.equals("")) ? " extends " + superclass : "");
		stub.append((interfaces.size() > 0) ? " implements " : "");
		boolean isFirst = true;
		for (String i : interfaces)
		{
			if (isFirst)
			{
				stub.append(i);
				isFirst = false;
			}
			else
			{
				stub.append(", " + i);
			}

		}
		stub.append(" {\n\n");
		setInitMethodPos(stub.length());
		if (objectClass != null && !objectClass.equals(""))
		{
			stub.append("\tprivate " + objectClass + " object;\n");
			stub.append("\tprivate ParserProxy input;\n\n");
			stub.append("\tpublic " + _class + "() {\n\t}\n\n");
			stub.append("\tpublic " + _class + "(" + objectClass + " object, ParserProxy input){\n");
			stub.append("\t\tthis.object = object;\n");
			stub.append("\t\tthis.input = input;\n");
			stub.append("\t\tinit();\n\t}\n\n");
			stub.append("\tpublic " + objectClass + " getObject(){\n");
			stub.append("\t\treturn this.object;\n\t}\n\n");
			stub.append("\tpublic void setObject(Object object) {\n\n");
			stub.append("\t\tthis.object = (" + objectClass + ") object;\n\t}\n\n");
			stub.append("\tpublic void setInput(ParserProxy input){\n");
			stub.append("\t\tthis.input = input;\n\t}\n\n");
			stub.append("\tpublic ParserProxy getInput() {\n");
			stub.append("\t\treturn this.input;\n\t}\n\n");
			stub.append("\tpublic void init() {\n\n");
			setInitMethodPos(stub.length());
			stub.append("\t}\n\n");
		}
		stub.append("}");
		return stub.toString();
	}

	public String getSuperclass()
	{
		return superclass;
	}

	public void setClass(String class1)
	{
		_class = class1;
	}

	public void setInitMethodPos(int initMethodPos)
	{
		this.initMethodPos = initMethodPos;
	}

	public void setObjectClass(String objectClass)
	{
		this.objectClass = objectClass;
	}

	public void setPackage(String _package)
	{
		this._package = _package;
	}

	public void setSuperclass(String superclass)
	{
		this.superclass = superclass;
	}

}
