package cordova.demo.plugin;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cordova.demo.StringUtils;

/**
 * Created by zhengxiaofeng on 2016/7/14.
 */
public class MyPlugin extends CordovaPlugin {
    CallbackContext callbackContext;
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if("call".equals(action)){
            try {
                    final String phone = args.getString(0);
                    if(StringUtils.isEmpty(phone)){
                        Toast.makeText(cordova.getActivity(),"电话号码为空！",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("phone","phone");
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + phone);
                            intent.setData(data);
                            cordova.getActivity().startActivity(intent);
                        }
                    });
                    this.callbackContext.success(jsonObject);
            }catch (Exception e){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("code",-1);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                this.callbackContext.error(jsonObject);
            }
        }
        return true;
    }

}
