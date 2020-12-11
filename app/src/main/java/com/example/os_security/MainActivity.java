package com.example.os_security;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private boolean isRooted(){ // 1) su 명령어 사용이 가능한지 아닌지로 rooting 판별
        boolean flag = false;
        try{
            Runtime.getRuntime().exec("su");
            flag = true;
        }catch(Exception e){
            flag = false;
        }

        if(flag){
            return true;
        }else{
            return false;
        }
    }
    private static boolean checkBuildTag() { // 2) build tag가 test 값인지 확인
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }
    private boolean findSuperuserFile() { // 3) 루팅이 의심되는 파일이 있는지 check
        return !(new File("/system/app/Superuser.apk").exists()
                &&
                new File("/system/bin/su").exists()
                &&
                new File("/system/xbin/su").exists()
                &&
                new File("/data/data/com.noshufou.android.su").exists()
                &&
                new File("/data/data/com.devadvance.rootcloak").exists()
                &&
                new File("/data/data/com.deadvance.rootcloakplus").exists()
                &&
                new File("/data/data/com.koushikdutta.superuser").exists()
                &&
                new File("/data/data/com.thirdparty.superuser").exists());
    }
    private boolean checkDirectoryAccessControl(){
        return !(new File("/data").canWrite()
                &&
                new File("/").canWrite()
                &&
                new File("/system").canWrite());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /************************************************/
        // rooting detection (1)(2)(3)(4) : using su command, checking build tags, checking rooting files, checking access control

        if(isRooted() && checkBuildTag() && findSuperuserFile() && checkDirectoryAccessControl()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("This device is rooted device. can't run this app");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok, Sorry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("This device is not looted. Welcome!");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        /************************************************/




    }

}