/*
 * Japex ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This Software is distributed under the following terms:
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc., 'Java', 'Java'-based names,
 * nor the names of contributors may be used to endorse or promote products
 * derived from this Software without specific prior written permission.
 * 
 * The Software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE
 * AS A RESULT OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE
 * LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
 * AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package com.sun.japex;

import java.util.*;
import java.net.*;
import java.io.File;

public class DriverInfo extends Params {
    
    String _name;
    List _testCases = null;
    JapexClassLoader _classLoader;
    Class _class = null;
    boolean _isNormal = false;
    
    static class JapexClassLoader extends URLClassLoader {
        public JapexClassLoader(URL[] urls) {
            super(urls);
        }        
        public Class findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }        
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
       
    public DriverInfo(String name, boolean isNormal, Properties params) {
        super(params);
        _name = name;
        _isNormal = isNormal;
        _classLoader = newJapexClassLoader();
    }
    
    public void setTestCases(List testCases) {
        _testCases = testCases;
    }
    
    public List getTestCases() {
        return _testCases;
    }
    
    JapexDriverBase getJapexDriver() throws ClassNotFoundException {
        String className = getParam(Constants.DRIVER_CLASS);
        if (_class == null) {
            _class = _classLoader.findClass(className);
        }
        
        try {
            Thread.currentThread().setContextClassLoader(_classLoader);
            return (JapexDriverBase) _class.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (ClassCastException e) {
            throw new RuntimeException("Class '" + className 
                + "' must extend '" + JapexDriverBase.class.getName() + "'");
        }
    }
    
    public String getName() {
        return _name;
    }    
    
    public boolean isNormal() {
        return _isNormal;
    }
    
    public void serialize(StringBuffer report, int spaces) {
        report.append(Util.getSpaces(spaces) 
            + "<driver name=\"" + _name + "\">\n");
        
        super.serialize(report, spaces + 2);

        Iterator tci = _testCases.iterator();
        while (tci.hasNext()) {
            TestCase tc = (TestCase) tci.next();
            tc.serialize(report, spaces + 2);
        }            

        report.append(Util.getSpaces(spaces) + "</driver>\n");       
    }
    
    private JapexClassLoader newJapexClassLoader() {
        JapexClassLoader result = new JapexClassLoader(new URL[0]);
        String classPath = getParam(Constants.CLASS_PATH);
        if (classPath == null) {
            return result;
        }
        
        StringTokenizer tokenizer = new StringTokenizer(classPath, 
            System.getProperty("path.separator"));
        
	while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();            
            try {
                result.addURL(new File(path).toURL());
            }
            catch (MalformedURLException e) {
                // ignore
            }
        }        
        return result;
    }
}
