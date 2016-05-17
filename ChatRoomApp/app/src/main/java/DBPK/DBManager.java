package DBPK;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by k on 16-5-8.
 */
public class DBManager {
    private DBHelper helper=null;
    private SQLiteDatabase db=null;
    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }
    private String createMAC(){
        String MAC="";
        for(int i=0;i<12;i++){
            MAC+=((int)(Math.random()*10000)%10)+"";
        }
        String insert="insert into UserInfo(MAC) values(?)";
        db.execSQL(insert,new String[]{MAC});
//        Log.i("DB",insert);
        return MAC;
    }
    public String getMACFromDB() {
        String MAC=null;
        String query="select * from UserInfo";
//        Log.i("DB",query+"\n");
        Cursor c=db.rawQuery(query,null);
//        Log.i("DB",c.getCount()+"\n");
        if(c.moveToNext()){
            MAC=c.getString(c.getColumnIndex("MAC"));
        }else{
            MAC=createMAC();
        }
        c.close();
        closeDB();
        return MAC;
    }
    public void closeDB(){
        if(db!=null){
            db.close();
        }
    }
}
