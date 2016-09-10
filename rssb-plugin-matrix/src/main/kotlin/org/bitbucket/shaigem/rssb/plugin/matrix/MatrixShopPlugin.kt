package org.bitbucket.shaigem.rssb.plugin.matrix

import javafx.beans.Observable
import javafx.beans.property.SimpleIntegerProperty
import net.xeoh.plugins.base.annotations.PluginImplementation
import net.xeoh.plugins.base.annotations.meta.Author
import org.bitbucket.shaigem.rssb.model.item.Item
import org.bitbucket.shaigem.rssb.model.shop.Shop
import org.bitbucket.shaigem.rssb.plugin.*
import java.io.File
import java.util.*

@PluginImplementation
@Author(name = "AbyssPartyy")
class MatrixUnpackedShopPlugin : BaseShopFormatPlugin() {

    override val format: ShopFormat<MatrixShop> = MatrixUnpackedShopFormat()

    override fun addColumnToExplorer() {
        column<MatrixShop, Int>(title = "Key") {
            SimpleIntegerProperty(it.value.key).asObject()
        }
    }
}

class MatrixUnpackedShopFormat : ShopFormat<MatrixShop> {

    override val defaultShop: MatrixShop = MatrixShop(
            key = -1,
            name = "New Shop",
            itemArray = emptyList(),
            currency = 995,
            canSellTo = false)

    override fun descriptor(): ShopFormatDescriptor {
        return ShopFormatDescriptor(
                name = "Matrix Unpacked",
                description = "Edit unpacked Matrix shops (unpackedShops.txt).")
    }

    override fun load(selectedFile: File): ArrayList<MatrixShop> {
        val shops = arrayListOf<MatrixShop>()

        fun parseShop(splitStringList: List<String>) {
            if (splitStringList.size != 3) {
                throw ParseMatrixFormatException(splitStringList.toString())
            }

            val splitShopPropertiesList: List<String> =
                    splitStringList[0].split(" ", limit = 3)

            if (splitShopPropertiesList.size != 3) {
                throw ParseMatrixFormatException(splitStringList.toString())
            }

            val key: Int = splitShopPropertiesList[0].toInt()
            val currency: Int = splitShopPropertiesList[1].toInt()
            val isGeneralStore: Boolean = splitShopPropertiesList[2].toBoolean()
            val shopName = splitStringList[1]

            fun readItems(): List<Item> {
                val shopItemsStringList: List<String> = splitStringList[2].split(" ").filterNot { it.isNullOrEmpty() }
                // Get every element with a even (0, 2, 4...) index for the item id
                val shopItemIdentifiers = shopItemsStringList.filterIndexed { i, s -> (i % 2) == 0 }.map { it.toInt() }
                // Get every element with a odd (1, 3, 5...) index for the amount
                val shopItemAmounts = shopItemsStringList.filterIndexed { i, s -> (i % 2) != 0 }.map { it.toInt() }
                return shopItemIdentifiers.zip(shopItemAmounts, { id, amount -> Item(id, amount) })
            }

            val shopItems = readItems()

            shops.add(MatrixShop(key, shopName, shopItems, currency, isGeneralStore))
        }

        selectedFile.bufferedReader().readLines().filterNot { it.isNullOrEmpty() || it.startsWith("//") }.
                map { it.split(" - ", limit = 3) }.forEach { parseShop(it) }

        return shops
    }
}

class MatrixShop(key: Int, name: String, itemArray: List<Item>, currency: Int,
                 canSellTo: Boolean) : Shop(name, itemArray, currency, canSellTo) {

    // Must import these:
    // import org.bitbucket.shaigem.rssb.plugin.getValue
    // import org.bitbucket.shaigem.rssb.plugin.setValue
    private val keyProperty = SimpleIntegerProperty(key)
    var key: Int by keyProperty

    override fun getCustomPropertiesToObserve(): Array<out Observable> {
        return arrayOf(keyProperty)
    }

    override fun copy(): Shop {
        return MatrixShop(key, name, items, currency, canSellTo)
    }
}


class ParseMatrixFormatException(line: String) : RuntimeException("Invalid list for shop line: $line")