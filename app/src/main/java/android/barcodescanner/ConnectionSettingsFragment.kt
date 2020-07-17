package android.barcodescanner

import android.barcodescanner.databinding.FragmentConnectionSettingsBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

class ConnectionSettingsFragment : Fragment() {
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
        return binding.root
    }
}