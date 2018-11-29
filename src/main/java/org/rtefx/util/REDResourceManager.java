//    RTEFX - Rich Text Editor for JavaFX
//    Copyright (C) 2003, 2018  Robert Lichtenberger
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
package org.rtefx.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** Resource management utility class. Singleton pattern with convenience methods.
  * @author rli@chello.at
  * @tier system
  */
public class REDResourceManager {
    /** File filter */
    class ResourceFileFilter implements FileFilter {
        ResourceFileFilter(String suffix) {
            fSuffix = suffix.toLowerCase();
        }
        public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().endsWith(fSuffix);
        }
        String fSuffix;
    }
	
	private REDResourceManager() {
	}
	
    private REDResourceInputStreamIterator doGetInputStreams(String resourcePath, String fileSuffix) {
		REDTracer.info("red.util", "REDResourceManager", "Getting input streams for: " + resourcePath + " and " + fileSuffix);
		ResourceFileFilter filter = new ResourceFileFilter(fileSuffix);
		ArrayList<Object> list = new ArrayList<>();
		Enumeration<URL> e = null;
		try {
			e = getClass().getClassLoader().getResources(resourcePath);
		}
		catch (IOException ioe) {
			REDTracer.error("red.util", "REDResourceManager", "IO Exception while trying to get system resources at: " + resourcePath);
			return null;
		}
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			REDTracer.info("red.util", "REDResourceManager", "Element: " + url);
			
			File file = new File(url.getFile());
			if (file.isDirectory()) {
				File [] files = file.listFiles(filter);
				for (int x = 0; x < files.length; x++) {
					list.add(files[x]);
				}
			}
			else {
				try {
					URLConnection connection = url.openConnection();
					if (connection instanceof JarURLConnection) {
						JarURLConnection juc = (JarURLConnection) connection;
						JarFile jarFile = juc.getJarFile();
						Enumeration<JarEntry> e2 = jarFile.entries();
						while (e2.hasMoreElements()) {
							String fName = "" + e2.nextElement();
							if (fName.startsWith(resourcePath) && fName.endsWith(fileSuffix)) {
								list.add(new REDResourceJarEntry(jarFile, jarFile.getEntry(fName)));
								REDTracer.info("red.util", "REDResourceManager", "Adding: " + jarFile.getEntry(fName) + " to list.");
							}
						}
					}
					else {
						REDTracer.error("red.util", "REDResourceManager", "Neither directory nor JarURL: " + connection);
					}
				}
				catch (IOException ioe) {
					REDTracer.error("red.util", "REDResourceManager", "IO Exception while trying to process JarURL: " + url);
				}			
			}
		}	
		return new REDResourceInputStreamIterator(list);
    }	

	/** Get resource input stream iterator.
	  * @param resourcePath The path to find resources in (must be in classpath), maybe in .jar - File.
	  * @param fileSuffix The suffix files to be iterated must end with.
	  * @return An iterator which will return InputStream objects over all the specified resources,  or null if an error occurred.
	  */
    public static REDResourceInputStreamIterator getInputStreams(String resourcePath, String fileSuffix) {
		return fgInstance.doGetInputStreams(resourcePath, fileSuffix);
    }
	
	private static REDResourceManager fgInstance = new REDResourceManager();
}
