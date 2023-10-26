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

/**
 * 이미지에 접근하기 위한 접근 권한 허용 요청을 하는 클래스
 * 권한 허용이 되지 않았을 경우 이미지 업로드가 불가능함.
 *
 * @author Jung
 */
public class PermissionSupport {
    private Context context;
    private Activity activity;
    private String[] permissions = {    // Manifset에 권한을 작성 후 요청할 권한을 배열로 저장
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private List<Object> permissionList;    // 권한 요청을 할 때 발생하는 창에 대한 결과값
    private final int MULTIPLE_PERMISSIONS = 1023;

    public PermissionSupport(Activity _activity, Context _context) {
        this.activity = _activity;
        this.context = _context;
    }

    // 허용할 권한 요청이 남았는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        // 배열로 저장한 권한 중 허용되지 않은 권한이 있는지 체크
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        return permissionList.isEmpty();
    }

    // 권한 허용 요청
    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
    }

    // 권한 요청에 대한 결과 처리
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS && (grantResults.length > 0))
            for (int grantResult : grantResults) // grantResults == 0 사용자가 허용한 것 / grantResults == -1 사용자가 거부한 것
                if (grantResult == -1) return false;
        return true;
    }
}
