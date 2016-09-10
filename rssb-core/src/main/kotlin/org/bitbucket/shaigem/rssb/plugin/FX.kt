package org.bitbucket.shaigem.rssb.plugin

import javafx.stage.FileChooser

/**
 * Created on 10/09/16.
 */
fun ext(description: String, extension: String): FileChooser.ExtensionFilter {
    return FileChooser.ExtensionFilter(description, extension)
}
