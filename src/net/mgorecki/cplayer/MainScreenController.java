/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.mgorecki.cplayer.model.MetaData;
import net.mgorecki.cplayer.model.Recent;
import net.mgorecki.cplayer.store.StorageManager;

/**
 * FXML Controller class
 *
 * @author marcin
 */
public class MainScreenController implements Initializable {

    @FXML private TableView tableView;
    @FXML private Label statusLabel;
    @FXML private TableColumn metaKeyColumn;
    @FXML private TableColumn metaValueColumn;
    @FXML private Text timeHours;
    @FXML private Text timeMinutes;
    @FXML private Text timeSeconds;
    @FXML private Slider globalSlider;
    @FXML private Text totalTime;
    @FXML private TextField newTime;
    
    private MediaPlayer player = null;
    private Timeline time = new Timeline();
    
    private double secY=Double.MIN_VALUE;
    private double minY=Double.MIN_VALUE;
    private double hourY=Double.MIN_VALUE;
    private boolean playStarted=false;
    
    private String mediaUri = "file:/tmp/katie.mp3";
    private int mediaPosition = 0;
    
    private final List metaToIgnore = Arrays.asList(new String[]{"raw metadata"});
    private final Pattern hmsPattern=Pattern.compile("^(\\d{1,2}):(\\d{2}):(\\d{2})$");
    private final Pattern msPattern=Pattern.compile("^(\\d{1,2}):(\\d{2})$");
    private final static Logger logger = Logger.getLogger(MainScreenController.class.getName());
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        metaKeyColumn.setCellValueFactory(new PropertyValueFactory<MetaData,String>("metaKey"));
        metaValueColumn.setCellValueFactory(new PropertyValueFactory<MetaData,String>("metaValue"));
        
        Recent recent = StorageManager.getRecent();
        logger.log(Level.FINE, "Recent = {0}", recent);
        
