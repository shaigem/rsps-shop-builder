package org.bitbucket.shaigem.rssb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * Created on 2016-05-12.
 */
public abstract class ItemSelectionModel<T> {
    private final ObservableSet<T> selectedItems;


    protected ItemSelectionModel() {
        selectedItems = FXCollections.observableSet();
    }
}
