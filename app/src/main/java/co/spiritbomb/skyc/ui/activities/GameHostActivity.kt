package co.spiritbomb.skyc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.spiritbomb.skyc.databinding.GameActivityBinding

class GameHostActivity : AppCompatActivity() {
    private var _binding: GameActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = GameActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
