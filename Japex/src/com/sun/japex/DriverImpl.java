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
import java.io.FilenameFilter;

public class DriverImpl extends ParamsImpl implements Driver {
    
    String _name;
    boolean _isNormal = false;
    boolean _computeMeans = true;
    Class _class = null;
    
    TestCaseArrayList[] _testCases;
    TestCaseArrayList _aggregateTestCases;
    
    int _runsPerDriver;    
    boolean _includeWarmupRun;
    
    static JapexClassLoader _classLoader;

    /**
     * Set the parent class loader to null in order to force the use of 
     * the bootstrap classloader. The boostrap class loader does not 
     * have access to the system's class path.
     */ 
    static class JapexClassLoader extends URLClassLoader {
        public JapexClassLoader(URL[] urls) {
            super(urls, null);
        }          
        public Class findClass(String name) throws ClassNotFoundException {
            // Delegate when loading Japex classes, excluding JDSL drivers
            if (name.startsWith("com.sun.japex.") && !name.startsWith("com.sun.japex.jdsl.")) {
                return DriverImpl.class.getClassLoader().loadClass(name);
            }
            
            // Otherwise, use class loader based on japex.classPath only
            return super.findClass(name);
        }        
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
    
       
    public DriverImpl(String name, boolean isNormal, int runsPerDriver, 
        boolean includeWarmupRun, ParamsImpl params) 
    {
        super(params);
        _name = name;
        _isNormal = isNormal;
        _runsPerDriver = runsPerDriver;
        _includeWarmupRun = includeWarmupRun;
        initJapexClassLoader();        
    }
    
    public void setTestCases(TestCaseArrayList testCases) {
        _testCases = new TestCaseArrayList[_runsPerDriver];
        for (int i = 0; i < _runsPerDriver; i++) {
            _testCases[i] = (TestCaseArrayList) testCases.clone();
        }
        
        _aggregateTestCases = (TestCaseArrayList) testCases.clone();
    }
        
    private void computeMeans() {
        final int runsPerDriver = _testCases.length;        
        final int startRun = _includeWarmupRun ? 1 : 0;
        
        boolean scatter = getParam(Constants.CHART_TYPE).equals("scatterchart");
        
        // Avoid re-computing the driver's aggregates
        if (_computeMeans) {
            final int nOfTests = _testCases[0].size();

            for (int n = 0; n < nOfTests; n++) {
                double avgRunsResult = 0.0;

                double[] results = new double[runsPerDriver];
                double[] resultsX = new double[runsPerDriver];
                
                // Collect vertical results for this test
                for (int i = startRun; i < runsPerDriver; i++) {            
                    TestCaseImpl tc = (TestCaseImpl) _testCases[i].get(n);
                    results[i] = tc.getDoubleParam(Constants.RESULT_VALUE);
                    if (scatter) {
                        resultsX[i] = tc.getDoubleParam(Constants.RESULT_VALUE_X);
                    }
                }
                
                // Compute vertical average and stddev for this test
                TestCaseImpl tc = (TestCaseImpl) _aggregateTestCases.get(n);
                tc.setDoubleParam(Constants.RESULT_VALUE, 
                                  Util.arithmeticMean(results, startRun));
                if (scatter) {
                    tc.setDoubleParam(Constants.RESULT_VALUE_X, 
                                      Util.arithmeticMean(resultsX, startRun));                    
                }                
                if (runsPerDriver - startRun > 1) {
                    tc.setDoubleParam(Constants.RESULT_VALUE_STDDEV, 
                                      Util.standardDev(results, startRun));
                    if (scatter) {
                        tc.setDoubleParam(Constants.RESULT_VALUE_X_STDDEV, 
                                          Util.standardDev(resultsX, startRun));                        
                    }
                }
            }
            
            // geometric mean = (sum{i,n} x_i) / n
            double geomMeanresult = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            double aritMeanresult = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            double harmMeanresultInverse = 0.0;
            
            // geometric mean = (sum{i,n} x_i) / n
            double geomMeanresultX = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            double aritMeanresultX = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            double harmMeanresultXInverse = 0.0;
            
            // Compute horizontal means based on vertical means
            Iterator tci = _aggregateTestCases.iterator();
            while (tci.hasNext()) {
                TestCaseImpl tc = (TestCaseImpl) tci.next();       
                
                // Compute running means 
                double result = tc.getDoubleParam(Constants.RESULT_VALUE);
                aritMeanresult += result / nOfTests;
                geomMeanresult *= Math.pow(result, 1.0 / nOfTests);
                harmMeanresultInverse += 1.0 / (nOfTests * result);                
                if (scatter) {
                    double resultX = tc.getDoubleParam(Constants.RESULT_VALUE_X);
                    aritMeanresultX += resultX / nOfTests;
                    geomMeanresultX *= Math.pow(resultX, 1.0 / nOfTests);
                    harmMeanresultXInverse += 1.0 / (nOfTests * resultX);
                }
            }
            
            // Set driver-specific params
            setDoubleParam(Constants.RESULT_ARIT_MEAN, aritMeanresult);
            setDoubleParam(Constants.RESULT_GEOM_MEAN, geomMeanresult);
            setDoubleParam(Constants.RESULT_HARM_MEAN, 1.0 / harmMeanresultInverse);      
            if (scatter) {
                setDoubleParam(Constants.RESULT_ARIT_MEAN_X, aritMeanresultX);
                setDoubleParam(Constants.RESULT_GEOM_MEAN_X, geomMeanresultX);
                setDoubleParam(Constants.RESULT_HARM_MEAN_X, 1.0 / harmMeanresultXInverse);                      
            }
            
            // Avoid re-computing these means
            _computeMeans = false;
            
            // If number of runs is just 1, we're done
            if (runsPerDriver - startRun == 1) {
                return;
            }
            
            // geometric mean = (sum{i,n} x_i) / n
            geomMeanresult = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            aritMeanresult = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            harmMeanresultInverse = 0.0;
            
            // geometric mean = (sum{i,n} x_i) / n
            geomMeanresultX = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            aritMeanresultX = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            harmMeanresultXInverse = 0.0;
            
            // Compute horizontal stddevs based on vertical stddevs
            tci = _aggregateTestCases.iterator();
            while (tci.hasNext()) {
                TestCaseImpl tc = (TestCaseImpl) tci.next();       
                
                // Compute running means 
                double result = tc.getDoubleParam(Constants.RESULT_VALUE_STDDEV);
                aritMeanresult += result / nOfTests;
                geomMeanresult *= Math.pow(result, 1.0 / nOfTests);
                harmMeanresultInverse += 1.0 / (nOfTests * result);
                if (scatter) {
                    double resultX = tc.getDoubleParam(Constants.RESULT_VALUE_STDDEV);
                    aritMeanresultX += resultX / nOfTests;
                    geomMeanresultX *= Math.pow(resultX, 1.0 / nOfTests);
                    harmMeanresultXInverse += 1.0 / (nOfTests * resultX);
                }
            }
            
            // Set driver-specific params
            setDoubleParam(Constants.RESULT_ARIT_MEAN_STDDEV, aritMeanresult);
            setDoubleParam(Constants.RESULT_GEOM_MEAN_STDDEV, geomMeanresult);
            setDoubleParam(Constants.RESULT_HARM_MEAN_STDDEV, 1.0 / harmMeanresultInverse);            
            if (scatter) {
                setDoubleParam(Constants.RESULT_ARIT_MEAN_X_STDDEV, aritMeanresultX);
                setDoubleParam(Constants.RESULT_GEOM_MEAN_X_STDDEV, geomMeanresultX);
                setDoubleParam(Constants.RESULT_HARM_MEAN_X_STDDEV, 1.0 / harmMeanresultXInverse);                        
            }
        }        
    }
    
