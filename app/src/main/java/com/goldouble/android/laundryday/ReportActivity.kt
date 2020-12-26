package com.goldouble.android.laundryday

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.goldouble.android.laundryday.databinding.ActivityReportBinding
import com.google.firebase.firestore.GeoPoint
import java.util.*

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding

    private var correctMap = hashMapOf(
        "name" to false,
        "phone" to false,
        "address" to false,
        "check" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "세탁소 제보"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.editTextReportName.addTextChangedListener {
            correctMap["name"] = !it.isNullOrEmpty()
            binding.buttonReportSubmit.isEnabled = !correctMap.containsValue(false)
        }

        binding.editTextReportPhone.addTextChangedListener {
            correctMap["phone"] = !it.isNullOrEmpty()
            binding.buttonReportSubmit.isEnabled = !correctMap.containsValue(false)
        }

        binding.editTextReportAddress.addTextChangedListener {
            correctMap["address"] = !it.isNullOrEmpty()
            binding.buttonReportSubmit.isEnabled = !correctMap.containsValue(false)
        }

        binding.checkBoxReport.setOnCheckedChangeListener { _, checked ->
            correctMap["check"] = checked
            binding.buttonReportSubmit.isEnabled = !correctMap.containsValue(false)
        }

        binding.buttonReportSubmit.setOnClickListener {
            binding.loadingLayout.root.visibility = View.VISIBLE
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            try {
                val geocoder = Geocoder(this)
                val addressList = geocoder.getFromLocationName(binding.editTextReportAddress.text.toString(), 1)

                addressList?.let {
                    if(it.isEmpty()) {
                        Toast.makeText(this, "해당되는 주소 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.loadingLayout.root.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    } else {
                        val point = addressList.first()
                        kFirestore.collection(Table.LAUNDRY.id).add (
                            hashMapOf(
                                "name" to binding.editTextReportName.text.toString(),
                                "type" to if(binding.radioGroupReportType.checkedRadioButtonId == R.id.radioReportSelf) "0001" else "0002",
                                "address" to point.getAddressLine(0),
                                "address_array" to  point.getAddressLine(0).split(" "),
                                "latLng" to GeoPoint(point.latitude, point.longitude),
                                "writer" to kAuth.currentUser!!.email!!,
                                "time" to Date(),
                                "registered" to false,
                                "number" to binding.editTextReportPhone.text.toString()
                            )
                        ).addOnCompleteListener {
                            binding.loadingLayout.root.visibility = View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.addOnCompleteListener { task ->
                            Log.d("REPORT", task.result.id)
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}