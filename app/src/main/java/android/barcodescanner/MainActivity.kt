package android.barcodescanner

import android.app.Activity
import android.barcodescanner.databinding.ActivityMainBinding
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.dataStorage.Account
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

const val SERVER_ADDRESS = "address"

class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val scanIntegrator = IntentIntegrator(this)
    private val repository = Repository()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var serverConnectionPref: SharedPreferences

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        serverConnectionPref = getSharedPreferences("settings", Context.MODE_PRIVATE)

        val adapter = ServicesAdapter()
        binding.servicesList.adapter = adapter

        scanButtonClickListener(binding)
        binding.scaneButton.visibility = VISIBLE

        scanIntegrator.setBeepEnabled(false)
        scanIntegrator.setOrientationLocked(false)

        mainViewModel.endLoading.observe(this, Observer {
            setLoadingViewsVisibility(GONE)
            setViewsForScanVisibility(VISIBLE)
            mainViewModel.setServicesList()
            setText()
        })

        mainViewModel.servicesList.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onPause() {
        super.onPause()

        serverConnectionPref.edit().apply {
            putString(SERVER_ADDRESS,Util.serverAddress)
            apply()
        }
    }

    override fun onResume() {
        super.onResume()

        if (serverConnectionPref.contains(SERVER_ADDRESS)){
            Util.serverAddress = serverConnectionPref.getString(SERVER_ADDRESS,"") ?: ""
        }
    }

    private fun scanButtonClickListener(binding: ActivityMainBinding) {
        binding.scaneButton.setOnClickListener {
            mainViewModel.account = Account()
            binding.errors.visibility = GONE
            setViewsForScanVisibility(GONE)
            if (mainViewModel.verifyAvailableNetwork(this)) {
                scanIntegrator.initiateScan()
            } else {
                binding.errors.text = "Отсутствует подключение к интернету"
                binding.errors.visibility = VISIBLE
            }
        }
    }

    private fun setViewsForScanVisibility(viewVisibility: Int) {
        binding.middlename.visibility = viewVisibility
        binding.surname.visibility = viewVisibility
        binding.name.visibility = viewVisibility
        binding.dateOfVisit.visibility = viewVisibility
        binding.birthdate.visibility = viewVisibility
        binding.servicesText.visibility = viewVisibility
        binding.servicesList.visibility = viewVisibility
        binding.totalPrice.visibility = viewVisibility
    }

    private fun setLoadingViewsVisibility(viewVisibility: Int) {
        binding.loadingBar.visibility = viewVisibility
        binding.loadingText.visibility = viewVisibility
    }

    private fun showErrorInfo(errorText: String) {
        setLoadingViewsVisibility(GONE)
        binding.errors.text = errorText
        binding.errors.visibility = VISIBLE
        binding.scaneButton.visibility = VISIBLE
    }

    private fun setText() {
        binding.middlename.text = "Отчество:${mainViewModel.account?.middleName}"
        binding.surname.text = "Фамилия:${mainViewModel.account?.surname}"
        binding.name.text = "Имя:${mainViewModel.account?.name}"
        binding.dateOfVisit.text = "Дата посещения:${mainViewModel.account?.dateOfVisit}"
        binding.birthdate.text = "Дата рождения:${mainViewModel.account?.birthday}"
        binding.totalPrice.text = "Итог: ${mainViewModel.getTotalPrice()}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Ошибка сканирования", Toast.LENGTH_LONG).show()
                } else {
                    if (result.formatName == IntentIntegrator.CODE_128) {
                        if (mainViewModel.checkBarcodeCRC(result.contents)) {
                            starDataLoading(result.contents)
                        } else {
                            binding.errors.text =
                                "Штрих-код не подлежит проверке (ошибка контрольной суммы):${result.contents}"
                            binding.errors.visibility = VISIBLE
                        }
                    } else {
                        binding.errors.text =
                            "Неправильный тип штрих-кода:${result.formatName} ${result.contents}"
                        binding.errors.visibility = VISIBLE
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun starDataLoading(barcode: String) {
        setViewsForScanVisibility(GONE)
        setLoadingViewsVisibility(VISIBLE)
        ioScope.launch {
            //имитация скачивания данных из внешнего источника
            mainViewModel.account = repository.getDataFromBase(barcode)
            withContext(Dispatchers.Main) {
                if (Util.dataReceivedSuccessful) {
                    mainViewModel.endLoading.value = true
                } else {
                    showErrorInfo(Util.errorText)
                }
            }
        }
    }
}
