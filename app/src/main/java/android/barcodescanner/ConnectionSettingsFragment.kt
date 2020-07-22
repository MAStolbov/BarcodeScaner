package android.barcodescanner

import android.barcodescanner.databinding.FragmentConnectionSettingsBinding
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Util
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController

class ConnectionSettingsFragment : Fragment() {

    private lateinit var connectionPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentConnectionSettingsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_connection_settings,
            container,
            false
        )

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_connectionSettingsFragment_to_scanFragment)
        }

        binding.serverAddressPlainText.setText(
            connectionPreferences.getString(SERVER_ADDRESS_KEY, "")
        )

        binding.loginPlainText.setText(connectionPreferences.getString(LOGIN_KEY,""))

        binding.passwordPlainText.setText(connectionPreferences.getString(PASSWORD_KEY,""))

        binding.setAddress.setOnClickListener {
            connectionPreferences.edit().apply {
                putString(SERVER_ADDRESS_KEY,binding.serverAddressPlainText.text.toString())
                putString(LOGIN_KEY,binding.loginPlainText.text.toString())
                putString(PASSWORD_KEY,binding.passwordPlainText.text.toString())
                apply()
            }
            findNavController().navigate(R.id.action_connectionSettingsFragment_to_scanFragment)
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        connectionPreferences =
            context.getSharedPreferences("connection settings", Context.MODE_PRIVATE)

    }
}