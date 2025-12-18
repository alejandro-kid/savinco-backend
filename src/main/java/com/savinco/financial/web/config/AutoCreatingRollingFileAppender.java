package com.savinco.financial.web.config;

import java.io.File;

import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 * Custom RollingFileAppender that automatically creates parent directories
 * if they don't exist before attempting to write to the log file.
 * If directory creation fails, the appender will not start but won't fail the application.
 */
public class AutoCreatingRollingFileAppender<E> extends RollingFileAppender<E> {

    @Override
    public void start() {
        // Create parent directories before starting
        String filePath = getFile();
        if (filePath != null) {
            if (!createParentDirectories(filePath)) {
                // If directory creation failed, set file to null to disable file logging
                // This allows the appender to start but it won't write to file
                setFile(null);
                addWarn("Failed to create log directory. File appender will write to console only: " + filePath);
            }
        }
        // Always call super.start() - if file is null, it will just not write to file
        super.start();
    }

    private boolean createParentDirectories(String filePath) {
        try {
            // Resolve to absolute path
            File file = new File(filePath);
            File parent = file.getParentFile();
            
            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                if (!created && !parent.exists()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
