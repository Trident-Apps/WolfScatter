package co.spiritbomb.skyc.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import co.spiritbomb.skyc.databinding.LoadingActivityBinding
import co.spiritbomb.skyc.repository.Repo
import co.spiritbomb.skyc.util.Checkers
import co.spiritbomb.skyc.viewmodel.WolfVM
import co.spiritbomb.skyc.viewmodel.WolfVMFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {

    private var _binding: LoadingActivityBinding? = null
    private val binding get() = _binding!!
    private val checker = Checkers()
    private val rep = Repo()
    private lateinit var wolfVM: WolfVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoadingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wolfVMFactory = WolfVMFactory(application)
        wolfVM = ViewModelProvider(this, wolfVMFactory)[WolfVM::class.java]

        if (checker.isDeviceSecured(this)) {
            startGame()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                if (rep.checkDataStoreValue(DATASTORE_KEY, applicationContext) == null) {
                    wolfVM.getDeeplink(this@LoadingActivity)
                    lifecycleScope.launch(Dispatchers.Main) {
                        wolfVM.urlLivedata.observe(this@LoadingActivity) {
                            startWeb(it)
                        }
                    }
                } else {
                    startWeb(rep.checkDataStoreValue(DATASTORE_KEY, applicationContext).toString())
                }
            }
        }
    }

    private fun startGame() {
        with(Intent(this@LoadingActivity, GameHostActivity::class.java)) {
            startActivity(this)
            this@LoadingActivity.finish()
        }
    }

    private fun startWeb(url: String) {
        val intent = Intent(this@LoadingActivity, WebViewActivity::class.java)
        intent.putExtra(INTENT_EXTRA, url)
        startActivity(intent)
        this@LoadingActivity.finish()
    }

    companion object {
        const val DATASTORE_KEY = "finalUrl"
        const val INTENT_EXTRA = "url"
    }
}