package org.grview.util;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

public class JarFileLoader extends URLClassLoader
{
    public JarFileLoader (URL[] urls)
    {
        super (urls);
    }

    public void addFile (File file) throws MalformedURLException
    {
        addURL (file.toURI().toURL());
    }
}
