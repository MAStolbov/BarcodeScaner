package android.barcodescanner

import android.app.Activity
import android.barcodescanner.databinding.ActivityMainBinding
import android.content.Intent
import android.dataStorage.DataStorage
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Util
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    private val dataStorage = DataStorage.instance
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        scanButtonClickListener(binding)

        Util.endLoading.observe(this, Observer {
            binding.resultText.text = dataStorage.dataFromBase
        })
    }

    private fun scanButtonClickListener(binding: ActivityMainBinding) {
        binding.scaneButton.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
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

    private fun starDataLoading(barcode:String){
        dataStorage.loadData(barcode)
    }
}