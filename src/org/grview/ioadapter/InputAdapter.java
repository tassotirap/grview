package org.grview.ioadapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.grview.editor.TextArea;
import org.grview.parser.ParserProxy;
import org.grview.parser.ParserProxyFactory;
import org.grview.ui.Window;
import org.grview.ui.component.AdvancedTextAreaComponent;
import org.grview.ui.component.BadParameterException;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.InputAdapterComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.util.JarFileLoader;
import org.grview.util.Log;
import org.grview.util.dynacode.DynaCode;


public class InputAdapter extends IOAdapter {

	private JComponent formView;
	private JComponent codeView;
	private JComponent frameView;
	
	private JFrame objectJFrame;
	
	private InputAdapterComponent iaComponent;
	private AdvancedTextAreaComponent codeAdvTextArea;
	private TextArea codeTextArea;
	
	private boolean built;
	private boolean started;
	
	private int stubInitPosition = -1;
	InputAdapterStub stub;
	/**
	 * A reference to the main class of the new component included in this adapter
	 */
	private Class<?> adapterClass;
	/**
	 * A reference to an instance of the new component's main class included in this adapter 
	 */
	private Object adapterInstance;
	
	/** when you are navigating through classes, this object holds an instance of this last visited class **/
	private Object lastInstance;
	
	private JarFileLoader jarLoader;
	
	public InputAdapter(InputAdapterComponent iaComponent) {
		this.iaComponent = iaComponent;
		codeAdvTextArea = new AdvancedTextAreaComponent("java");
		codeAdvTextArea.addComponentListener(iaComponent);
		codeTextArea = codeAdvTextArea.getTextArea();
		TextAreaRepo.remove(codeAdvTextArea);
		TextAreaRepo.register(iaComponent, codeAdvTextArea.getTextArea());
	}
	
	
	/** Validates a new build **/
	public boolean canBuild() {
		//TODO decent validation, and rebuild
		InputAdapterForm iaf = (InputAdapterForm) formView;
		return new File(iaf.getJarTextField().getText()).exists() && !iaf.getMainTextField().getText().equals("");
	}
	
	public boolean build() {
		try {
			InputAdapterForm iaf = (InputAdapterForm) formView;
			jarLoader = new JarFileLoader(new URL[] {});
			jarLoader.addFile(new File(iaf.getJarTextField().getText()));
			adapterClass = jarLoader.loadClass(iaf.getMainTextField().getText());
			built = true;
			iaComponent.setBuilt(true);
			return true;
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Could not built!", e);
		}
		return false;
	}
	
	/** Verifies if everything is ready to start, after a build **/
	public boolean canStart() {
		return built;
	}
	
