package org.bitbucket.shaigem.rssb.event;

import java.io.File;

/**
 * Created on 05/09/16.
 */
public class ImportPluginRequest {

    private File file;

    public ImportPluginRequest(File file) {
        this.file = file;
    }

    public File getFileToImport() {
        return file;
    }
}