    public List getTestCases(int driverRun) {
        return _testCases[driverRun];
    }
    
    public List getAggregateTestCases() {
        computeMeans();  
        return _aggregateTestCases;
    }
    
    JapexDriverBase getJapexDriver() throws ClassNotFoundException {
        String className = getParam(Constants.DRIVER_CLASS);
        if (_class == null) {
            _class = Class.forName(className, true, _classLoader);
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
        
       /*
         * Calling getAggregateTestCases() forces call to computeMeans(). This
         * is necessary before serializing driver params.
         */
        Iterator tci = getAggregateTestCases().iterator();
       
        // Serialize driver params
        super.serialize(report, spaces + 2);

        // Serialize each test case
        while (tci.hasNext()) {
            TestCaseImpl tc = (TestCaseImpl) tci.next();
            tc.serialize(report, spaces + 2);
        }            

        report.append(Util.getSpaces(spaces) + "</driver>\n");       
    }
    
    /**
     * Initializes the Japex class loader. A single class loader will be
     * created for all drivers. Thus, if japex.classPath is defined as
     * a driver's property, it will be ignored.
     */ 
    synchronized private void initJapexClassLoader() {
        // Initialize class loader only once
        if (_classLoader != null) {
            return;
        }

        _classLoader = new JapexClassLoader(new URL[0]);
        String classPath = getParam(Constants.CLASS_PATH);
        if (classPath == null) {
            return;
        }
        
        String pathSep = System.getProperty("path.separator");
        String fileSep = System.getProperty("file.separator");
        StringTokenizer tokenizer = new StringTokenizer(classPath, pathSep); 
        
        // TODO: Ensure that this code works on Windows too!
	while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();            
            try {
                boolean lookForJars = false;
                
                // Strip off '*.jar' at the end if present
                if (path.endsWith("*.jar")) {
                    int k = path.lastIndexOf('/');
                    path = (k >= 0) ? path.substring(0, k + 1) : "./";
                    lookForJars = true;
                }
                
                // Create a file from the resulting path
                File file = new File(path);
                
                // If a directory, add all '.jar'
                if (file.isDirectory() && lookForJars) {
                    String children[] = file.list(
                        new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".jar");
                            }
                        });
                        
                    for (String c : children) {
                        _classLoader.addURL(new File(path + fileSep + c).toURL());
                    }
                }
                else {
                    _classLoader.addURL(file.toURL());
                }
            }
            catch (MalformedURLException e) {
                // ignore
            }
        }        
    }
}
