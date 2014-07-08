/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception handler for uncaught exceptions. 
 */
public class ExceptionHandler implements UncaughtExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);
    
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOG.error("Uncaught exception from thread " + t.getName(), e);
        JOptionPane.showMessageDialog(null, "An error has occured! If the problem persists please restart the software and contact an administrator.", "Application error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

}
