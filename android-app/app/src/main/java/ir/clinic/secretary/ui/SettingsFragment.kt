package ir.clinic.secretary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ir.clinic.secretary.MainViewModel
import ir.clinic.secretary.R
import ir.clinic.secretary.databinding.FragmentSettingsBinding
import ir.clinic.secretary.util.Fa

/** Doctor name, specialty and default visit fee. Stored in the `settings` table. */
class SettingsFragment : Fragment() {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()
    private var loaded = false

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentSettingsBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Fill the fields once with the stored values (don't overwrite what the user is typing)
        vm.settings.observe(viewLifecycleOwner) { s ->
            if (!loaded) {
                loaded = true
                b.etDoctorName.setText(s.doctorName)
                b.etSpecialty.setText(s.specialty)
                b.etDefaultFee.setText(s.defaultFee.toString())
            }
        }

        b.btnSaveSettings.setOnClickListener {
            val name = b.etDoctorName.text?.toString()?.trim().orEmpty()
            val spec = b.etSpecialty.text?.toString()?.trim().orEmpty()
            val fee = Fa.parseNumber(b.etDefaultFee.text?.toString()) ?: 0L
            vm.saveSettings(name, spec, fee)
            Toast.makeText(requireContext(), R.string.msg_settings_saved, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
