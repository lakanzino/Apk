package ir.clinic.secretary.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import ir.clinic.secretary.MainViewModel
import ir.clinic.secretary.R
import ir.clinic.secretary.data.Patient
import ir.clinic.secretary.databinding.FragmentPatientsBinding
import ir.clinic.secretary.util.Fa

/** Patient table with chips to show/hide each column. */
class PatientsFragment : Fragment() {

    private var _b: FragmentPatientsBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()
    private lateinit var adapter: PatientAdapter

    private val prefs by lazy { requireContext().getSharedPreferences("ui", Context.MODE_PRIVATE) }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentPatientsBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PatientAdapter { showDetails(it) }
        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        // Chip -> column key
        val chips: Map<Chip, String> = mapOf(
            b.chipName to Columns.NAME,
            b.chipInsurance to Columns.INSURANCE,
            b.chipSupp to Columns.SUPPLEMENTAL,
            b.chipReason to Columns.REASON,
            b.chipTime to Columns.TIME,
            b.chipDate to Columns.DATE,
            b.chipFee to Columns.FEE
        )

        // Restore saved choice (defaults to everything visible)
        val saved = prefs.getStringSet("visible_columns", Columns.ALL) ?: Columns.ALL
        chips.forEach { (chip, key) ->
            chip.isChecked = key in saved
            chip.setOnCheckedChangeListener { _, _ ->
                val now = chips.filterKeys { it.isChecked }.values.toSet()
                prefs.edit().putStringSet("visible_columns", now).apply()
                applyColumns(now)
            }
        }
        applyColumns(chips.filterKeys { it.isChecked }.values.toSet())

        vm.patients.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            val total = list.sumOf { it.fee }
            b.tvSummary.text = getString(
                R.string.summary, Fa.digits(list.size.toString()), Fa.money(total)
            )
        }
    }

    /** Shows/hides the header cells and the row cells together. */
    private fun applyColumns(v: Set<String>) {
        fun vis(k: String) = if (k in v) View.VISIBLE else View.GONE
        val h = b.header
        h.hName.visibility = vis(Columns.NAME)
        h.hInsurance.visibility = vis(Columns.INSURANCE)
        h.hSupp.visibility = vis(Columns.SUPPLEMENTAL)
        h.hReason.visibility = vis(Columns.REASON)
        h.hTime.visibility = vis(Columns.TIME)
        h.hDate.visibility = vis(Columns.DATE)
        h.hFee.visibility = vis(Columns.FEE)
        adapter.visibleColumns = v
    }

    /** Tap a row: show full details, with an option to delete the record. */
    private fun showDetails(p: Patient) {
        val yes = getString(if (p.hasSupplemental) R.string.yes else R.string.no)
        val msg = """
            ${getString(R.string.col_insurance)}: ${p.insuranceType}
            ${getString(R.string.col_supp)}: $yes
            ${getString(R.string.col_reason)}: ${p.reason.ifBlank { "—" }}
            ${getString(R.string.col_time)}: ${Fa.digits(p.visitTime)}
            ${getString(R.string.col_date)}: ${Fa.digits(p.visitDate)}
            ${getString(R.string.col_fee)}: ${Fa.money(p.fee)}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle(p.name)
            .setMessage(msg)
            .setPositiveButton(R.string.close, null)
            .setNegativeButton(R.string.delete) { _, _ -> confirmDelete(p) }
            .show()
    }

    private fun confirmDelete(p: Patient) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.delete) { _, _ -> vm.deletePatient(p) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
