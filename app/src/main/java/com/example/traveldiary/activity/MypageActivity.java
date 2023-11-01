package com.example.traveldiary.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.traveldiary.R;
import com.example.traveldiary.SHA256;
import com.example.traveldiary.dialog.ProgressDialog;
import com.example.traveldiary.fragment.FragmentBoard;
import com.example.traveldiary.fragment.FragmentBookmark;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MypageActivity extends AppCompatActivity {
    FragmentBoard fragmentBoard;
    FragmentBookmark fragmentBookmark;
    @SuppressLint("StaticFieldLeak")
    public static TextView postCount;
    @SuppressLint("StaticFieldLeak")
    public static TextView bookmarkCount;
    private FragmentManager fragmentManager;
    private Uri filePath;
    private DatabaseReference mDatabase;
    private ImageView profileImage;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean isImage;
    ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        TextView nickName = findViewById(R.id.nickName);
        mDatabase = FirebaseDatabase.getInstance().getReference("UI");
        mDatabase.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("info").child("userNickName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickName.setText(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        postCount = findViewById(R.id.postCount);
        bookmarkCount = findViewById(R.id.bookmarkCount);
        loadDataCount();    //bookmark 게시물 개수 불러오기.
        fragmentManager = getSupportFragmentManager();

        ImageView profile = findViewById(R.id.imageView);
        profile.setClipToOutline(true);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Profile/" + FirebaseAuth.getInstance().getUid()).child("ProfileImage.jpg").getDownloadUrl().addOnSuccessListener(command ->
                Glide.with(this)
                        .load(command)
                        .into(profile));
        findViewById(R.id.settingBtn).setOnClickListener(v -> onClickSelected());

        fragmentBoard = new FragmentBoard();
        fragmentManager.beginTransaction().replace(R.id.container, fragmentBoard).commit();

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(R.string.mypage_boarder));
        tabs.addTab(tabs.newTab().setText(R.string.mypage_bookmark));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // position = 0: fragmentBoard 의미함.
                    if (fragmentBoard == null) {
                        fragmentBoard = new FragmentBoard();
                        fragmentManager.beginTransaction().add(R.id.container, fragmentBoard).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .show(fragmentBoard)
                                .hide(fragmentBookmark)
                                .commit();
                    }
                } else {
                    if (fragmentBookmark == null) {
                        fragmentBookmark = new FragmentBookmark();
                        fragmentManager.beginTransaction().add(R.id.container, fragmentBookmark).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .show(fragmentBookmark)
                                .hide(fragmentBoard)
                                .commit();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result != null) {
                        Glide
                                .with(getApplicationContext()).load(result.getData().getData())
                                .centerCrop()
                                .into(profileImage);
                        filePath = result.getData().getData();
                        isImage = true;
                    }
                });

        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customProgressDialog.setCancelable(false);

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainViewActivity.class));

        });

        ImageView upload = findViewById(R.id.upload);
        upload.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, UploadCalendarActivity.class));
        });
    }

    //bookmark 게시물 개수를 불러오는 코드
    private void loadDataCount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").whereArrayContains("bookmark", Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().
                addOnSuccessListener(queryDocumentSnapshots -> bookmarkCount.setText(String.valueOf(queryDocumentSnapshots.size())));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //MypageActivity가 종료되었을때 fragment instance를 종료시킴
        if (fragmentBoard != null) {
            fragmentBoard.onDestroy();
        }
        if (fragmentBookmark != null) {
            fragmentBookmark.onDestroy();
        }
        // static 변수로 선언된 게시물 수와 북마크 수를 종료시킴.
        postCount = null;
        bookmarkCount = null;
    }

    // 설정 화면 output Dialog
    public void onClickSelected() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_setting);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        dialog.show();

        dialog.findViewById(R.id.noticeLayout).setOnClickListener(v -> {
            startActivity(new Intent(this, NoticeActivity.class));
            dialog.dismiss();
        });

        dialog.findViewById(R.id.changeProfileLayout).setOnClickListener(v -> {
            onClickSelectedChangeProfile();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.changePasswordLayout).setOnClickListener(v -> {
            onClickSelectedChangePw();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.termsLayout).setOnClickListener(v -> Toast.makeText(this, "안알랴줌", Toast.LENGTH_SHORT).show());

        dialog.findViewById(R.id.LogoutLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, StartViewActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), R.string.logout, Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            dialog.dismiss();
            finish();
        });

        dialog.findViewById(R.id.deleteAccountLayout).setOnClickListener(v -> {
            onClickSelectedDeleteAccount();
            dialog.dismiss();
        });
    }

    // 설정 화면에서 '비밀번호 변경' 클릭 시 output 관리하는 Dialog
    private void onClickSelectedChangePw() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        dialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Button confirm_button = dialog.findViewById(R.id.confirm_button);

        confirm_button.setOnClickListener(v -> {
            EditText newPassword = dialog.findViewById(R.id.inputNewPw);
            EditText newPasswordRe = dialog.findViewById(R.id.inputNewRePw);

            SHA256 sha256 = new SHA256();

            String strNewPw;
            String strNewPwRe;
            try {
                strNewPw = sha256.encrypt(sha256.encrypt(newPassword.getText().toString()));
                strNewPwRe = sha256.encrypt(sha256.encrypt(newPasswordRe.getText().toString()));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            if (newPassword.length() > 0 && newPasswordRe.length() > 0) {
                if (strNewPw.equals(strNewPwRe)) {
                    user.updatePassword(strNewPw).addOnSuccessListener(command -> {
                        Intent intent = new Intent(this, MypageActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getApplication(), R.string.mypage_change_pw_success, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            }
        });
    }

    // 설정 화면에서 '프로필 변경' 클릭 시 output 관리하는 Dialog
    private void onClickSelectedChangeProfile() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_profile);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        dialog.show();

        isImage = false;
        TextView nickName = dialog.findViewById(R.id.changeNickNameText);
        profileImage = dialog.findViewById(R.id.profileImage);
        profileImage.setClipToOutline(true);

        loadData(nickName, profileImage);

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            activityResultLauncher.launch(intent);
        });

        dialog.findViewById(R.id.confirm_button).setOnClickListener(v -> changeProfile(nickName.getText().toString(), dialog));
    }

    // 설정 화면에서 '계정 삭제' 클릭 시 output 관리하는 Dialog
    private void onClickSelectedDeleteAccount() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        dialog.show();

        dialog.findViewById(R.id.confirm_button).setOnClickListener(v -> {
            if (((CheckBox) dialog.findViewById(R.id.checkbox)).isChecked()) {
                // 작성한 게시물 삭제하기
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // 북마크 삭제
                db.collection("data").whereArrayContains("bookmark", Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        MyPageValue mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                        List<String> bookmarkArray = mp.getBookmark();
                        bookmarkArray.remove(FirebaseAuth.getInstance().getUid());
                        db.collection("data").document(mp.getBoardID()).update("bookmark", bookmarkArray);
                    }
                });

                // 사용자 이미지 삭제
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference desertRef = storageRef.child("Profile/" + FirebaseAuth.getInstance().getUid() + "/").child("ProfileImage.jpg");
                desertRef.delete();

                // realtime database 및 auth 삭제
                mDatabase.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).removeValue();
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(command -> Toast.makeText(getApplication(), "계정 삭제 완료", Toast.LENGTH_SHORT).show());

                Intent intent = new Intent(this, StartViewActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();
            } else {
                Toast.makeText(this, R.string.mypage_delete_account_isAgree, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // onClickSelectedChangeProfile()에서(프로필 변경) 처음 데이터를 불러올 때 사용하는 메소드
    private void loadData(TextView nickName, ImageView profileImage) {
        mDatabase.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("info").child("userNickName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickName.setText(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Profile/" + FirebaseAuth.getInstance().getUid()).child("ProfileImage.jpg").getDownloadUrl().addOnSuccessListener(command ->
                Glide.with(this)
                        .load(command)
                        .into(profileImage));
    }

    // onClickSelectedChangeProfile()에서(프로필 변경) 닉네임과 이미지를 변경할 때 사용하는 메소드
    public void changeProfile(String nickName, Dialog dialog) {
        customProgressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userNickName", nickName);

        mDatabase.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("info").updateChildren(hashMap);

        if (isImage) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference desertRef = storageRef.child("Profile/" + FirebaseAuth.getInstance().getUid() + "/").child("ProfileImage.jpg");
            desertRef.delete();
            StorageReference imgRef = storageRef.child("Profile").child(FirebaseAuth.getInstance().getUid()).child("ProfileImage" + "." + getFileExtension(filePath));
            imgRef.putFile(filePath).addOnSuccessListener(command -> {
                Intent intent = new Intent(this, MypageActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(this, R.string.mypage_change_profile_success, Toast.LENGTH_SHORT).show();
                customProgressDialog.dismiss();
                dialog.dismiss();
            });
        } else {
            Intent intent = new Intent(this, MypageActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(this, R.string.mypage_change_profile_success, Toast.LENGTH_SHORT).show();
            customProgressDialog.dismiss();
            dialog.dismiss();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}