	public boolean start() {
        try {
			InputAdapterForm iaf = (InputAdapterForm) formView;
        	String[] args = iaf.getArgsTextField().getText().split(" ");
        	Object[] objArgs = new Object[] {args};
        	//someday my friend
			//adapterInstance = adapterClass.newInstance();
            adapterClass.getMethod("main", new Class[] {String[].class})
            .invoke(null, objArgs);
			String frameClass = iaf.getJframeTextField().getText();
			String frameName = iaf.getJframeNameTextField().getText();
	        for (java.awt.Window w : java.awt.Window.getOwnerlessWindows()) {
	        	if (!frameClass.equals("")) {
	        		if (w.getClass().getName().equals(frameClass)) {
	        			w.setVisible(false);
	        			if (w instanceof JFrame) {
	        				objectJFrame = (JFrame) w;
	        			}
	        		}
	        	}
	        	else if (!frameName.equals("")) {
	        		if (w.getName().equals(frameName)) {
	        			w.setVisible(false);
	        			if (w instanceof JFrame) {
	        				objectJFrame = (JFrame) w;
	        			}
	        		}
	        	}
	        	else if (w instanceof JFrame && !w.getName().equals(Window.DEFAULT_NAME)) {
	        		w.setVisible(false);
	        		//RISKY...Could get dialogs and other stuff
        			objectJFrame = (JFrame) w;
	        	}
	        }
	        adapterInstance = objectJFrame;
	        if (adapterInstance == null) {
	        	adapterInstance = adapterClass.newInstance();
	        }
	        started = true;
	        iaComponent.setStarted(true);
	        return true;
        } catch (Exception e) {
        	Log.log(Log.ERROR, this, "Could not start!", e);
            /*InternalError error = new InternalError("Failed to invoke main method");
            error.initCause(e);
            throw error;*/
        }
        adapterClass = null;
        adapterInstance = null;
        return false;
	}

	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/** Generates a java file with the input adapter **/
	public boolean generate() {
		String filePath = new File(codeAdvTextArea.getPath()).getParentFile().getAbsolutePath() +
		"/generated_code/" +  "Input" + adapterClass.getSimpleName() + ".java";
		File file = new File(filePath);
		try {
			String text = codeTextArea.getBuffer().getText(0, codeTextArea.getBuffer().getLength());
			FileWriter fw = new FileWriter(file);
			fw.write(text);
			fw.close();
			DynaCode dynacode = new DynaCode(jarLoader);
			dynacode.addSourceDir(file.getParentFile(), new File("dynacode"));
			InputAdapterImpl iaImpl = (InputAdapterImpl) dynacode.newProxyInstance(jarLoader, InputAdapterImpl.class,"Input" + adapterClass.getSimpleName());
			iaImpl.setObject(adapterInstance);
			iaImpl.setInput(ParserProxyFactory.create(this));
			iaImpl.init();
			frameView = new InputAdapterFrame(objectJFrame);
		} catch (IOException e) {
			Log.log(Log.ERROR, this, "Could not generate a java file! See the console for details.", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the currently focused class
	 * @return the last focused class
	 */
	public Class<?> getCurrentClass() {
		if (lastInstance == null) {
			return adapterClass;
		}
		else {
			return lastInstance.getClass();
		}
	}
	
	public void save() {
		codeAdvTextArea.saveFile();
	}
	
	@Override
	public JComponent getCodeView() {
		if (codeView == null) {
			try {
				codeView = codeAdvTextArea.create(iaComponent.getPath());
				if (codeTextArea.getText().equals("")) {
					stub = new InputAdapterStub();
					stub.setClass("Input" + adapterClass.getSimpleName());
					stub.addImport(adapterInstance.getClass().getCanonicalName());
					stub.addImport(ParserProxyFactory.class.getCanonicalName());
					stub.addImport(ParserProxy.class.getCanonicalName());
					stub.addImport(InputAdapterImpl.class.getCanonicalName());
					stub.setObjectClass(adapterInstance.getClass().getName());
					stub.addInterface("InputAdapterImpl");
					codeTextArea.setText(stub.getStub());
					this.stubInitPosition = stub.getInitMethodPos();
				}
			} catch (BadParameterException e) {}
		}
		return codeView;
	}

	@Override
	public JComponent getFormView() {
		if (formView == null) {
			InputAdapterForm iaf = new InputAdapterForm(this);
			formView = iaf;
			File file = new File(iaComponent.getPath());
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String firstLine = br.readLine();
				br.close();
				fis.close();
				if (firstLine != null && firstLine.startsWith("///~")) {
					iaf.getMainTextField().setText(firstLine.replace("///~", ""));
					iaf.getMainTextField().setEditable(false);
				}
			}
			catch (IOException e) {}
		}
		return formView;
	}

	@Override
	public JComponent getFrameView() {
		return frameView;
	}

	public void resetLastInstance() {
		lastInstance = null;
	}
	
 	public boolean isBuilt() {
		return built;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public Class<?> getAdapterClass() {
		return adapterClass;
	}
	
	public Object getAdapterInstance() {
		return adapterInstance;
	}
	
	public void setLastInstance(Object instance) {
		this.lastInstance = instance;
	}
	
	public Object getLastInstance() {
		if (lastInstance == null) {
			return adapterInstance;
		}
		return lastInstance;
	}
	
	public TextArea getCodeTextArea() {
		return codeTextArea;
	}


	public int getStubInitPosition() {
		File file = new File(codeAdvTextArea.getPath());
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			int lnCnt = 0;
			while ((line = br.readLine()) != null) {
				if (line.replace("/t", "").replace(" ", "").matches("^publicvoidinit(){.*")) {
					stubInitPosition = lnCnt + 1;
					break;
				}
				lnCnt++;
			}
			br.close();
			fis.close();
		} catch (IOException e) {
			Log.log(Log.ERROR, this, "An internal error occurred", e);
			stubInitPosition = stub.getInitMethodPos();
		};
		return stubInitPosition;
	}
}
