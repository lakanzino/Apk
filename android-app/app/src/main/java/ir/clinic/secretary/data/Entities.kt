package ir.clinic.secretary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Table `patients`.
 * Room stores Boolean as INTEGER 0/1 and Long as INTEGER, matching the requested schema.
 */
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val insuranceType: String,
    val hasSupplemental: Boolean,
    val reason: String,
    val visitTime: String,   // "14:30"
    val visitDate: String,   // Shamsi "1403/05/12"
    val fee: Long            // Toman/Rial, whole number
)

/** Table `settings`: simple key/value store (doctor name, specialty, default fee). */
@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey val key: String,
    val value: String
)

/** Convenience holder built from the key/value rows. */
data class AppSettings(
    val doctorName: String = "نام پزشک",
    val specialty: String = "تخصص پزشک",
    val defaultFee: Long = 0L
) {
    companion object {
        const val KEY_NAME = "doctor_name"
        const val KEY_SPECIALTY = "doctor_specialty"
        const val KEY_FEE = "default_fee"

        fun fromMap(m: Map<String, String>): AppSettings {
            val d = AppSettings()
            return AppSettings(
                doctorName = m[KEY_NAME] ?: d.doctorName,
                specialty = m[KEY_SPECIALTY] ?: d.specialty,
                defaultFee = m[KEY_FEE]?.toLongOrNull() ?: d.defaultFee
            )
        }
    }
}
