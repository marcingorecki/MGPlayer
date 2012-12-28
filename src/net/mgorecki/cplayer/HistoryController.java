/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.mgorecki.cplayer.model.MetaData;
import net.mgorecki.cplayer.model.Recent;
import net.mgorecki.cplayer.store.StorageManager;

/**
 *
 * @author marcin
 */
public class HistoryController {
    
    public Stage stage;
    public Recent selected = null;
    @FXML private TableView historyTable;
    @FXML private TableColumn mediaColumn;
    @FXML private TableColumn positionColumn;
    @FXML private TableColumn timestampColumn;
    
    private final static Logger logger = Logger.getLogger(HistoryController.class.getName());
    
    @FXML 
    void initialize() {
        mediaColumn.setCellValueFactory(new PropertyValueFactory<MetaData,String>("mediaUri"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<MetaData,String>("positionFormatted"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<MetaData,String>("timestampFormatted"));
        
        populateHistoryTable();
    }
    
    public void closeAction(ActionEvent event) {
        logger.log(Level.FINE, "Closing history");
        stage.hide();
    }
    
    public void mouseClicked(MouseEvent event) {
        logger.log(Level.FINE, "Mouse event");
        if(event.getClickCount() == 2) {
            selected = (Recent) historyTable.getSelectionModel().getSelectedItem();
            logger.log(Level.FINE, "Selected by doubleclick = {0}", selected);
            stage.hide();
        }
    }

    private void populateHistoryTable() {
        List<Recent> allRecent = StorageManager.getAllRecent();
        ObservableList<Recent> data = FXCollections.observableArrayList();
        data.addAll(allRecent);
        historyTable.setItems(data);
    }
}
