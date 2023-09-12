package com.example.traveldiary.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.example.traveldiary.R
import com.example.traveldiary.databinding.ActivityUploadCalendarBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.StorageReference
import java.io.File
import kotlin.collections.HashMap

class UploadCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadCalendarBinding
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // intent 불러오기.
        val userToken = intent.getStringExtra("userToken")

        calendarPick()

        binding.next.setOnClickListener {
            nextBtnClickEvent(userToken)
        }

        // toolbar Btn
        binding.home.setOnClickListener {
            val intent = Intent(this, MainViewActivity::class.java)
            intent.putExtra("userToken", userToken)
            startActivity(intent)
            finish()
        }
        binding.myPage.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            intent.putExtra("userToken", userToken)
            startActivity(intent)
            finish()
        }


        // 대표 이미지 가져오는 곳

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode === android.app.Activity.RESULT_OK) {
                Glide
                    .with(getApplicationContext())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(250, 200))
                    .centerCrop()
                    .into(binding.imageButton)

                val cursor = contentResolver.query(
                    it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
                );
                cursor?.moveToFirst().let {
                    filePath = cursor?.getString(0) as String
                }

            }
        }

        binding.imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            )
            requestLauncher.launch(intent)
        }
    }

    private fun calendarPick() {
        //calendar Range
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(resources.getString(R.string.mainActivity_dataPick_title))
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        binding.dataRangeBtn.setOnClickListener {
            dateRangePicker.show(supportFragmentManager, "Material Date Range Picker")
            dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
                val startDate = datePicked.first
                val endDate = datePicked.second
                if (startDate != null && endDate != null) {
                    binding.dataPickerText.text = getString(
                        R.string.uploadCalender_calendar,
                        convertLongToDate(startDate),
                        convertLongToDate(endDate)
                    )
                }
            }
        }
    }

    private fun HashTagCustom(): String {
        var arr = " "
        if (binding.chip1.isChecked) arr = "$arr#혼자 여행 "
        if (binding.chip2.isChecked) arr = "$arr#커플 여행 "
        if (binding.chip3.isChecked) arr = "$arr#친구와 여행 "
        if (binding.chip4.isChecked) arr = "$arr#가족 여행 "
        return arr
    }

    private fun nextBtnClickEvent(userToken: String?) {
        val intent = Intent(this, UploadBoardActivity::class.java)
        intent.putExtra("userToken", userToken)
        val info = HashMap<String, Any>()
        info["date"] = binding.dataPickerText.text.toString()
        try {
            info["mainImg"] = filePath
        } catch (e: Exception) {
            info["mainImg"] = R.drawable.baseline_image_24.toString()
        }
        val storage = LoginActivity.storage
        val storageRef: StorageReference = storage.reference
        val imgRef: StorageReference = storageRef.child("image/${userToken}}.jpg")

        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnFailureListener {
                Log.d("jung", "failure........" + it)
            }.addOnSuccessListener {
                Toast.makeText(this, "데이터 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }

        info["hashTag"] = HashTagCustom()

        intent.putExtra("info", info)
        startActivity(intent)
    }

    /**
     * Code that changes the format of the date view to match the specified format data when selecting a date in Select Date (Duration).
     *
     * @param time Date data to be changed to match the format.
     * @return String format : yyyy년=mm월-dd일
     * @author wjdsheep15
     * @since 1.0
     */
    private fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat(
            "yyyy.MM.dd", Locale.getDefault()
        )
        return format.format(date)
    }
}