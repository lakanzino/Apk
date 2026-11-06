package ir.clinic.secretary.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.clinic.secretary.R
import ir.clinic.secretary.data.Patient
import ir.clinic.secretary.databinding.ItemPatientBinding
import ir.clinic.secretary.util.Fa

/** Keys for the toggleable columns (also persisted in SharedPreferences). */
object Columns {
    const val NAME = "name"
    const val INSURANCE = "insurance"
    const val SUPPLEMENTAL = "supplemental"
    const val REASON = "reason"
    const val TIME = "time"
    const val DATE = "date"
    const val FEE = "fee"
    val ALL = setOf(NAME, INSURANCE, SUPPLEMENTAL, REASON, TIME, DATE, FEE)
}

class PatientAdapter(
    private val onClick: (Patient) -> Unit
) : ListAdapter<Patient, PatientAdapter.VH>(Diff) {

    /** Which columns are currently visible. Changing it redraws the rows. */
    var visibleColumns: Set<String> = Columns.ALL
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class VH(val b: ItemPatientBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val p = getItem(position)
        val b = h.b
        val ctx = b.root.context

        b.tvName.text = p.name
        b.tvInsurance.text = p.insuranceType
        b.tvSupp.text = ctx.getString(if (p.hasSupplemental) R.string.yes else R.string.no)
        b.tvReason.text = p.reason.ifBlank { "—" }
        b.tvTime.text = Fa.digits(p.visitTime)
        b.tvDate.text = Fa.digits(p.visitDate)
        b.tvFee.text = Fa.money(p.fee)

        fun vis(key: String) = if (key in visibleColumns) View.VISIBLE else View.GONE
        b.tvName.visibility = vis(Columns.NAME)
        b.tvInsurance.visibility = vis(Columns.INSURANCE)
        b.tvSupp.visibility = vis(Columns.SUPPLEMENTAL)
        b.tvReason.visibility = vis(Columns.REASON)
        b.tvTime.visibility = vis(Columns.TIME)
        b.tvDate.visibility = vis(Columns.DATE)
        b.tvFee.visibility = vis(Columns.FEE)

        b.root.setOnClickListener { onClick(p) }
    }

    private object Diff : DiffUtil.ItemCallback<Patient>() {
        override fun areItemsTheSame(a: Patient, b: Patient) = a.id == b.id
        override fun areContentsTheSame(a: Patient, b: Patient) = a == b
    }
}
