package com.gonzup.upslt.game.menu

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.FragmentMenuBinding
import com.gonzup.upslt.game.other.ViewBindingFragment
import com.gonzup.upslt.helpful.soundClickListener

class FragmentGameMenu : ViewBindingFragment<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            playButton.soundClickListener {
                findNavController().navigate(FragmentGameMenuDirections.actionFragmentMenuToFragmentGame())
            }

            settingsButton.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMenu_to_fragmentSettings)
            }

            infoButton.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMenu_to_fragmentRules)
            }
            bonusButton.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMenu_to_dialogBonus)
            }
        }
    }
}