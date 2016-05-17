package com.example.k.chatroomapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import StaticClass.NetManager;
import StaticClass.StaticVar;
import User.UserInfo;
import ChatList.MSG;
import ChatList.Chat_adapter;


public class ChatActivity extends Activity {
    Context context;

    Button postBt;
    EditText msgEt;
    ListView chatList;
    boolean firstSend=true;
    boolean getFlag=true;

    MSG lastSendMsg=new MSG();
    MSG newMsg=new MSG();
    MSG exitMsg=new MSG();

    ArrayList<MSG> msgs=new ArrayList<>();
    Chat_adapter chat_adapter;

    HandlerThread mhandlerthread;
    Handler mhandler;
    boolean getthreadrunning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }
    void init(){
        setTitle(UserInfo.getPreroom().get("roomname"));
        initUI();
        String exitmsg=StaticVar.prefix+UserInfo.getUsername()+StaticVar.exitMsg;
        exitMsg.setMSG(2,exitmsg);
        context=getApplicationContext();
        initList();
        sendFirstText();
    }
    void initUI(){
        postBt=(Button)findViewById(R.id.postBt);
        msgEt=(EditText)findViewById(R.id.msgEt);
        chatList=(ListView)findViewById(R.id.chatList);
        postBt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendBtClick();
            }
        });
    }
    void initList(){
        chat_adapter=new Chat_adapter(ChatActivity.this,msgs);
        chatList.setAdapter(chat_adapter);
    }
    void sendFirstText(){
        sendPost();
    }

    void updateList(){
        MSG temp=new MSG();
        if(newMsg.getType()==1){
            temp.setMSG(newMsg.getType(),newMsg.getMsg(),newMsg.getUsername(),newMsg.getHead(),newMsg.getBubble());
        }else{
            temp.setMSG(newMsg.getType(),newMsg.getMsg());
        }
        msgs.add(temp);
        chat_adapter.notifyDataSetChanged();
        chatList.setSelection(msgs.size()-1);
    }
    void sendBtClick(){
        String msg_et=msgEt.getText().toString();
        msgEt.setText("");
        lastSendMsg.setMSG(1,msg_et,UserInfo.getUsername(),UserInfo.getHead(),UserInfo.getBubbe());
        sendPost();
    }
    void startGetThread(){

        mhandlerthread=new HandlerThread("GetThread");
        mhandlerthread.start();
        mhandler=new Handler(mhandlerthread.getLooper());
        mhandler.post(getpostthread);
    }






   /*
   * send meaasge
   *
   * */
    Handler sendhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            sendHandleResult(msg.obj.toString());
        }
    };
    void sendHandleResult(String result){
        if(result.equals(StaticVar.FAIL)){
            Toast.makeText(ChatActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
        }else{
            if(firstSend) {
                firstSend = false;
                getPost();
            }
        }
    }
    void sendPost(){
        if(firstSend) {
            String msg=StaticVar.prefix+UserInfo.getUsername()+StaticVar.enterMsg;
            lastSendMsg.setMSG(0,msg);
        }
        if(NetManager.isNetConnected(context)) {
            new Thread(sendpostthread).start();
        }else{
            Toast.makeText(ChatActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }
    Runnable sendpostthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.Chat;
                HttpURLConnection urlconn=(HttpURLConnection) new URL(url).openConnection();
                urlconn.setDoOutput(true);
                urlconn.setDoInput(true);
                urlconn.setRequestMethod("POST");
                urlconn.setUseCaches(false);
                urlconn.setInstanceFollowRedirects(true);
                urlconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");


                String params="chatroomid="+UserInfo.getPreroom().get("roomid")
                        +"&"+"MSG="+lastSendMsg.toString();
//                Log.i("param",params);
                byte[] data=params.getBytes();
//                String data=new String(params.getBytes(),"UTF-8");
//                Log.i("param2",data);
                urlconn.connect();
                DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
                out.write(data);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null){
                    result+=line;
                }

                Message msg=new Message();
                msg.obj=result;
                sendhandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    /*
    *
    * get message
    *
    *
    * */

    Handler gethandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            getHandleResult(msg.obj.toString());
        }
    };
    void getHandleResult(String result){
        if(result.equals("")||result.length()==0){
            return ;
        }
        String[] strArr=result.split(StaticVar.Split_Str);
        if(strArr.length!=0){
            int type=Integer.parseInt(strArr[0].replace("\n",""));
            String msg=strArr[1];
            newMsg.setType(type);
            newMsg.setMsg(msg);
            if(type==1){
                String username=strArr[2];
                int head=Integer.parseInt(strArr[3].replace("\n",""));
                int bubble=Integer.parseInt(strArr[4].replace("\n",""));
                newMsg.setUsername(username);
                newMsg.setHead(head);
                newMsg.setBubble(bubble);
            }
            updateList();
            UserInfo.addPreNum();
            getFlag=true;
        }
    }
    void getPost(){
        if(NetManager.isNetConnected(context)) {
            startGetThread();
        }else{
            Toast.makeText(ChatActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }
    Runnable getpostthread=new Runnable() {
        @Override
        public void run() {
            while (getthreadrunning) {
                try {
                    String result = "";
                    String url = "http://" + StaticVar.IP_ADDR + "/" + StaticVar.WEB_APP + "/" + StaticVar.ChatGet;
                    HttpURLConnection urlconn = (HttpURLConnection) new URL(url).openConnection();
                    urlconn.setDoOutput(true);
                    urlconn.setDoInput(true);
                    urlconn.setRequestMethod("POST");
                    urlconn.setUseCaches(false);
                    urlconn.setInstanceFollowRedirects(true);
                    urlconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    String params = "chatroomid=" + UserInfo.getPreroom().get("roomid")
                            + "&" + "newestid=" + UserInfo.getPreNum();


                    String data = params;
//                    new String(params.getBytes(), "UTF-8");

                    urlconn.connect();
                    DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
                    out.writeBytes(data);
                    out.flush();
                    out.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
//                        String str=new String(line.getBytes(),"UTF-8");
//                        result += str;
                        result+=line;
                        result+="\n";
                    }

                    Message msg = new Message();
                    msg.obj = result;
                    gethandler.sendMessage(msg);
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /*
    *
    * exitEvent
    *
    *
    * */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            // 创建退出对话框

            AlertDialog isExit = new AlertDialog.Builder(this).create();

            // 设置对话框标题

            isExit.setTitle("提示");

            // 设置对话框消息

            isExit.setMessage("确定要退出吗");

            // 添加选择按钮并注册监听

            isExit.setButton("确定", back_listener);

            isExit.setButton2("取消", back_listener);

            // 显示对话框

            isExit.show();
        }
        return super.onKeyDown(keyCode, event);
    }
    DialogInterface.OnClickListener back_listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    exitActivity();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
    void exitActivity(){
        startDialog();
        doExitPost();
    }
    Handler exithandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            exitHandleResult(msg.obj.toString());
        }
    };
    void exitHandleResult(String result){
        stopDialog();
        if(result.equals(StaticVar.FAIL)){
            Toast.makeText(ChatActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
        }else{
            ChatActivity.this.finish();
        }
    }
    void doExitPost(){
        if(NetManager.isNetConnected(context)) {
            new Thread(exitpostthread).start();
        }else{
            stopDialog();
            Toast.makeText(ChatActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }
    Runnable exitpostthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.Chat;
                HttpURLConnection urlconn=(HttpURLConnection) new URL(url).openConnection();
                urlconn.setDoOutput(true);
                urlconn.setDoInput(true);
                urlconn.setRequestMethod("POST");
                urlconn.setUseCaches(false);
                urlconn.setInstanceFollowRedirects(true);
                urlconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");


                String params="chatroomid="+UserInfo.getPreroom().get("roomid")
                        +"&"+"MSG="+exitMsg.toString();

                byte[] data=params.getBytes();
                urlconn.connect();
                DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
                out.write(data);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null){
                    result+=line;
                }

                Message msg=new Message();
                msg.obj=result;
                exithandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    void startDialog(){
        progressDialog= ProgressDialog.show(ChatActivity.this,null,
                getResources().getString(R.string.pleasewait));
    }
    void stopDialog(){
        progressDialog.dismiss();
    }
    ProgressDialog progressDialog;

    @Override
    protected void onResume() {
        getthreadrunning=true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        getthreadrunning=false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        getthreadrunning=false;
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        mhandler.removeCallbacks(getpostthread);
        super.onDestroy();
    }
}

