package android.barcodescanner

import android.app.Activity
import android.barcodescanner.databinding.ActivityMainBinding
import android.content.Intent
import android.dataStorage.DataFromBase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.repository.Repository
import android.util.Util
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.oned.Code128Reader
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.*

const val KEY_DATA_FROM_BASE = "data_from_base_key"

class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
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
        viewsForScanVisibility(VISIBLE)
        scanIntegrator.setBeepEnabled(false)
        scanIntegrator.setOrientationLocked(false)

        Util.endLoading.observe(this, Observer {
            loadingViewsVisibility(GONE)
            viewsForScanVisibility(VISIBLE)
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

    private fun viewsForScanVisibility(viewVisibility: Int) {
        binding.scaneButton.visibility = viewVisibility
        binding.resultText.visibility = viewVisibility
    }

    private fun loadingViewsVisibility(viewVisibility: Int) {
        binding.loadingBar.visibility = viewVisibility
        binding.loadingText.visibility = viewVisibility
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Wrong barcode", Toast.LENGTH_LONG).show()
                } else {
                    if (result.formatName == IntentIntegrator.CODE_128) {
                        starDataLoading(result.contents)
                    } else {
                        binding.resultText.text = "Wrong barcode type:${result.formatName}"
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun starDataLoading(barcode: String) {
        viewsForScanVisibility(GONE)
        loadingViewsVisibility(VISIBLE)
        ioScope.launch {
            //имитация скачивания данных из внешнего источника
            delay(2000)
            dataFromBase = repository.getDataFromBase(barcode)
            withContext(Dispatchers.Main) { Util.endLoading.value = true }
        }
    }
}