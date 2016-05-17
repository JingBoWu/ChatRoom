package com.example.k.chatroomapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class ChatinfoActivity extends Activity {
    Context context;
    ListView roomList;
    Button managerBt;
    ImageView headImage;
    TextView usernameTv;

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
            Toast.makeText(ChatinfoActivity.this,strArr[0],Toast.LENGTH_SHORT).show();
        }else{
            int num=Integer.parseInt(strArr[0]);
            if(num>10){
                num-=10;
            }
            UserInfo.setPreNum(num);
            ToChatActivity();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatinfo);
        init();

    }
    void init(){
        initUI();
        context=getApplicationContext();
    }
    void initUI(){
        usernameTv=(TextView)findViewById(R.id.usernameofcharinfo);
        headImage=(ImageView)findViewById(R.id.headImageofchatinfo);
        usernameTv.setText(UserInfo.getUsername());
        headImage.setBackground(getResources().getDrawable(StaticVar.Head[UserInfo.getHead()]));
        roomList=(ListView)findViewById(R.id.chatroomlist);
        managerBt=(Button)findViewById(R.id.managerBt);
        managerBt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                ToRoomMgrActivity();
            }
        });
        ArrayList<String> roomnames=UserInfo.getRooms();
        ChatInfoAdapter myadapter = new ChatInfoAdapter(ChatinfoActivity.this,roomnames);
        roomList.setAdapter(myadapter);


    }
    void ToRoomMgrActivity(){
        Intent intent=new Intent();
        intent.setClass(ChatinfoActivity.this,RoomMgrActivity.class);
        startActivity(intent);
        ChatinfoActivity.this.finish();
    }
    void ToChatActivity(){
//        Toast.makeText(ChatinfoActivity.this,"To chat activity",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();
        intent.setClass(ChatinfoActivity.this,ChatActivity.class);
        startActivity(intent);
    }

    void handeListClick(int postion){
        startDialog();
        UserInfo.setPreroombyIndex(postion);
        doPost();
//        ToChatActivity();
    }

    class ChatInfoAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<String> roomnames;
        public ChatInfoAdapter(Context context, ArrayList<String> rooms){
            this.mInflater = LayoutInflater.from(context);
            roomnames=rooms;
        }

        @Override
        public int getCount() {
            return roomnames.size();
        }


        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ChatRoom_ViewHolder holder;
            if(convertView==null){
                holder=new ChatRoom_ViewHolder();
                convertView=mInflater.inflate(R.layout.list_chatroom,null);
                holder.roomNameTv=(TextView)convertView.findViewById(R.id.ChatRoomName);
                holder.enterImage=(ImageView)convertView.findViewById(R.id.enterRoom);
                convertView.setTag(holder);

            }else{
                holder=(ChatRoom_ViewHolder)convertView.getTag();
            }
            holder.roomNameTv.setText(roomnames.get(position));
            holder.enterImage.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    handeListClick(position);
                }
            });
            return convertView;
        }
        class ChatRoom_ViewHolder{
            public TextView roomNameTv;
            public ImageView enterImage;
        }

    }



    void doPost(){
        if(NetManager.isNetConnected(context)) {
            new Thread(postthread).start();
        }else{
            stopDialog();
            Toast.makeText(ChatinfoActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
        }
    }
    Runnable postthread=new Runnable() {
        @Override
        public void run() {
            try{
                String result="";
                String url="http://"+ StaticVar.IP_ADDR+"/"+StaticVar.WEB_APP+"/"+StaticVar.ChatNum;
                HttpURLConnection urlconn=(HttpURLConnection) new URL(url).openConnection();
                urlconn.setDoOutput(true);
                urlconn.setDoInput(true);
                urlconn.setRequestMethod("POST");
                urlconn.setUseCaches(false);
                urlconn.setInstanceFollowRedirects(true);
                urlconn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                if(UserInfo.getPreroom().get("roomid").length()!=3){
                    return;
                }
                String params="chatroomid="+UserInfo.getPreroom().get("roomid");
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
                handler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    void startDialog(){
        progressDialog= ProgressDialog.show(ChatinfoActivity.this,null,
                getResources().getString(R.string.pleasewait));
    }
    void stopDialog(){
        progressDialog.dismiss();
    }
    ProgressDialog progressDialog;
}

