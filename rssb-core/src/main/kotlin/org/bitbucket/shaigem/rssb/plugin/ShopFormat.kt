package org.bitbucket.shaigem.rssb.plugin

import org.bitbucket.shaigem.rssb.model.shop.Shop
import java.io.File
import java.util.*

/**
 * Created on 30/08/16.
 */
interface ShopFormat<S : Shop> {

    val defaultShop : S

    fun load(selectedFile: File): ArrayList<S>

    fun descriptor(): ShopFormatDescriptor



}
