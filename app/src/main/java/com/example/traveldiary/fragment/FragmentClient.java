package com.example.traveldiary.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentClient extends Fragment implements OnMapReadyCallback {
    private MapView googlemap = null;
    private GoogleMap map; // 추가: GoogleMap 객체를 저장하는 변수
    private FusedLocationProviderClient fusedLocationClient;
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private static MyPageValue mp;
    private LinearLayout listView, routeView;
    ImageView imageView;
    TextView routeTitle;
    FirebaseFirestore db;
    ArrayList<Integer> arrayStartIndex = new ArrayList<Integer>();
    ArrayList<Integer> arrayEndIndex = new ArrayList<Integer>();
    ArrayList<View> arrayimage = new ArrayList();
    ArrayList<String> arrayroute = new ArrayList();
    ArrayList<Double> arraylat = new ArrayList<>();
    ArrayList<Double> arraylon = new ArrayList<>();
    TextView fragment_title;
    TextView fragment_hashtag;
    int clickCheck = 0;

    public FragmentClient() {
    }

    public static FragmentClient newInstance(MyPageValue myPageValue) {
        FragmentClient fragment = new FragmentClient();
        Bundle args = new Bundle();
        args.putParcelable("myPageValue", (Parcelable) myPageValue);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);
        fragment_title = view.findViewById(R.id.fragment_title);
        fragment_hashtag = view.findViewById(R.id.fragment_hashtag);

        //리스트뷰 content와 image 담아두는 곳
        listView = view.findViewById(R.id.listView);
        routeView = view.findViewById(R.id.routeView);
        routeTitle = view.findViewById(R.id.routeTitle);
        googlemap = (MapView) view.findViewById(R.id.map);
        googlemap.onCreate(savedInstanceState); // 지도의 onCreate 호출
        // Firebase에서 데이터 가져오는 함수
        fetchDataFromFirebase();

        listView.setVisibility(View.VISIBLE);
        routeView.setVisibility(View.GONE);
        routeTitle.setVisibility(View.GONE);
        googlemap.setVisibility(View.GONE);

        ImageButton nextBtn = view.findViewById(R.id.imgBtn_next);
        ImageButton beforeBtn = view.findViewById(R.id.imgBtn_before);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "다음페이지 보여줘", Toast.LENGTH_SHORT).show();
                listView.setVisibility(View.GONE);
                routeView.setVisibility(View.VISIBLE);
                routeTitle.setVisibility(View.VISIBLE);
                googlemap.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
                beforeBtn.setVisibility(View.VISIBLE);
            }
        });
        beforeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                routeView.setVisibility(View.GONE);
                routeTitle.setVisibility(View.GONE);
                googlemap.setVisibility(View.GONE);
                nextBtn.setVisibility(View.VISIBLE);
                beforeBtn.setVisibility(View.GONE);
            }
        });

        return view;
    }

    //Map 생명주기
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (googlemap != null) {
            googlemap.onResume(); // 지도의 onResume 호출
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map != null) {
            googlemap.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (map != null) {
            googlemap.onPause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (map != null) {
            googlemap.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (map != null) {
            googlemap.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (map != null) {
            googlemap.onLowMemory();
        }
    }


    // 문자에서 이미지 시작과 끝을 가져오기
    private void checkText(MyPageValue mp) {
        String str = mp.getCon();

        for (int index = 0; index < str.length(); index++) {
            if (str.charAt(index) == '<' && str.charAt(index + 1) == 'i' && str.charAt(index + 2) == 'm') {
                arrayStartIndex.add(index);
            }
            if (str.charAt(index) == '>' && str.charAt(index - 1) == '"' && str.charAt(index - 2) == '0') {
                arrayEndIndex.add(index);
            }
        }
        // 이미지 가져오기
        if (arrayStartIndex.size() == 0) {
            createTextView(mp.getCon());
        } else {
            if (arrayStartIndex.get(0) != 0) {
                String str0 = mp.getCon().substring(0, arrayStartIndex.get(0));
                createTextView(str0);
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                        createTextView(str1);
                    } else {
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str1);
                    }
                }
            } else {
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str2 = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                        createTextView(str2);
                    } else {
                        String str3 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str3);
                    }
                }
            }
        }
        try {
            Imagedown(mp.getBoardID());
        } catch (Exception e) {

        }
    }

    // Image 다운로드 함수
    private void Imagedown(String boardID) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Image/" + boardID).listAll().addOnSuccessListener(listResult -> {
            for (int i = 1; i < listResult.getItems().size(); i++) {
                StorageReference item = listResult.getItems().get(i);
                int finalI = i - 1;
                item.getDownloadUrl().addOnSuccessListener(command -> {
                    if (arrayimage.size() > finalI) {
                        //Glide를 onActiviytyCreated 내에서 사용
                        if (getActivity() != null) {
                            Glide.with(getActivity()).load(command).into(((ImageView) arrayimage.get(finalI)));
                        }
                    }
                });
            }
        });
    }

    int imageId = 10000;

    private void createImageView() {
        imageView = new ImageView(getContext());
        imageView.setId(imageId);
        Glide.with(getContext()).load(R.drawable.baseline_image_24).into(imageView);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(param);
        arrayimage.add(imageView);
        listView.addView(imageView);
        imageId++;
    }

    private void createTextView(String str) {
        TextView textViewNm = new TextView(getActivity());
        textViewNm.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString());
        textViewNm.setTextSize(15);
        textViewNm.setTextColor(Color.rgb(0, 0, 0));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewNm.setLayoutParams(param);
        listView.addView(textViewNm);
    }

    // 경로 문자 쓰기
    private void createTextView2(ArrayList<String> route) {
        int count = route.size();
        for (int i = 0; i < count; i++) {
            TextView textViewNm = new TextView(getActivity());
            textViewNm.setText(i + 1 + ". " + route.get(i));
            textViewNm.setTextSize(15);
            textViewNm.setTextColor(Color.rgb(0, 0, 0));
            // 예쁜 스타일 적용
            // textViewNm.setBackgroundResource(R.drawable.textview_background); // 배경 이미지 설정
            textViewNm.setPadding(16, 16, 16, 16); // 텍스트와 테두리 간격 조정
            textViewNm.setGravity(Gravity.CENTER); // 텍스트 가운데 정렬
            textViewNm.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            routeView.addView(textViewNm);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap; // GoogleMap 객체를 초기화
        // 지도 UI 설정 활성화
        googleMap.getUiSettings().setZoomControlsEnabled(true); // 확대/축소 버튼 활성화
        googleMap.getUiSettings().setZoomGesturesEnabled(true); // 확대/축소 제스처 활성화
        googleMap.getUiSettings().setScrollGesturesEnabled(true); // 드래그 제스처 활성화

        double latitudeSum = 0;
        double longitudeSum = 0;
        Geocoder geocoder = new Geocoder(getContext());

        for (int i = 0; i < arrayroute.size(); i++) {
            String name = arrayroute.get(i);
            // 지명으로 경도 위도 뽑기
            String locationName = name + ", 대한민국";
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(locationName, 1);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (addresses != null && !addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                arraylat.add(latitude);
                arraylon.add(longitude);
                //마커넣기
                LatLng mark = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mark).title(name);
                googleMap.addMarker(markerOptions);

                latitudeSum += latitude;
                longitudeSum += longitude;

            }
        }
        LatLng markmean = new LatLng(latitudeSum / arrayroute.size(), longitudeSum / arrayroute.size());
        double maxDistance = calculateDistance(arraylat, arraylon, markmean);
        Log.d("로그", "최대거리 확인하기" + maxDistance);
        int defaultZoom = 13;
        if(maxDistance>13470){
            defaultZoom = 11;
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markmean, defaultZoom));
    }

    private static double calculateDistance(ArrayList arraylat, ArrayList arraylon,  LatLng markMean) {
        double maxDistance = 0;
        final double EARTH_RADIUS = 6371.0;
       for(int i =0; i< arraylat.size(); i++){
           double dLat = Math.toRadians(arraylat.indexOf(i) - markMean.latitude);
           double dLon= Math.toRadians(arraylon.indexOf(i) - markMean.longitude);

           double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(markMean.latitude)) * Math.cos(Math.toRadians(arraylat.indexOf(i))) *
                           Math.sin(dLon / 2) * Math.sin(dLon / 2);
           double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
           double distance = EARTH_RADIUS * c;

           if(maxDistance < distance){
               maxDistance = distance;
           }
       }
        return maxDistance;
    }


    // 비동기식 처리를 위한 함수임 암튼 그럼
    private void fetchDataFromFirebase(){
        if (getArguments() != null) {
            mp = getArguments().getParcelable("myPageValue");
            fragment_title.setText(mp.getTitle());
            fragment_hashtag.setText(mp.getHashTag());
            arrayroute = mp.getRoute(); // 이 부분에서 arrayroute 설정
            checkText(mp);
            createTextView2(arrayroute);
            if(arrayroute!= null){
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment == null) {
                    mapFragment = SupportMapFragment.newInstance();
                    getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                }
                mapFragment.getMapAsync(this);
            }
        } else {
            db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).limit(1).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                                    arrayroute = mp.getRoute(); // arrayroute를 여기서 설정
                                    checkText(mp);
                                    createTextView2(arrayroute);
                                    fragment_title.setText(mp.getTitle());
                                    fragment_hashtag.setText(mp.getHashTag());
                                    // 데이터를 가져온 후에 onMapReady를 호출
                                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                                    if (mapFragment == null) {
                                        mapFragment = SupportMapFragment.newInstance();
                                        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                                    }
                                    mapFragment.getMapAsync(this);
                                }
                            }
                    );
        }
    }
}