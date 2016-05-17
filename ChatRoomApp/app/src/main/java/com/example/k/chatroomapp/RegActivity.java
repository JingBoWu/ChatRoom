package com.example.k.chatroomapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import StaticClass.NetManager;
import StaticClass.StaticVar;
import User.UserInfo;

public class RegActivity extends Activity {
    Context context=null;
    ImageView headImage;
    ImageView bubbleImage;
    EditText nameEt;
    Button okBt;

    int headIndex=1;
    int bubblrIndex=1;
//    String MAC;
    String name="";

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            handleResult(msg.obj.toString());
        }
    };
    public void handleResult(String result){
        stopDialog();
        String[] strArr=result.split(StaticVar.Split_Str);
        if(strArr[0].equals(StaticVar.FAIL)){
            Toast.makeText(RegActivity.this,strArr[0]+":please try again",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RegActivity.this,strArr[0],Toast.LENGTH_SHORT).show();
            backToMainActivity();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        init();
    }
    void init(){
        initUI();
        context=getApplicationContext();
    }
    void initUI(){
        okBt=(Button)findViewById(R.id.okBt);
        headImage=(ImageView)findViewById(R.id.headImage);
        bubbleImage=(ImageView)findViewById(R.id.bubbleImage);
        nameEt=(EditText)findViewById(R.id.nameEt);

        headImage.setBackground(getResources().getDrawable(StaticVar.Head[headIndex]));
        bubbleImage.setBackground(getResources().getDrawable(StaticVar.Bubbble[bubblrIndex]));

        headImage.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                headIndex++;
                if(headIndex==StaticVar.NumOfHeads)
                    headIndex=1;
                headImage.setBackground(getResources().getDrawable(StaticVar.Head[headIndex]));
            }
        });
        bubbleImage.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                bubblrIndex++;
                if(bubblrIndex==StaticVar.NumofBubble)
                    bubblrIndex=1;
                bubbleImage.setBackground(getResources().getDrawable(StaticVar.Bubbble[bubblrIndex]));            }
        });
        okBt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                startDialog();
                doPost();
            }
        });
        nameEt.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View v) {
                nameEt.setText("");
                nameEt.setOnClickListener(null);
            }
        });
    }
    public void backToMainActivity(){
        Intent intent=new Intent();
        intent.setClass(RegActivity.this,MainActivity.class);
        startActivity(intent);
        RegActivity.this.finish();
    }

    void doPost(){
        name=nameEt.getText().toString();
        if(name.length()==0||name.equals("Name")){
            stopDialog();
            Toast.makeText(RegActivity.this,"请输入名称",Toast.LENGTH_SHORT).show();
            return ;
        }
        UserInfo.setUsername(name);
        UserInfo.setHead(headIndex);
        UserInfo.setBubbe(bubblrIndex);
        if(NetManager.isNetConnected(context)) {
            new Thread(postthread).start();
        }else{
            stopDialog();
            Toast.makeText(RegActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }


    Runnable postthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.Reg;
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
                        +"&"+"username="+UserInfo.getUsername()
                        +"&"+"head="+UserInfo.getHead()
                        +"&"+"bubble="+UserInfo.getBubbe();
//                String data=new String(params.getBytes(),"UTF-8");
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
                handler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    void startDialog(){
        progressDialog= ProgressDialog.show(RegActivity.this,null,
                getResources().getString(R.string.pleasewait));
    }
    void stopDialog(){
        progressDialog.dismiss();
    }
    ProgressDialog progressDialog;
}
