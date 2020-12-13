package com.example.os_security;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.Settings;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

//import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.lang.reflect.Method;
import android.annotation.SuppressLint;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    /******************** android.os.SystemProperties는 항상 reflection 방식으로만 접근이 가능하다. */

    public static String get(String key){
        String ret = "";
        try{
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");

            @SuppressWarnings("rawtypes")
            Class[] paramTypes = { String.class };
            Method get = SystemProperties.getMethod("get",paramTypes);
            Object[] params = { key };
            ret = (String)get.invoke(SystemProperties,params);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /********************/


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
    private boolean checkDirectoryAccessControl(){ // 4) 루팅으로 디렉토리의 접근권한이 바뀌는지 check.
        return !(new File("/data").canWrite()
                &&
                new File("/").canWrite()
                &&
                new File("/system").canWrite());
    }
    boolean isProbablyAnEmulator(){ // checking Emulator
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_")) // Android SDK emulator
                && get("service.camera.running").equals("")
                && get("ro.bluetooth.tty").equals("")
                && get("ro.chipname").equals("")
                && get("ro.gps.chip.vendor").equals("")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || "QC_Reference_Phone" == Build.BOARD  //bluestacks
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST.startsWith("Build") //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT == "google_sdk"
                || get("ro.kernel.qemu").equals("1");// another Android SDK emulator check
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /************************************************/
        // emulator detection (1)(2)(3)(4) : using device sensor, using build property

        if(isProbablyAnEmulator()){ // emulator detection
            if(isRooted() && checkBuildTag() && findSuperuserFile() && checkDirectoryAccessControl()){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("This device is emulator and rooted. can't run this app");
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("This device is emulator. can't run this app");
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

        /************************************************/
        // rooting detection (1)(2)(3)(4) : using su command, checking build tags, checking rooting files, checking access control
        // not emulator, but rooted device.
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