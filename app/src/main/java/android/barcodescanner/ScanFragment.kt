package android.barcodescanner

import android.app.Activity
import android.app.AlertDialog
import android.barcodescanner.databinding.FragmentScanBinding
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.dataStorage.Account
import android.os.Bundle
import android.repository.Repository
import android.util.Util
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SERVER_ADDRESS_KEY = "server address"

class ScanFragment : Fragment() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val repository = Repository()
    private lateinit var alertDialog: AlertDialog
    private lateinit var scanViewModel: ScanViewModel
    private lateinit var scanIntegrator: IntentIntegrator
    private lateinit var connectionPreferences: SharedPreferences
    private lateinit var binding: FragmentScanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        scanViewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
        scanIntegrator = IntentIntegrator.forSupportFragment(this)

        val adapter = ServicesAdapter()
        binding.servicesList.adapter = adapter

        scanButtonClickListener(binding)
        binding.scaneButton.visibility = View.VISIBLE

        binding.servicesList

        scanIntegrator.setBeepEnabled(false)
        scanIntegrator.setOrientationLocked(false)

        scanViewModel.wrongPriceFormatFounded.observe(viewLifecycleOwner, Observer {
            alertDialog.show()
        })

        scanViewModel.endLoading.observe(viewLifecycleOwner, Observer {
            setLoadingViewsVisibility(View.GONE)
            setViewsForScanVisibility(View.VISIBLE)
            scanViewModel.setServicesList()
            setText()
        })

        scanViewModel.servicesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        alertDialog = setAlertDialog()
        connectionPreferences =
            context.getSharedPreferences("connection settings", Context.MODE_PRIVATE)

    }

    private fun setAlertDialog(): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle("Итоговая цена")
        alertDialogBuilder.setMessage(
            "Не все цены услуг вобшли в итоговую стоимость в виду неверного вида")
        alertDialogBuilder.setPositiveButton("Ок") { dialog, which ->

        }

        return alertDialogBuilder.create()
    }

    private fun scanButtonClickListener(binding: FragmentScanBinding) {
        binding.scaneButton.setOnClickListener {
            scanViewModel.account = Account()
            binding.errors.visibility = View.GONE
            setViewsForScanVisibility(View.GONE)
            if (scanViewModel.verifyAvailableNetwork(this.context)) {
                scanIntegrator.initiateScan()
            } else {
                binding.errors.text = "Отсутствует подключение к интернету"
                binding.errors.visibility = View.VISIBLE
            }
        }
    }

    private fun setViewsForScanVisibility(viewVisibility: Int) {
        binding.middlename.visibility = viewVisibility
        binding.surname.visibility = viewVisibility
        binding.name.visibility = viewVisibility
        binding.srnmHint.visibility = viewVisibility
        binding.nameHint.visibility = viewVisibility
        binding.mdlnHint.visibility = viewVisibility
        binding.visHint.visibility = viewVisibility
        binding.brtdHint.visibility = viewVisibility
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
        setLoadingViewsVisibility(View.GONE)
        binding.errors.text = errorText
        binding.errors.visibility = View.VISIBLE
        binding.scaneButton.visibility = View.VISIBLE
    }

    private fun setText() {
        binding.middlename.text = "${scanViewModel.account?.middleName}"
        binding.surname.text = "${scanViewModel.account?.surname}"
        binding.name.text = "${scanViewModel.account?.name}"
        binding.dateOfVisit.text = "${scanViewModel.account?.dateOfVisit}"
        binding.birthdate.text = "${scanViewModel.account?.birthday}"
        binding.totalPrice.text = "Итог: ${scanViewModel.getTotalPrice()} руб."
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(context, "Ошибка сканирования", Toast.LENGTH_LONG).show()
                } else {
                    if (result.formatName == IntentIntegrator.CODE_128) {
                        if (scanViewModel.openSettings(result.contents)) {
                            findNavController().navigate(R.id.action_scanFragment_to_connectionSettingsFragment)
                        }
                        if (scanViewModel.checkBarcodeCRC(result.contents)) {
                            startDataLoading(result.contents)
                        } else {
                            showErrorInfo("Штрих-код не подлежит проверке (ошибка контрольной суммы):${result.contents}")
                        }
                    } else {
                        showErrorInfo("Неправильный тип штрих-кода:${result.formatName} ${result.contents}")
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun startDataLoading(barcode: String) {
        setViewsForScanVisibility(View.GONE)
        setLoadingViewsVisibility(View.VISIBLE)
        val serverAddress = connectionPreferences.getString(SERVER_ADDRESS_KEY, "") ?: ""
        ioScope.launch {
            scanViewModel.account = repository.getDataFromBase(serverAddress, barcode)
//            scanViewModel.account = Util.setTextAccount()
//            Util.dataReceivedSuccessful = true
            withContext(Dispatchers.Main) {
                if (Util.dataReceivedSuccessful) {
                    scanViewModel.endLoading.value = true
                } else {
                    showErrorInfo(Util.errorText)
                }
            }
        }
    }

}