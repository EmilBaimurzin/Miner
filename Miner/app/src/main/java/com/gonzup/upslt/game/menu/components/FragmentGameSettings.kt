package com.gonzup.upslt.game.menu.components

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.gonzup.upslt.databinding.FragmentSettingsBinding
import com.gonzup.upslt.game.other.ViewBindingFragment
import com.gonzup.upslt.helpful.soundClickListener

class FragmentGameSettings :
    ViewBindingFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonsText()
        binding.resetButton.soundClickListener {
            Toast.makeText(requireContext(), "Your Balance was reset to 5000", Toast.LENGTH_SHORT)
                .show()
            sharedPreferences.edit().putLong("BALANCE", 5000).apply()
        }
        binding.soundButton.soundClickListener {
            val soundValue = sharedPreferences.getBoolean("SOUND", true)
            sharedPreferences.edit().putBoolean("SOUND", !soundValue).apply()
            setButtonsText()
        }

        binding.menuButton.soundClickListener {
            findNavController().popBackStack()
        }

        binding.exitButton.soundClickListener {
            requireActivity().finish()
        }
    }

    private fun setButtonsText() {
        binding.soundButton.text = if (sharedPreferences.getBoolean("SOUND", true)) {
            "Sound: On"
        } else {
            "Sound: Off"
        }
    }
}