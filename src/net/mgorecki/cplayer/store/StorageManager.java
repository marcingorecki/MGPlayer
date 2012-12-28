/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbm.PrimaryMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import net.mgorecki.cplayer.model.Recent;

/**
 *
 * @author marcin
 */
public class StorageManager {
    
    private static final String dbFileName = "playerDataStore";
    private static final String PLAYER = "player";
    private static final String RECENT = "recentList";
    private static RecordManager recMan;
    
    static{
        try {
            recMan = RecordManagerFactory.createRecordManager(dbFileName);
        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static boolean addRecent(Recent recent) {
        PrimaryMap<String, List<Recent>> player = recMan.hashMap(PLAYER);
        List<Recent> list = player.get(RECENT);
        if(list==null){
            list=new ArrayList<>();
        }
        //check if already on the list
        for(Recent r : list) {
            if(r.getMediaUri().equals(recent.getMediaUri())){
                list.remove(r);
                break;
            }
        }
        list.add(recent);
        player.put(RECENT, list);
        try {
            recMan.commit();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static Recent getRecent() {
        PrimaryMap<String, List<Recent>> player = recMan.hashMap(PLAYER);
        List<Recent> list = player.get(RECENT);
        long latest = 0;
        Recent recent=null;
        if(list!=null){
            for(Recent r:list){
                if(r.getTimestamp().getTime()>latest){
                    recent=r;
                    latest=r.getTimestamp().getTime();
                }
            }
        }
        return recent;
    }
    
    public static List<Recent> getAllRecent() {
        PrimaryMap<String, List<Recent>> player = recMan.hashMap(PLAYER);
        List<Recent> list = player.get(RECENT);
        return list;
    }
}
