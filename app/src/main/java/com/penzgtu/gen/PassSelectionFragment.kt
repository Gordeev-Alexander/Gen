package com.penzgtu.gen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.penzgtu.gen.databinding.FragmentPassSelectionBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class PassSelectionFragment : Fragment() {

    private val binding: FragmentPassSelectionBinding
        get() = _binding!!
    private var _binding: FragmentPassSelectionBinding? = null
    private val viewModel: PassSelectionViewModel by viewModels<PassSelectionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPassSelectionBinding.inflate(inflater, container, false)

        binding.passSelectionInput.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 30) {
                binding.passSelectionTextInputLayout.error =
                    "Пароль не должен превышать 30 символов!"
            } else if (text.length < 30) {
                binding.passSelectionTextInputLayout.error = null
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadDictionary(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.generatedPassword.collectLatest { password ->
                binding.passwordSelectionOutput.text = password
            }
        }

        binding.switchSemantic.isChecked = true
        binding.switchSemantic.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleSemanticReplacement(isChecked)
        }

        binding.passSelectionButtonGenerate.setOnClickListener {
            val input = binding.passSelectionInput.text?.toString()?.trim() ?: ""
            if (input.isEmpty()) {
                binding.passSelectionTextInputLayout.error = "Введите исходный пароль"
                return@setOnClickListener
            }
            if (input.length > 30) {
                binding.passSelectionTextInputLayout.error = "Пароль не должен превышать 30 символов!"
                return@setOnClickListener
            }
            binding.passSelectionTextInputLayout.error = null
            viewModel.generateSecurePassword(input)
        }

        binding.passwordSelectionOutput.setOnClickListener {
            val passwordOut = binding.passwordSelectionOutput.text.toString()
            if (passwordOut.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Password", passwordOut))
                Toast.makeText(requireContext(), "Пароль был скопирован в буфер обмена", Toast.LENGTH_SHORT).show()
            }
        }

        binding.passSelectionButtonGenerate.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.passSelectionButtonGenerate.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(100)
                        .start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.passSelectionButtonGenerate.animate()
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

