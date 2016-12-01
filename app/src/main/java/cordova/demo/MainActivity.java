/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package cordova.demo;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import org.apache.cordova.CordovaActivity;
import org.apache.cordova.event.LoadingEvent;

import de.greenrobot.event.EventBus;

public class MainActivity extends CordovaActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    Dialog dialog;
    public void showProcessDialog(){
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.dialogview, null);

            LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

            // main.xml中的ImageView
            ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
            // 加载动画
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.animation);
            // 使用ImageView显示动画
            spaceshipImage.startAnimation(hyperspaceJumpAnimation);

            dialog = new Dialog(this,
                    R.style.FullHeightDialog);
            dialog.setCancelable(true);
            dialog.setContentView(layout, new LinearLayout.LayoutParams(180,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
      //  }
        dialog.show();
    }
    public void hideProcessDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }


    public void onEventMainThread(LoadingEvent event) {
        if(event.status == LoadingEvent.Started){
            showProcessDialog();
        }
        if(event.status == LoadingEvent.Finished){
            hideProcessDialog();
        }
        if(event.status == LoadingEvent.Error){
            hideProcessDialog();
            Toast.makeText(this,"加载失败，请检查网络",Toast.LENGTH_LONG);
        }
        if(event.status == LoadingEvent.Reload){
            /*cordovaWeight.loadUrl(cordovaWeight.getLaunchUrl());*/
            loadUrl(launchUrl);
        }
        if(event.status == LoadingEvent.closeAPP){
            finish();
        }
    }
}
