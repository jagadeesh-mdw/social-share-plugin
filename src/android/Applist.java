package org.jmrezayi2.Applist;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.content.ComponentName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;  
import android.content.Context;
import android.graphics.PixelFormat;
import java.util.List;


public class Applist extends CordovaPlugin {
    public static final String ACTION_ADD_CALENDAR_ENTRY = "addCalendarEntry";
    public static final String ACTION_SHARE = "shareApp";


    // //保存图片png
    // public static void drawableTofile(Drawable drawable,String path)
    // {



    // }


// public static String getDataDir(Context context) throws Exception {
//     return context.getPackageManager()
//             .getPackageInfo(context.getPackageName(), 0)
//             .applicationInfo.dataDir;
// }

    //路径设置 
    public String getSDPath()
     {
            File SDdir=null;
            boolean sdCardExist= Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if(sdCardExist){
                    SDdir=Environment.getExternalStorageDirectory();
            }
            if(SDdir!=null){

                    return SDdir.toString();
            }
            else{
                    return null;
            }
    }


    //获取SD卡路径
    public static void makeRootDirectory(String filePath) {  
        File file = null;  
        try {  
            file = new File(filePath);  
            if (!file.exists()) {
                file.mkdirs();  //make Directory
            }  
        } catch (Exception e) {  
             e.printStackTrace();
        }  
    }


    public static String getImage(Drawable drawable,String path) {
        //cheak exist  or not
        File  cheakfile  = new File( path );
        String base64String   = null;

                if(drawable!=null)                             {
                     //
                    File file = new File(path);
                    Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    base64String = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                    

                    //write the bytes in file
                    // FileOutputStream fos;
                    // try {
                    //     fos = new FileOutputStream(file);
                    //     fos.write(bitmapdata);
                    // } catch (IOException e) {
                    //     // TODO Auto-generated catch block
                    //     e.printStackTrace();
                    // }
                    //    
                }

        return base64String;
    }



    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException 
    {
        if (ACTION_ADD_CALENDAR_ENTRY.equals(action))
        { 
            cordova.getThreadPool().execute(new Runnable() 
            {
                public void run()
                {
                    try
                    {
                        //get a list of installed apps.
                        PackageManager pm = cordova.getActivity().getPackageManager();
                        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
                        
                        JSONArray  app_list = new JSONArray();
                        int cnt =0;
                        String path=getSDPath();
                        makeRootDirectory(path+"/Android/data/com.aqua.build/");
                        makeRootDirectory(path+"/Android/data/com.aqua.build/.icons/");
                        //File dir = cordova.getActivity().getApplicationContext().getFilesDir();
                        for (ApplicationInfo packageInfo : packages) 
                        {
                        String img_name =  "/Android/data/com.aqua.build/.icons/"+ packageInfo.packageName +".png";
                        Drawable icon = pm.getApplicationIcon(packageInfo);

                            if ((packageInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0)
                            {
                                    JSONObject info = new JSONObject();  
                                    info.put("name",packageInfo.loadLabel(pm));//这里获取的是应用名
                                    info.put("package",packageInfo.packageName);
                                    //info.put("icon",packageInfo.loadIcon(pm));
                                    if (packageInfo.packageName.equals("com.google.android.talk")) {
                                        String img_src = Applist.getImage(icon, path+img_name);
                                        info.put("img",img_src);
                                    }

                                    if (packageInfo.packageName.equals("com.android.mms")) {
                                        String img_src = Applist.getImage(icon, path+img_name);
                                        info.put("img",img_src);                                           
                                    }   

                                    if (packageInfo.packageName.equals("com.android.email")) {
                                        String img_src = Applist.getImage(icon, path+img_name);
                                        info.put("img",img_src);                                           
                                    }
                                    if (packageInfo.packageName.equals("com.google.android.gm")) {
                                        String img_src = Applist.getImage(icon, path+img_name);
                                        info.put("img",img_src);                                           
                                    }                                      

                                   app_list.put(cnt++,info);
                            }
                            else
                            {
                                    JSONObject info = new JSONObject();  
                                    info.put("name",packageInfo.loadLabel(pm));//这里获取的是应用名
                                    info.put("package",packageInfo.packageName);
                                    if (packageInfo.packageName.equals("com.whatsapp")) {
                                        String img_src = Applist.getImage(icon, path+img_name);
                                        info.put("img",img_src);                                        
                                    }
                                    if (packageInfo.packageName.equals("com.facebook.katana")) {
                                        String img_src = Applist.getImage(icon, path+img_name);
                                        info.put("img",img_src);                                        
                                    }                                    

                                   app_list.put(cnt++,info);
                            }
                        }
                        callbackContext.success( app_list );
                     } 
                     catch(Exception e) 
                     {
                        System.err.println("Exception: " + e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                }// end of Run Runnable()
            });// end of run getThreadPool()
            return true;
        }
        //
        if (ACTION_SHARE.equals(action))
        {

                JSONObject obj = args.getJSONObject(0);
                String pkg_name = obj.has("package") ? obj.getString("package") : null;
                String text_msg = obj.has("description") ? obj.getString("description") : null;

                Intent sendIntent = new Intent(); 
                sendIntent.setAction(Intent.ACTION_SEND); 
                sendIntent.putExtra(Intent.EXTRA_TEXT, text_msg); 
                sendIntent.setType("text/plain");
                sendIntent.setPackage(pkg_name);
                cordova.getActivity().startActivity(sendIntent);
                return true;            
        }

        callbackContext.error("Invalid action");
        return false;
    } 
}
