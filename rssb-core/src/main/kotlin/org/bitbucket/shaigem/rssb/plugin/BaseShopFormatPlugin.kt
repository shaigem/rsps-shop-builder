package org.bitbucket.shaigem.rssb.plugin


import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.util.Callback
import net.xeoh.plugins.base.annotations.events.Init
import org.bitbucket.shaigem.rssb.model.shop.Shop

/**
 * Created on 01/09/16.
 */
abstract class BaseShopFormatPlugin : ShopFormatPlugin {

    abstract val format: ShopFormat<*>

    val customTableColumns: MutableList<TableColumn<out Shop, *>> = mutableListOf()

    /*
     * To be executed upon plugin initialization.
     */
    @Init
    fun initialize() {
        addColumnToExplorer()
    }

    /*
     * To be overridden by subclasses
     */
    /**
     * Allows the creation of extra table columns that will be displayed in the shop explorer section.
     */
    open fun addColumnToExplorer() {

    }

    fun <S : Shop, T> column(title: String, cellValueProvider: (TableColumn.CellDataFeatures<S, T>) ->
    ObservableValue<T>) {
        val column = TableColumn<S, T>(title)
        column.cellValueFactory = Callback { cellValueProvider(it) }
        customTableColumns.add(column)
    }
}

