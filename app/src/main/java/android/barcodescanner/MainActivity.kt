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
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val scanIntegrator = IntentIntegrator(this)
    private val repository = Repository()
    private lateinit var mainViewModel: MainViewModel

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val adapter = ServicesAdapter()
        binding.servicesList.adapter = adapter

        scanButtonClickListener(binding)
        binding.scaneButton.visibility = VISIBLE

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
            binding.errors.visibility = GONE
            if (mainViewModel.verifyAvailableNetwork(this)) {
                scanIntegrator.initiateScan()
            } else {
                binding.errors.text = "Отсутствует подключение к интернету"
                binding.errors.visibility = VISIBLE
            }
        }
    }

    private fun viewsForScanVisibility(viewVisibility: Int) {
        binding.scaneButton.visibility = viewVisibility
        binding.middlename.visibility = viewVisibility
        binding.surname.visibility = viewVisibility
        binding.name.visibility = viewVisibility
        binding.dateOfVisit.visibility = viewVisibility
        binding.birthdate.visibility = viewVisibility
        binding.serviceScroll.visibility = viewVisibility
        binding.servicesText.visibility = viewVisibility
    }

    private fun loadingViewsVisibility(viewVisibility: Int) {
        binding.loadingBar.visibility = viewVisibility
        binding.loadingText.visibility = viewVisibility
    }

    private fun setText() {
        binding.middlename.text = "Отчество:${mainViewModel.account?.middleName}"
        binding.surname.text = "Фамилия:${mainViewModel.account?.surname}"
        binding.name.text = "Имя:${mainViewModel.account?.name}"
        binding.dateOfVisit.text = "Дата посещения:${mainViewModel.account?.dateOfVisit}"
        binding.birthdate.text = "Дата рождения:${mainViewModel.account?.birthday}"
        binding.servicesSet.text = mainViewModel.account?.services.toString()
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
            try {
                mainViewModel.account = repository.getDataFromBase(barcode)
                withContext(Dispatchers.Main) {
                    Util.endLoading.value = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loadingViewsVisibility(GONE)
                    binding.errors.text = e.message
                    binding.errors.visibility = VISIBLE
                    binding.scaneButton.visibility = VISIBLE
                }
            }
        }
    }
}