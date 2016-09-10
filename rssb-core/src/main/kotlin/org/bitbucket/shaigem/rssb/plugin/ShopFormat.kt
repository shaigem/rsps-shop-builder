package org.bitbucket.shaigem.rssb.plugin

import javafx.collections.ObservableList
import javafx.stage.FileChooser
import org.bitbucket.shaigem.rssb.model.shop.Shop
import java.io.File
import java.util.*

/**
 * Created on 30/08/16.
 */
interface ShopFormat<S : Shop> {

    val defaultShop: S

    val defaultFileName: String

    val extensions: List<FileChooser.ExtensionFilter>

    @Throws(ShopLoadException::class)
    fun load(selectedFile: File): ArrayList<S>

    @Throws(ShopExportException::class)
    fun export(selectedFile: File, shopsToExport: ObservableList<S>)

    fun descriptor(): ShopFormatDescriptor

}
