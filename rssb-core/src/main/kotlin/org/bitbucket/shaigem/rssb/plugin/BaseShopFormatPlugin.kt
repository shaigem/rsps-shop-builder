package org.bitbucket.shaigem.rssb.plugin


import com.google.common.base.Objects
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.util.Callback
import net.xeoh.plugins.base.PluginInformation
import net.xeoh.plugins.base.annotations.events.Init
import net.xeoh.plugins.base.annotations.injections.InjectPlugin
import org.bitbucket.shaigem.rssb.model.shop.Shop

/**
 * Created on 01/09/16.
 */
abstract class BaseShopFormatPlugin : ShopFormatPlugin {

    @InjectPlugin
    lateinit var information: PluginInformation;

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

    fun getAuthor(): String {
        return information.getInformation(PluginInformation.Information.AUTHORS, this).elementAtOrElse(0,
                { "Unknown Author" })
    }

    fun getVersion(): String {
        return information.getInformation(PluginInformation.Information.VERSION, this).elementAtOrElse(0, { "v1.0.0" })
    }

    fun <S : Shop, T> column(title: String, cellValueProvider: (TableColumn.CellDataFeatures<S, T>) ->
    ObservableValue<T>) {
        val column = TableColumn<S, T>(title)
        column.cellValueFactory = Callback { cellValueProvider(it) }
        customTableColumns.add(column)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(format.descriptor(), getAuthor(), getVersion())
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is BaseShopFormatPlugin) {
            return false
        }
        return Objects.equal(other.format.descriptor(), this.format.descriptor()).
                and(Objects.equal(other.getAuthor(), this.getAuthor())).
                and(Objects.equal(other.getVersion(), this.getVersion()))
    }
}

