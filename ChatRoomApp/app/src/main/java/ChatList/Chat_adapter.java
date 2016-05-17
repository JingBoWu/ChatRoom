package ChatList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.k.chatroomapp.R;

import java.util.ArrayList;

import StaticClass.StaticVar;

/**
 * Created by k on 16-5-15.
 */
public class Chat_adapter extends BaseAdapter {
    Context context_adapter;
    LayoutInflater myinflate;
    ArrayList<MSG> msgs_adapter;
    RelativeLayout relativeLayout;
    public Chat_adapter(Context context, ArrayList<MSG> msgs){
        context_adapter=context;
        this.myinflate=LayoutInflater.from(context);
        this.msgs_adapter=msgs;
    }
    @Override
    public int getCount() {
        return msgs_adapter.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs_adapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(msgs_adapter.get(position).getType()==1){
            relativeLayout=(RelativeLayout)myinflate.inflate(R.layout.list_msg_layout,null);
            Chat_Viewholder chatholder=new Chat_Viewholder();
            chatholder.headImage=(ImageView)relativeLayout.findViewById(R.id.headImage);
            chatholder.username=(TextView)relativeLayout.findViewById(R.id.usernameTv);
            chatholder.msg=(TextView)relativeLayout.findViewById(R.id.msgTv);
            int head=msgs_adapter.get(position).getHead();
            String username=msgs_adapter.get(position).getUsername();
            String msg_here=msgs_adapter.get(position).getMsg();
            int bubble=msgs_adapter.get(position).getBubble();

            chatholder.headImage.setBackground(context_adapter.getResources().getDrawable(StaticVar.Head[head]));
            chatholder.username.setText(username);
            chatholder.msg.setBackground(context_adapter.getResources().getDrawable(StaticVar.Bubbble[bubble]));
            chatholder.msg.setText(msg_here);
        }else{
            MSG_Viewhoder msgholder=new MSG_Viewhoder();
            relativeLayout=(RelativeLayout) myinflate.inflate(R.layout.list_sys_msg_layout,null);
            msgholder.msg=(TextView)relativeLayout.findViewById(R.id.sys_msg);
            String msg_sys=msgs_adapter.get(position).getMsg();
            msgholder.msg.setText(msg_sys);
        }
        return relativeLayout;
    }
    class Chat_Viewholder{
        public ImageView headImage;
        public TextView username;
        public TextView msg;
    }
    class MSG_Viewhoder{
        public TextView msg;
    }
}
