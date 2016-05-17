package com.example.k.chatroomapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RoomMgrActivity extends Activity {
    Context context;

    Button delRoomBt;
    Button addRoomBt;
    Button backBt;
    Spinner room_spinner;
    EditText roomidEt;
    EditText roompwdEt;

    ArrayAdapter<String> spinner_adapter;
    ArrayList<String> roompairs;
    String preRoom=null;

    String enteredRoomid=null;
    String enteredRoomPwd=null;


    Handler delhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            delhandleResult(msg.obj.toString());
        }
    };
    public void delhandleResult(String result){
        stopDialog();
        String[] strArr=result.split(StaticVar.Split_Str);
        if(strArr[0].equals(StaticVar.FAIL)){
            Toast.makeText(RoomMgrActivity.this,strArr[1],Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RoomMgrActivity.this,strArr[0],Toast.LENGTH_SHORT).show();
            UserInfo.delRoombyID(preRoom);
            preRoom=null;
            reflushSpinner();
        }
    }
    Handler addhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            addhandleResult(msg.obj.toString());
        }
    };
    public void addhandleResult(String result){
        stopDialog();
        String[] strArr=result.split(StaticVar.Split_Str);
        if(strArr[0].equals(StaticVar.FAIL)){
            Toast.makeText(RoomMgrActivity.this,strArr[1],Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RoomMgrActivity.this,strArr[0],Toast.LENGTH_SHORT).show();
            UserInfo.addRoom(enteredRoomid,strArr[1]);
            preRoom=null;
            reflushSpinner();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_mgr);
        init();
    }
    void init(){
        initUI();
        context=getApplicationContext();
    }
    void initUI(){
        delRoomBt=(Button)findViewById(R.id.delRoomBt);
        room_spinner=(Spinner)findViewById(R.id.room_spinner);
        roomidEt=(EditText)findViewById(R.id.roomIDEt);
        roompwdEt=(EditText)findViewById(R.id.passwordEt);
        addRoomBt=(Button)findViewById(R.id.addRoomBt);
        backBt=(Button)findViewById(R.id.backBt);
        addRoomToSpinner();

        delRoomBt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                startDialog();
                doDelPost();
            }
        });
        addRoomBt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                startDialog();
                doAddPost();
            }
        });
        backBt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                ToChatInfoActivity();
            }
        });

        room_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] strArr=roompairs.get(position).split(StaticVar.Room_ID_NAME_Split);
                preRoom=strArr[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void reflushSpinner(){
        addRoomToSpinner();
    }
    void addRoomToSpinner(){
        roompairs= UserInfo.getRoomIdAndName();
        spinner_adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,roompairs);
        room_spinner.setAdapter(spinner_adapter);

    }
    void ToChatInfoActivity(){
        Intent intent=new Intent();
        intent.setClass(RoomMgrActivity.this,ChatinfoActivity.class);
        startActivity(intent);
        RoomMgrActivity.this.finish();
    }
    void doDelPost(){
        if(preRoom==null){
            stopDialog();
            Toast.makeText(RoomMgrActivity.this,"请选择房间",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(NetManager.isNetConnected(context)) {
            new Thread(delpostthread).start();
        }else{
            stopDialog();
            Toast.makeText(RoomMgrActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }

    }
    void doAddPost(){
        enteredRoomid=roomidEt.getText().toString();
        enteredRoomPwd=roompwdEt.getText().toString();
        if(enteredRoomPwd.length()!=6||enteredRoomid.length()!=3){
            stopDialog();
            Toast.makeText(RoomMgrActivity.this,"输入格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        if(NetManager.isNetConnected(context)) {
            new Thread(addpostthread).start();
        }else{
            stopDialog();
            Toast.makeText(RoomMgrActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }

    Runnable delpostthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.ChatQuit;
                HttpURLConnection urlconn=(HttpURLConnection) new URL(url).openConnection();
                urlconn.setDoOutput(true);
                urlconn.setDoInput(true);
                urlconn.setRequestMethod("POST");
                urlconn.setUseCaches(false);
                urlconn.setInstanceFollowRedirects(true);
                urlconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                if(UserInfo.getMAC().length()!=12||UserInfo.getUsername().length()==0
                        ||UserInfo.getHead()==0||UserInfo.getBubbe()==0){
                    return;
                }
                String params="MAC="+ UserInfo.getMAC()
                        +"&"+"chatroomid="+preRoom;
                String data=new String(params.getBytes(),"UTF-8");

                urlconn.connect();
                DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null){
                    result+=line;
                }

                Message msg=new Message();
                msg.obj=result;
                delhandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    Runnable addpostthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.ChatReg;
                HttpURLConnection urlconn=(HttpURLConnection) new URL(url).openConnection();
                urlconn.setDoOutput(true);
                urlconn.setDoInput(true);
                urlconn.setRequestMethod("POST");
                urlconn.setUseCaches(false);
                urlconn.setInstanceFollowRedirects(true);
                urlconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                if(UserInfo.getMAC().length()!=12||enteredRoomid.length()!=3){
                    return;
                }
                String params="MAC="+ UserInfo.getMAC()
                        +"&"+"chatroomid="+enteredRoomid
                        +"&"+"password="+enteredRoomPwd;
                String data=new String(params.getBytes(),"UTF-8");

                urlconn.connect();
                DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null){
                    result+=line;
                }

                Message msg=new Message();
                msg.obj=result;
                addhandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    void startDialog(){
        progressDialog= ProgressDialog.show(RoomMgrActivity.this,null,
                getResources().getString(R.string.pleasewait));
    }
    void stopDialog(){
        progressDialog.dismiss();
    }
    ProgressDialog progressDialog;
}
