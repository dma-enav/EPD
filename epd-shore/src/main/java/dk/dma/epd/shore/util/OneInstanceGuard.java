/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.shore.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for checking if other instances are running.
 */
public class OneInstanceGuard {

    private static final Logger LOG = LoggerFactory.getLogger(OneInstanceGuard.class);

    private File lockFile;
    private FileChannel channel;
    private FileLock lock;
    private boolean alreadyRunning;

    public OneInstanceGuard(String lockFileName) {
        lockFile = new File(lockFileName);
        if (lockFile.exists()) {
            lockFile.delete();
        }
        try {
            channel = new RandomAccessFile(lockFile, "rw").getChannel();
        } catch (FileNotFoundException e) {
            // Not running
            LOG.info("File not found: " + e);
            return;
        }
        try {
            lock = channel.tryLock();
            if (lock == null) {
                // File is lock by other application
                channel.close();
                throw new IOException("Instance already active");
            }
        } catch (IOException e) {
            // Running
            LOG.info("Instance already running");
            alreadyRunning = true;
            return;
        }
        ShutdownHook shutdownHook = new ShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

    }

    public void unlockFile() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                lockFile.delete();
            }
        } catch (IOException e) {
            LOG.error("Failed to unlock lock file");
        }
    }

    public boolean isAlreadyRunning() {
        return alreadyRunning;
    }

    static class ShutdownHook extends Thread {

        private OneInstanceGuard guard;

        public ShutdownHook(OneInstanceGuard guard) {
            setDaemon(true);
            this.guard = guard;
        }

        public void run() {
            guard.unlockFile();
        }
    }

}
