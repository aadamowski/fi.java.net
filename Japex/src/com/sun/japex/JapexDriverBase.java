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

import java.util.Properties;

public class JapexDriverBase implements JapexDriver {
    
    private TestSuite _testSuite;
    private TestCase _testCase;
    
    public void setTestSuite(TestSuite testSuite) {
        _testSuite = testSuite;
    }
    
    public void setTestCase(TestCase testCase) {
        _testCase = testCase;
    }
    
    protected TestSuite getTestSuite() {
        return _testSuite;
    }
    
    /**
     * Execute prepare and warmup phases. Even in multi-threaded test, this
     * method will only be executed in single-threaded mode, so there's no
     * need for additional synchronization.
     */
    public void prepareAndWarmup() {
        long millis, nanos;
        TestCase tc = _testCase;
        
        // -- Prepare ----------------------------------------
        
        nanos = Util.currentTimeNanos();
        prepare(tc);
        nanos = Util.currentTimeNanos() - nanos;
        tc.setDoubleParam(Constants.ACTUAL_PREPARE_TIME, 
                          Util.nanosToMillis(nanos));

        // -- Warmup -----------------------------------------

        int warmupIterations = 0;
        String warmupTime = tc.getParam(Constants.WARMUP_TIME);
        if (warmupTime != null) {
            // Calculate end time
            long startTime = Util.currentTimeMillis();
            long endTime = startTime + Util.parseDuration(warmupTime);

            do {
                warmup(tc);      // Call warmup
                warmupIterations++;
                millis = Util.currentTimeMillis();
            } while (endTime >= millis);

            // Set actual number of millis
            millis -= startTime;

            // Set actual number of iterations
            tc.setIntParam(Constants.ACTUAL_WARMUP_ITERATIONS, warmupIterations);
        }
        else {
            // Adjust warmup iterations based on number of threads
            warmupIterations = tc.getIntParam(Constants.WARMUP_ITERATIONS);
            
            nanos = Util.currentTimeNanos();
            for (int i = 0; i < warmupIterations; i++) {
                warmup(tc);      // Call warmup
            }
            nanos = Util.currentTimeNanos() - nanos;
            
            // Set actual warmup time
            tc.setDoubleParam(Constants.ACTUAL_WARMUP_TIME, 
                              Util.nanosToMillis(nanos));
        }
    }
    
    // -- Runnable interface ------------------------------------------
    
    /**
     * Execute the run phase. This method can be executed concurrently
     * by multiple threads. Care should be taken to ensure proper
     * synchronization. Note that parameter getters and setters are
     * already synchronized.
     */
    public void run() {
        long millis, nanos;
        TestCase tc = _testCase;
        
        // Get number of threads to adjust iterations
        int nOfThreads = tc.getIntParam(Constants.NUMBER_OF_THREADS);
        
        int runIterations = 0;
        String runTime = tc.getParam(Constants.RUN_TIME);
        if (runTime != null) {
            // Calculate end time
            long startTime = Util.currentTimeMillis();
            long endTime = startTime + Util.parseDuration(runTime);

            // Run phase
            do {
                run(tc);      // Call run
                runIterations++;
                millis = Util.currentTimeMillis();
            } while (endTime >= millis);

            // Set actual number of millis
            millis -= startTime;
        }
        else {
            // Adjust runIterations based on number of threads
            runIterations = tc.getIntParam(Constants.RUN_ITERATIONS) / nOfThreads;

            // Run phase
            nanos = Util.currentTimeNanos();
            for (int i = 0; i < runIterations; i++) {
                run(tc);      // Call run
            }
            nanos = Util.currentTimeNanos() - nanos;            
            
        }
        
        // Accumulate actual number of iterations
        synchronized (tc) {
            int actualRunIterations =  
                tc.hasParam(Constants.ACTUAL_RUN_ITERATIONS) ? 
                    tc.getIntParam(Constants.ACTUAL_RUN_ITERATIONS) : 0;
            tc.setIntParam(Constants.ACTUAL_RUN_ITERATIONS, 
                           actualRunIterations + runIterations);
        }
    }
    
    // -- JapexDriver interface ------------------------------------------
    
    /**
     * Called once when the class is loaded.
     */
    public void initializeDriver() {
    }
    
    /**
     * Called exactly once for every test, before calling warmup.
     */
    public void prepare(TestCase testCase) {
    }
    
    /**
     * Called once or more for every test, before calling run.
     */
    public void warmup(TestCase testCase) {        
    }
    
    /**
     * Called once or more for every test to obtain perf data.
     */
    public void run(TestCase testCase) {
    }
    
    /**
     * Called exactly once after calling run.
     */
    public void finish(TestCase testCase) {
    }
    
    /**
     * Called after all tests are completed.
     */
    public void terminateDriver() {
    }
    
}
