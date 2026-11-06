package ir.clinic.secretary.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ir.clinic.secretary.MainViewModel
import ir.clinic.secretary.R
import ir.clinic.secretary.data.Patient
import ir.clinic.secretary.databinding.FragmentRegisterBinding
import ir.clinic.secretary.util.Fa
import ir.clinic.secretary.util.PersianDate
import java.util.Calendar
import java.util.Locale

/** Patient registration form. */
class RegisterFragment : Fragment() {

    private var _b: FragmentRegisterBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()

    private var hour = 0
    private var minute = 0
    private var defaultFee = 0L

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRegisterBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Insurance dropdown (Spinner) filled from res/values/strings.xml
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.insurance_types, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spInsurance.adapter = adapter

        resetTimeToNow()

        // Tapping the time field opens a 24h time picker
        b.etTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                hour = h; minute = m; showTime()
            }, hour, minute, true).show()
        }

        // Default fee comes from Settings; only pre-fill when the field is empty
        vm.settings.observe(viewLifecycleOwner) { s ->
            defaultFee = s.defaultFee
            if (b.etFee.text.isNullOrBlank()) b.etFee.setText(defaultFee.toString())
        }

        b.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val name = b.etName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            b.tilName.error = getString(R.string.err_name_required)
            b.etName.requestFocus()
            return
        }
        b.tilName.error = null

        val patient = Patient(
            name = name,
            insuranceType = b.spInsurance.selectedItem.toString(),
            hasSupplemental = b.cbSupplemental.isChecked,
            reason = b.etReason.text?.toString()?.trim().orEmpty(),
            visitTime = String.format(Locale.US, "%02d:%02d", hour, minute),
            visitDate = PersianDate.today().format(),
            fee = Fa.parseNumber(b.etFee.text?.toString()) ?: defaultFee
        )
        vm.savePatient(patient)
        Toast.makeText(requireContext(), R.string.msg_saved, Toast.LENGTH_SHORT).show()
        clearForm()
    }

    /** Resets everything for the next patient. */
    private fun clearForm() {
        b.etName.text?.clear()
        b.etReason.text?.clear()
        b.cbSupplemental.isChecked = false
        b.spInsurance.setSelection(0)
        b.etFee.setText(defaultFee.toString())
        resetTimeToNow()
        b.etName.requestFocus()
    }

    private fun resetTimeToNow() {
        val now = Calendar.getInstance()
        hour = now.get(Calendar.HOUR_OF_DAY)
        minute = now.get(Calendar.MINUTE)
        showTime()
    }

    private fun showTime() {
        b.etTime.setText(Fa.digits(String.format(Locale.US, "%02d:%02d", hour, minute)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
