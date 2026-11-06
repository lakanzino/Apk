package ir.clinic.secretary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ir.clinic.secretary.data.AppDatabase
import ir.clinic.secretary.data.AppSettings
import ir.clinic.secretary.data.Patient
import ir.clinic.secretary.data.Setting
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * One ViewModel shared by the activity and all three fragments
 * (via activityViewModels()), so every screen sees the same live data.
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val patientDao = db.patientDao()
    private val settingDao = db.settingDao()

    /** All patients, updated live from SQLite. */
    val patients: LiveData<List<Patient>> = patientDao.observeAll().asLiveData()

    /** Doctor name / specialty / default fee, updated live from SQLite. */
    val settings: LiveData<AppSettings> = settingDao.observeAll()
        .map { rows -> AppSettings.fromMap(rows.associate { it.key to it.value }) }
        .asLiveData()

    fun savePatient(p: Patient) = viewModelScope.launch { patientDao.insert(p) }

    fun deletePatient(p: Patient) = viewModelScope.launch { patientDao.delete(p) }

    fun saveSettings(name: String, specialty: String, fee: Long) = viewModelScope.launch {
        settingDao.upsertAll(
            listOf(
                Setting(AppSettings.KEY_NAME, name),
                Setting(AppSettings.KEY_SPECIALTY, specialty),
                Setting(AppSettings.KEY_FEE, fee.toString())
            )
        )
    }
}
