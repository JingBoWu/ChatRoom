package ChatList;

import StaticClass.StaticVar;

/**
 * Created by k on 16-5-15.
 */
public class MSG{
    int type=-1;
    String msg;
    String username;
    int head;

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public String getUsername() {
        return username;
    }

    public int getHead() {
        return head;
    }

    public int getBubble() {
        return bubble;
    }

    int bubble;
    public void setMSG(int type,String msg){
        setType(type);
        setMsg(msg);
    }
    public void setMSG(int type ,String msg,String username,int head,int bubble){
        setType(type);
        setMsg(msg);
        setUsername(username);
        setHead(head);
        setBubble(bubble);
    }
    public void setType(int type) {
        this.type = type;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public void setBubble(int bubble) {
        this.bubble = bubble;
    }

    public String toString(){
        String str="";
        switch (type){
            case 2:
            case 0:
                str+=type+ StaticVar.Split_Str;
                str+=msg+StaticVar.Split_Str;
                str+=username;
                break;
            case 1:
                str+=type+ StaticVar.Split_Str;
                str+=msg+ StaticVar.Split_Str;
                str+=username+ StaticVar.Split_Str;
                str+=head+ StaticVar.Split_Str;
                str+=bubble+ StaticVar.Split_Str;
                break;
        }
        return str;
    }
}
