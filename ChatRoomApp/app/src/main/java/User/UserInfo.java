package User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Pack200;

import StaticClass.StaticVar;

/**
 * Created by k on 16-5-8.
 */
public class UserInfo {
    static String MAC=null;
    static String username=null;
    static int head=0;
    static int bubbe=0;

    static ArrayList<HashMap<String,String>> rooms=new ArrayList<>();
    static HashMap<String,String> preroom=new HashMap<>();
    static int preNum=0;

    public static int getPreNum() {
        return preNum;
    }

    public static void setPreNum(int preNum) {
        UserInfo.preNum = preNum;
    }
    public static void addPreNum(){
        UserInfo.preNum++;
    }



    public static String getMAC() {
        return MAC;
    }

    public static void setMAC(String MAC) {
        UserInfo.MAC = MAC;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserInfo.username = username;
    }

    public static int getHead() {
        return head;
    }

    public static void setHead(int head) {
        UserInfo.head = head;
    }

    public static int getBubbe() {
        return bubbe;
    }

    public static void setBubbe(int bubbe) {
        UserInfo.bubbe = bubbe;
    }
    public static void clearRooms(){
        rooms.clear();
    }

    public static void addRoom(String roomId,String roomname){
        HashMap<String,String> room=new HashMap<>();
        room.put("roomid",roomId);
        room.put("roomname",roomname);
        rooms.add(room);
    }
    public static void setPreroom(String roomId,String roomname){
        preroom.clear();
        preroom.put("roomid",roomId);
        preroom.put("roomname",roomname);
    }
    public static ArrayList<String> getRooms(){
        ArrayList<String> roomnames=new ArrayList<>();
        for(int i=0;i<rooms.size();i++){
            roomnames.add(rooms.get(i).get("roomname")+"("+rooms.get(i).get("roomid")+")");
        }
        return roomnames;
    }

    public static void setPreroombyIndex(int index){
        String roomid=rooms.get(index).get("roomid");
        String roomname=rooms.get(index).get("roomname");
        setPreroom(roomid,roomname);
    }
    public static Map<String,String> getPreroom(){
        return preroom;
    }
    public static ArrayList<String> getRoomIdAndName(){
        ArrayList<String> roompairs=new ArrayList<>();
        for(int i=0;i<rooms.size();i++){
            roompairs.add(rooms.get(i).get("roomid")+StaticVar.Room_ID_NAME_Split+rooms.get(i).get("roomname"));
        }
        return roompairs;
    }
    public static void delRoombyID(String id){
        for(int i=0;i<rooms.size();i++){
            if(rooms.get(i).get("roomid").equals(id)){
                rooms.remove(i);
            }
        }
    }

}
