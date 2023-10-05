package com.example.traveldiary.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.traveldiary.R
import com.example.traveldiary.databinding.ActivityUploadCalendarBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UploadCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadCalendarBinding
    lateinit var listView: LinearLayout
    lateinit var filePath: Uri
    var isMainImage: Boolean = false
    var isDataPickerText: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.listView)

        calendarPick()  // 여행일 선택창 output.

        binding.next.setOnClickListener {
            if(!isDataPickerText) Toast.makeText(applicationContext, "여행 기간을 선택해주세요.", Toast.LENGTH_SHORT).show()
            else if(!isMainImage) Toast.makeText(applicationContext, "대표 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            else nextBtnClickEvent()
        }

        // toolbar Btn
        binding.home.setOnClickListener {
            startActivity(Intent(this, MainViewActivity::class.java))
            finish()
        }
        binding.myPage.setOnClickListener {
            startActivity(Intent(this, MypageActivity::class.java))
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
                )
                cursor?.moveToFirst().let {
                    filePath = Uri.fromFile(File(cursor?.getString(0) as String))
                    isMainImage = true
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
                    isDataPickerText = true
                }
            }
        }

        binding.addLoad.setOnClickListener {
            addEditor();
        }
    }

    var count = 2
    private fun addEditor() {
        // xml 300dp 숫자 만드는 방법
        var dpValue = 300
        var density = resources.displayMetrics.density
        var pixelValue = dpValue * density

        var editText: EditText = EditText(applicationContext)
        editText.setHintTextColor(getColor(R.color.icon))
        editText.setTextColor(getColor(R.color.text_gray))
        editText.id = count
        editText.setHint("경로" + count)
        editText.textSize = 12f
        editText.setLinkTextColor(getColor(R.color.icon))
        editText.width = pixelValue.toInt()
        editText.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.icon))
        editText.setTextAppearance(R.style.uploadCalender_rote)

        val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        editText.layoutParams = param
        listView.addView(editText)
        count++
    }

    private fun HashTagCustom(): String {
        var arr = " "
        if (binding.chip1.isChecked) arr = "$arr#혼자 여행 "
        if (binding.chip2.isChecked) arr = "$arr#커플 여행 "
        if (binding.chip3.isChecked) arr = "$arr#친구와 여행 "
        if (binding.chip4.isChecked) arr = "$arr#가족 여행 "

        if (binding.chip10.isChecked) arr = "$arr#계획적인 "
        if (binding.chip11.isChecked) arr = "$arr#자유로운 "
        if (binding.chip12.isChecked) arr = "$arr#휴가 "
        if (binding.chip13.isChecked) arr = "$arr#추억 "
        if (binding.chip14.isChecked) arr = "$arr#힐링 "
        if (binding.chip15.isChecked) arr = "$arr#엑티비티 "
        if (binding.chip16.isChecked) arr = "$arr#맛집투어 "
        if (binding.chip17.isChecked) arr = "$arr#낭만 "
        if (binding.chip18.isChecked) arr = "$arr#감성 "
        return arr
    }

    private fun nextBtnClickEvent() {
        val intent = Intent(this, UploadBoardActivity::class.java)
        val info = HashMap<String, Any>()
        info["date"] = binding.dataPickerText.text.toString()
        info["hashTag"] = HashTagCustom()
        info["mainImage"] = filePath
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
