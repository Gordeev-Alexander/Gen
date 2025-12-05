package com.penzgtu.gen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.penzgtu.gen.databinding.FragmentPassGenBinding
import android.view.MotionEvent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PassGenFragment : Fragment() {
    private var _binding: FragmentPassGenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PassGenViewModel by viewModels<PassGenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassGenBinding.inflate(inflater, container, false)

        binding.sliderParam1.valueFrom = PassGenViewModel.MIN_PASSWORD_LENGTH
        binding.sliderParam1.value = PassGenViewModel.MIN_PASSWORD_LENGTH
        binding.sliderParam1.addOnChangeListener { _, value, _ ->
            viewModel.setPasswordLength(value)
        }

        binding.checkUppercase.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUpperChars(isChecked)
        }

        binding.checkDigits.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUseNumbers(isChecked)
        }

        binding.checkSymbols.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSpecialSymbol(isChecked)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.generatedPassword.collectLatest { generatedText ->
                binding.passwordOutput.text = generatedText
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.generatedPassword.collectLatest { password ->
                binding.passwordOutput.text = password
            }
        }

        viewModel.setRecommendedMode(true)
        binding.radioGroup.check(R.id.radioRecommended)
        binding.customSettingsContainer.visibility = View.GONE

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioRecommended -> {
                    binding.customSettingsContainer.visibility = View.GONE
                    viewModel.setRecommendedMode(true)
                }
                R.id.radioCustom -> {
                    binding.customSettingsContainer.visibility = View.VISIBLE
                    viewModel.setRecommendedMode(false)
                }
            }
        }

        binding.radioGroup.check(R.id.radioRecommended)

        binding.passGenButtonGenerate.setOnClickListener {
            viewModel.generatePassword()
        }

        binding.passwordOutput.setOnClickListener {
            val passwordOut = binding.passwordOutput.text.toString()
            if (passwordOut.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Password", passwordOut))
                Toast.makeText(requireContext(), "Пароль был скопирован в буфер обмена", Toast.LENGTH_SHORT).show()
            }
        }

        binding.passGenButtonGenerate.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.passGenButtonGenerate.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(100)
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.passGenButtonGenerate.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

