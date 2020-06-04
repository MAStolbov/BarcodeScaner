package android.barcodescanner

import android.app.Activity
import android.barcodescanner.databinding.ActivityMainBinding
import android.content.Intent
import android.dataStorage.DataFromBase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.repository.Repository
import android.util.Util
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator

const val KEY_DATA_FROM_BASE = "data_from_base_key"

class MainActivity : AppCompatActivity() {

    private var dataFromBase = DataFromBase("")
    private val scanIntegrator = IntentIntegrator(this)
    private val repository = Repository()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (savedInstanceState != null) {
            dataFromBase.resultData = savedInstanceState.getString(KEY_DATA_FROM_BASE, "")
            binding.resultText.text = dataFromBase.resultData
        }
        scanButtonClickListener(binding)
        scanIntegrator.setBeepEnabled(false)
        scanIntegrator.setOrientationLocked(false)

        Util.endLoading.observe(this, Observer {
            binding.resultText.text = dataFromBase.resultData
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_DATA_FROM_BASE, dataFromBase.resultData)
    }

    private fun scanButtonClickListener(binding: ActivityMainBinding) {
        binding.scaneButton.setOnClickListener {
            scanIntegrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Wrong barcode", Toast.LENGTH_LONG).show()
                } else {
                    starDataLoading(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun starDataLoading(barcode: String) {
        dataFromBase = repository.getDataFromBase(barcode)
        Util.endLoading.value = true
    }
}