package com.example.traveldiary.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.example.traveldiary.R
import com.example.traveldiary.databinding.ActivityUploadCalendarBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class UploadCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // intent 불러오기.
        val userToken = intent.getStringExtra("userToken")

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
                    binding.dataPickerText.text =
                        convertLongToDate(startDate) + " ~ " + convertLongToDate(endDate)
                }
            }
        }

        binding.next.setOnClickListener {
            val intent = Intent(this,UploadBoardActivity::class.java)
            intent.putExtra("userToken", userToken)
            startActivity(intent)
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