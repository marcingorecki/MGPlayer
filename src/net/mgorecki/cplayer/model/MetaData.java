/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer.model;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author marcin
 */
public class MetaData {
    public SimpleStringProperty metaKey = new SimpleStringProperty();
    public SimpleStringProperty metaValue = new SimpleStringProperty();

    public String getMetaKey() {
        return metaKey.getValue();
    }

    public String getMetaValue() {
        return metaValue.getValue();
    }
    
}
