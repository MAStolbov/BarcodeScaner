package android.barcodescanner

import android.app.Activity
import android.barcodescanner.databinding.ActivityMainBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.repository.Repository
import android.util.Util
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val scanIntegrator = IntentIntegrator(this)
    private val repository = Repository()
    private lateinit var mainViewModel:MainViewModel

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        scanButtonClickListener(binding)
        viewsForScanVisibility(VISIBLE)
        scanIntegrator.setBeepEnabled(false)
        scanIntegrator.setOrientationLocked(false)

        Util.endLoading.observe(this, Observer {
            loadingViewsVisibility(GONE)
            viewsForScanVisibility(VISIBLE)
            setText()
        })
    }

    private fun scanButtonClickListener(binding: ActivityMainBinding) {
        binding.scaneButton.setOnClickListener {
            scanIntegrator.initiateScan()
        }
    }

    private fun viewsForScanVisibility(viewVisibility: Int) {
        binding.scaneButton.visibility = viewVisibility
        binding.user.visibility = viewVisibility
        binding.code.visibility = viewVisibility
        binding.name.visibility = viewVisibility
    }

    private fun loadingViewsVisibility(viewVisibility: Int) {
        binding.loadingBar.visibility = viewVisibility
        binding.loadingText.visibility = viewVisibility
    }

    private fun setText(){
        binding.user.text = mainViewModel.dataFromBase?.user
        binding.code.text = mainViewModel.dataFromBase?.code.toString()
        binding.name.text = mainViewModel.dataFromBase?.name
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
                        binding.errors.text = "Wrong barcode type:${result.formatName}"
                        binding.errors.visibility = VISIBLE
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
            delay(1000)
            mainViewModel.dataFromBase = repository.getDataFromBase(barcode)
            withContext(Dispatchers.Main) { Util.endLoading.value = true }
        }
    }
}