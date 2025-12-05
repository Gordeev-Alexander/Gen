package com.penzgtu.gen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.penzgtu.gen.databinding.FragmentPassExamBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PassExamFragment : Fragment() {

    private var _binding: FragmentPassExamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PassExamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassExamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.strength.collectLatest { strength ->
                updateIndicators(strength)
            }
        }

        binding.passExamInput.doOnTextChanged { text, _, _, _ ->
            viewModel.setPassword(text?.toString() ?: "", requireContext())
        }
    }

    private fun updateIndicators(strength: PasswordStrength) {
        val checked = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_checked)
        val unchecked = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_unchecked)

        binding.indicatorDigit.setImageDrawable(if (strength.hasDigit) checked else unchecked)
        binding.indicatorSpecial.setImageDrawable(if (strength.hasSpecial) checked else unchecked)
        binding.indicatorUppercase.setImageDrawable(if (strength.hasUppercase) checked else unchecked)

        binding.textViewCrackTime.text = "Примерное время взлома: ${strength.crackTime}"

        if (strength.isInWeakList) {
            binding.textViewWeakWarning.text = "⚠️ Этот пароль найден в списках утечек!"
        } else {
            binding.textViewWeakWarning.text = ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}