        if(recent!=null) {
            mediaUri = recent.getMediaUri();
            mediaPosition = recent.getPosition();
        }
        startUIUpdate();
        reloadMedia();
        refreshMetadata();
    }    
    
    public void saveState() {
        //we save state only when something was being played. Otherwise the player won't give me current possition. Bastard.
        if(playStarted){
            Recent recent = new Recent(mediaUri, Integer.valueOf((int)player.getCurrentTime().toMillis()));
            StorageManager.addRecent(recent);
        }
    }
    
    public void playAction(ActionEvent event) {
        logger.fine("playAction");
        startPlaying();
        refreshMetadata();
    }
    
    public void browseHistory(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("History.fxml"));
        try {
            Pane root = (Pane) loader.load();
            HistoryController hc = loader.getController();
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            hc.stage = stage;
            stage.showAndWait();
            //check if user selected something in history window
            if(hc.selected != null) {
                logger.log(Level.FINE, "History jump to {0}", hc.selected);
                reloadToRecent(hc.selected);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        if(file!=null) {
            saveState(); //save position of current file
            mediaUri = file.toURI().toString();
            mediaPosition = 0;

            reloadMedia();
            refreshMetadata();
            saveState(); 
            startPlaying();
        }
    }
    
    public void textEntered(ActionEvent event) {
        String nTime = newTime.getText();
        Duration d = getDurationFromText(nTime);
        if(d!=null && d.lessThan(player.getMedia().getDuration())){
            seekToDuration(d);
        }
    }
    
    public void dragGlobalSlider(Event event) {
        logger.fine("dragGlobalSlider");
        seekToPercent(globalSlider.getValue()); 
    }
    
    public void dragSeconds(MouseEvent event) {
        logger.fine("dragSeconds");
        double currentY=event.getY();
        if(currentY<secY) {
            seekForward(1);
        }
        if(currentY>secY) {
            seekBack(1);
        }
        secY=currentY;
    }
    
    public void clickedSeconds(MouseEvent event) {
        System.out.println(logger.getHandlers().length);
        logger.fine("clickedSeconds");
        secY=event.getY();
    }
    
    public void dragMinutes(MouseEvent event) {
        logger.fine("dragMinutes");
        double currentY=event.getY();
        if(currentY<minY) {
            seekForward(60);
        }
        if(currentY>minY) {
            seekBack(60);
        }
        minY=currentY;
    }
    
    public void clickedMinutes(MouseEvent event) {
        logger.fine("clickedMinutes");
        minY=event.getY();
    }
    
    private void reloadMedia() {
        if(player!=null){
            player.stop();
        }
        try{
            Media media = new Media(mediaUri);
            player = new MediaPlayer(media);
            player.setOnReady(new Runnable() {
                @Override
                public void run() {
                    refreshMetadata();
                }
            });
        } catch (MediaException mx) {
            logger.log(Level.SEVERE, "MediaException on mediaUri {0}", mx.getLocalizedMessage());
            showAlert("Can't load "+mediaUri+".\nError: "+mx.getLocalizedMessage());
        }  
    }
    
    private void seekToPercent(double percent){
        logger.fine("Seeking to "+percent+"%");
        seekToDuration(player.getMedia().getDuration().multiply(percent/100));
    }
    
    private void seekForward(int seconds){
        logger.fine("Seeking forward "+seconds+"s to ");
        seekToDuration(player.getCurrentTime().add(Duration.seconds(seconds)));
    }
    
    private void seekBack(int seconds){
        logger.fine("Seeking back "+seconds+"s");
        seekToDuration(player.getCurrentTime().subtract(Duration.seconds(seconds)));
    }
    
    public void stopAction(ActionEvent event) {
        if(player.getStatus().equals(Status.PAUSED)){
            player.play();
        } else {
            player.pause();
        }
    }
       
    private void refreshView(){
        if(player!=null){
            timeSeconds.setText(Formatter.formatDuration(player.getCurrentTime(), "ss"));
            timeMinutes.setText(Formatter.formatDuration(player.getCurrentTime(), "mm"));
            timeHours.setText(Formatter.formatDuration(player.getCurrentTime(), "HH"));
            globalSlider.setValue(100*player.getCurrentTime().toMillis()/player.getMedia().getDuration().toMillis());
            statusLabel.setText(player.getStatus().toString());
            totalTime.setText(Formatter.formatDuration(player.getMedia().getDuration()));
        }
    }
    
    private void refreshMetadata(){
        if(player == null ){
            return;
        }
        ObservableMap<String,Object> meta = player.getMedia().getMetadata();
        ObservableList<MetaData> data = FXCollections.observableArrayList();
        
        //add file name
        data.add(getFilenameMetaData());
                
        //add metadata from media file
        for(ObservableMap.Entry<String,Object> pair : meta.entrySet()){
            if(!metaToIgnore.contains(pair.getKey())){
                MetaData md = new MetaData();
                md.metaKey.set(pair.getKey());
                md.metaValue.set(pair.getValue().toString());
                data.add(md);
            }
        }
        tableView.setItems(data);
    }
    
    private MetaData getFilenameMetaData() {
        File file=new File(player.getMedia().getSource());
        MetaData fileMD = new MetaData();
        fileMD.metaKey.set("Filename");
        fileMD.metaValue.set(file.getName());
        return fileMD;
    }

    private Duration getDurationFromText(String nTime) throws NumberFormatException {
        Duration d=null;
        Matcher m =hmsPattern.matcher(nTime);
        if(m.find()){
            int hour=Integer.parseInt(m.group(1));
            int min=Integer.parseInt(m.group(2));
            int sec=Integer.parseInt(m.group(3));
            d=new Duration(1000*(3600*hour+60*min+sec));
            logger.log(Level.FINE, "New Duration {0} which is {1}", new Object[]{d, Formatter.formatDuration(d)});
        } else {
            m =msPattern.matcher(nTime);
            if(m.find()){
                int min=Integer.parseInt(m.group(1));
                int sec=Integer.parseInt(m.group(2));
                d=new Duration(1000*(60*min+sec));
                logger.log(Level.FINE, "New Duration {0} which is {1}", new Object[]{d, Formatter.formatDuration(d)});
            }
        }
        return d;
    }

    private void startUIUpdate() {
        time.setCycleCount(Timeline.INDEFINITE);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshView();
            }
        });
        time.getKeyFrames().add(keyFrame);
        time.play();
    }

    private void startPlaying() {
        player.stop();
        player.setStartTime(new Duration(mediaPosition));
        player.play();
        playStarted=true;
    }

    private void reloadToRecent(Recent recent) {
        saveState();
        if(player!=null) player.stop();
        mediaUri = recent.getMediaUri();
        mediaPosition = recent.getPosition();
        reloadMedia();
        refreshMetadata();
        startPlaying();
    }

    private void seekToDuration(Duration d) {
        player.setStartTime(Duration.ZERO);//reset start position so we can seek back
        player.seek(d);
    }
    
    private void showAlert(String text){
        final Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button button = new Button("Ok");
        button.setOnAction(new EventHandler(){
            @Override
            public void handle(Event t) {
                dialogStage.hide();
            }
        });
        dialogStage.setScene(new Scene(VBoxBuilder.create().
            children(new Text(text), button).
            alignment(Pos.CENTER).padding(new Insets(5)).build()));
        dialogStage.showAndWait();
    }
}
