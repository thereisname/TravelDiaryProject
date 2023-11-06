package com.example.traveldiary.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.traveldiary.R;
import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentClient extends Fragment implements OnMapReadyCallback {
    private MapView googlemap = null;
    private GoogleMap map; // 추가: GoogleMap 객체를 저장하는 변수
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private ImageButton bookmark;
    private static MyPageValue mp;
    private LinearLayout listView, routeView;
    TextView routeTitle, fragment_title, fragment_hashtag, uploadDate;
    FirebaseFirestore db;
    ArrayList<String> arrayroute = new ArrayList();
    ArrayList<Double> arraylat = new ArrayList<>();
    ArrayList<Double> arraylon = new ArrayList<>();

    public FragmentClient() {
    }

    public static FragmentClient newInstance(MyPageValue myPageValue) {
        FragmentClient fragment = new FragmentClient();
        Bundle args = new Bundle();
        args.putParcelable("myPageValue", myPageValue);
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

        bookmark = view.findViewById(R.id.bookMarkBtn);
        fragment_title = view.findViewById(R.id.fragment_title);
        fragment_hashtag = view.findViewById(R.id.fragment_hashtag);
        uploadDate = view.findViewById(R.id.uploadDate);

        //리스트뷰 content와 image 담아두는 곳
        listView = view.findViewById(R.id.listView);
        routeView = view.findViewById(R.id.routeView);
        routeTitle = view.findViewById(R.id.routeTitle);
        googlemap = view.findViewById(R.id.map);
        googlemap.onCreate(savedInstanceState); // 지도의 onCreate 호출

        listView.setVisibility(View.VISIBLE);
        uploadDate.setVisibility(View.VISIBLE);
        routeView.setVisibility(View.GONE);
        routeTitle.setVisibility(View.GONE);
        googlemap.setVisibility(View.GONE);

        bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        TextView fragment_hashtag = view.findViewById(R.id.fragment_hashtag);
        listView = view.findViewById(R.id.listView);

        if (getArguments() != null) {
            mp = getArguments().getParcelable("myPageValue");
            fragment_title.setText(mp.getTitle());
            fragment_hashtag.setText(mp.getHashTag());
            uploadDate.setText(getString(R.string.uploadBoard_uploadDate, mp.getUploadDate()));
            arrayroute = mp.getRoute();
            ContentDownloadAdapter contentDownloadAdapter = new ContentDownloadAdapter(getActivity(), listView, mp);
            contentDownloadAdapter.checkText();
            contentDownloadAdapter.ImageDown(mp.getBoardID());
            createTextView2(arrayroute);
            if (arrayroute != null) {
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment == null) {
                    mapFragment = SupportMapFragment.newInstance();
                    getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                }
                mapFragment.getMapAsync(this);
            }
            if (mp.getBookmark() != null) {
                if (mp.getBookmark().contains(FirebaseAuth.getInstance().getUid())) {
                    bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                    isAttBookmark = 1;
                }
            }
        } else {
            db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).limit(1).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                                    arrayroute = mp.getRoute();
                                    ContentDownloadAdapter contentDownloadAdapter = new ContentDownloadAdapter(getActivity(), listView, mp);
                                    createTextView2(arrayroute);
                                    contentDownloadAdapter.checkText();
                                    contentDownloadAdapter.ImageDown(mp.getBoardID());
                                    fragment_title.setText(mp.getTitle());
                                    fragment_hashtag.setText(mp.getHashTag());
                                    uploadDate.setText(getString(R.string.uploadBoard_uploadDate, mp.getUploadDate()));
                                    // 데이터를 가져온 후에 onMapReady를 호출
                                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                                    if (mapFragment == null) {
                                        mapFragment = SupportMapFragment.newInstance();
                                        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                                    }
                                    mapFragment.getMapAsync(this);
                                    if (mp.getBookmark().contains(FirebaseAuth.getInstance().getUid())) {
                                        bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                                        isAttBookmark = 1;
                                    }
                                }
                            }
                    );
        }

        bookmark.setOnClickListener(v -> {
            List<String> bookmarkArray = mp.getBookmark();
            if (isAttBookmark == 0) {
                bookmarkArray.add(FirebaseAuth.getInstance().getUid());
                db.collection("data").document(mp.getBoardID()).update("bookmark", bookmarkArray).addOnSuccessListener(command -> {
                    isAttBookmark = 1;
                    bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                });
            } else {
                bookmarkArray.remove(FirebaseAuth.getInstance().getUid());
                db.collection("data").document(mp.getBoardID()).update("bookmark", bookmarkArray).addOnSuccessListener(command -> {
                    isAttBookmark = 0;
                    bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
                });
            }
        });

        ImageButton nextBtn = view.findViewById(R.id.imgBtn_next);
        ImageButton beforeBtn = view.findViewById(R.id.imgBtn_before);

        nextBtn.bringToFront();
        beforeBtn.bringToFront();
        nextBtn.setOnClickListener(v -> {
            listView.setVisibility(View.GONE);
            uploadDate.setVisibility(View.GONE);
            routeView.setVisibility(View.VISIBLE);
            routeTitle.setVisibility(View.VISIBLE);
            googlemap.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
            beforeBtn.setVisibility(View.VISIBLE);
        });

        beforeBtn.setOnClickListener(v -> {
            listView.setVisibility(View.VISIBLE);
            uploadDate.setVisibility(View.VISIBLE);
            routeView.setVisibility(View.GONE);
            routeTitle.setVisibility(View.GONE);
            googlemap.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
            beforeBtn.setVisibility(View.GONE);
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

    // 경로 문자 쓰기
    private void createTextView2(ArrayList<String> route) {
        int count = route.size();
        for (int i = 0; i < count; i++) {
            TextView textViewNm = new TextView(getActivity());
            textViewNm.setText(i + 1 + ". " + route.get(i));
            textViewNm.setTextSize(15);
            textViewNm.setTextColor(Color.rgb(0, 0, 0));
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

        PolylineOptions polylineOptions = new PolylineOptions(); // PolylineOptions 초기화

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

                markerOptions.position(mark).title((i + 1) + name);
                googleMap.addMarker(markerOptions);
                polylineOptions.add(mark);

                latitudeSum += latitude;
                longitudeSum += longitude;
            }
        }
        LatLng markmean = new LatLng(latitudeSum / arrayroute.size(), longitudeSum / arrayroute.size());
        double maxDistance = calculateDistance(arraylat, arraylon, markmean);
        int defaultZoom = 13;
        if (maxDistance > 13470) {
            defaultZoom = 11;
        }

        // Polyline을 그립니다.
        googleMap.addPolyline(polylineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markmean, defaultZoom));
    }

    private static double calculateDistance(ArrayList arraylat, ArrayList arraylon, LatLng markMean) {
        double maxDistance = 0;
        final double EARTH_RADIUS = 6371.0;
        for (int i = 0; i < arraylat.size(); i++) {
            double dLat = Math.toRadians(arraylat.indexOf(i) - markMean.latitude);
            double dLon = Math.toRadians(arraylon.indexOf(i) - markMean.longitude);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(markMean.latitude)) * Math.cos(Math.toRadians(arraylat.indexOf(i))) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = EARTH_RADIUS * c;

            if (maxDistance < distance) {
                maxDistance = distance;
            }
        }
        return maxDistance;
    }
}