package co.spiritbomb.skyc.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.spiritbomb.skyc.R
import co.spiritbomb.skyc.databinding.StartGameFragmentBinding

class StartGameFragment : Fragment() {
    private var _binding: StartGameFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StartGameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startBtn.setOnClickListener {
            findNavController().navigate(R.id.gameFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}