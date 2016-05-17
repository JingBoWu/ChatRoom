package com.example.k.chatroomapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import DBPK.DBManager;
import StaticClass.NetManager;
import StaticClass.StaticVar;
import User.UserInfo;

public class MainActivity extends Activity {
    Context context=null;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            handleResult(msg.obj.toString());
        }
    };
    Handler errorHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            stopDialog();
            Toast.makeText(MainActivity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
        }
    };
    ImageView loginBt;
    TextView regBt;


    public void handleResult(String result){
        String str;
        String[] strArr=result.split(StaticVar.Split_Str);
        if(strArr[0].equals(StaticVar.FAIL)){
            stopDialog();
            str=strArr[1];
            Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG)
                    .show();
        }else{
            UserInfo.setUsername(strArr[0]);
            UserInfo.setHead(Integer.parseInt(strArr[2]));
            UserInfo.setBubbe(Integer.parseInt(strArr[3]));
//            str=StaticVar.OK;
//            Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT)
//                    .show();
            new Thread(roompostthread).start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        DBManager dbmg=new DBManager(MainActivity.this);
        UserInfo.setMAC(dbmg.getMACFromDB());
        initUI();
        context=getApplicationContext();
    }

    public void initUI(){
        loginBt=(ImageView)findViewById(R.id.loginImage);
        loginBt.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                startDialog();
                doPost();
            }
        });
        regBt=(TextView) findViewById(R.id.regTv);
        regBt.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                ToRegActivity();
            }
        });

    }
    void doPost(){
        if(NetManager.isNetConnected(context)) {
            new Thread(postthread).start();
        }else{
            stopDialog();
            Toast.makeText(MainActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }


    Runnable postthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.Login;
                HttpURLConnection urlconn=(HttpURLConnection) new URL(url).openConnection();
                urlconn.setDoOutput(true);
                urlconn.setDoInput(true);
                urlconn.setRequestMethod("POST");
                urlconn.setUseCaches(false);
                urlconn.setInstanceFollowRedirects(true);
                urlconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                String params="MAC="+UserInfo.getMAC();
                if(UserInfo.getMAC().length()!=12){
                    return;
                }
                urlconn.connect();
                DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
                String data= new String(params.getBytes(),"UTF-8");
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
                handler.sendMessage(msg);
            }catch (Exception e){
                Message errormsg=new Message();
                errormsg.obj=e.getMessage();
                errorHandler.sendMessage(errormsg);
                e.printStackTrace();
            }
        }
    };
    void ToRegActivity(){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,RegActivity.class);
        startActivity(intent);
    }

    void ToChatInfoActivity(){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,ChatinfoActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    void startDialog(){
        progressDialog=ProgressDialog.show(MainActivity.this,null,
                getResources().getString(R.string.pleasewait));
    }
    void stopDialog(){
        progressDialog.dismiss();
    }
    ProgressDialog progressDialog;



    Handler roomhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            roomhandleResult(msg.obj.toString());
        }
    };
    public void roomhandleResult(String result){
        stopDialog();
        UserInfo.clearRooms();
        if(result.length()==0){
            Toast.makeText(MainActivity.this,"No Room",Toast.LENGTH_SHORT).show();
        }else {
            String[] strArr = result.split(StaticVar.Split_Str);
            int count = strArr.length;
            for (int i = 0; i < count; i+=2) {
                String roomid = strArr[i];
                String roomname = strArr[i+1];
                UserInfo.addRoom(roomid, roomname);
            }
        }
        ToChatInfoActivity();
    }
    Runnable roompostthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.ChatInfo;
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
                String params="MAC="+ UserInfo.getMAC();
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
                roomhandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

}
