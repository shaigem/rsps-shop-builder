package org.bitbucket.shaigem.rssb.plugin

import javafx.stage.FileChooser

/**
 * Created on 10/09/16.
 */
fun extension(description: String, extension: String): FileChooser.ExtensionFilter {
    return FileChooser.ExtensionFilter(description, extension)
}
