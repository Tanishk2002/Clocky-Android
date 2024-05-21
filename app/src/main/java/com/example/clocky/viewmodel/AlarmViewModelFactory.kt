import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clocky.repository.AlarmRepository
import com.example.clocky.viewmodel.AlarmViewModel

class AlarmViewModelFactory(
    val application: Application,
    private val alarmRepository: AlarmRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(application, alarmRepository) as T
    }
}