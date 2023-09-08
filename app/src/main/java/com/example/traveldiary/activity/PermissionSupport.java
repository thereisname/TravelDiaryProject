package com.example.traveldiary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {

    private Context context;
    private Activity activity;

    // Manifset에 권한을 작성 후
    // 요청할 권한을 배열로 저장
    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,


    };
    // 권한 요청을 할 때 발생하는 창에 대한 결과값
    private List<Object> permissionList;

    private final int MULTIPLE_PERMISSIONS = 1023 ;


    public PermissionSupport(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    // 허용할 권한 요청이 남았는지 체크
    public boolean checkPermission(){
        int result;
        permissionList = new ArrayList<>();

        // 배열로 저장한 권한 중 허용되지 않은 권한이 있는지 체크
        for (String pm : permissions){
            result = ContextCompat.checkSelfPermission(context, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }

        return permissionList.isEmpty();
    }

    // 권한 허용 요청
    public  void requestPermission(){
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]),
                MULTIPLE_PERMISSIONS);
    }

    // 권한 요청에 대한 결과 처리
    public boolean permissionResult(int requestCode , @NonNull String[] permissions, @NonNull int[] grantResults){


        if (requestCode == MULTIPLE_PERMISSIONS && (grantResults.length > 0)){
            for(int i =0; i < grantResults.length ; i ++){

                // grantResults == 0 사용자가 허용한 것
                // grantResults == -1 사용자가 거부한 것
                if(grantResults[i] == -1){
                    return false;
                }
            }
        }
        return true;
    }


}
