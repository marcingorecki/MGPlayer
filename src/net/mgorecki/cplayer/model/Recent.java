/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer.model;

import java.io.Serializable;
import java.util.Date;
import javafx.util.Duration;
import net.mgorecki.cplayer.Formatter;

/**
 *
 * @author marcin
 */
public class Recent implements Serializable {
    private String mediaUri;
    private Integer position;
    private Date timestamp;
    static final long serialVersionUID = 6744584542352960359L;

    public Recent(String mediaUri, Integer position) {
        this.mediaUri = mediaUri;
        this.position = position;
        timestamp = new Date();
    }
    /**
     * @return the mediaUril
     */
    public String getMediaUri() {
        return mediaUri;
    }

    /**
     * @param mediaUril the mediaUril to set
     */
    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    /**
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPositionFormatted() {
        return Formatter.formatDuration(new Duration(position));
    }

    public String getTimestampFormatted() {
        return Formatter.formatDate(timestamp);
    }
    
    @Override
    public String toString(){
        return "Recent = {mediaUri= "+mediaUri+"; position="+position+"; timestamp="+timestamp+"}";
    }
}
