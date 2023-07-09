package com.gonzup.upslt.game.menu.components.rules

import android.os.Bundle
import android.view.View
import com.gonzup.upslt.databinding.FragmentRulesBinding
import com.gonzup.upslt.game.other.ViewBindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class FragmentGameRules : ViewBindingFragment<FragmentRulesBinding>(FragmentRulesBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rulesVP.adapter =
            RulesAdapter(
                childFragmentManager,
                lifecycle,
                arrayListOf(Page1(), Page2(), Page3(), Page4())
            )
        TabLayoutMediator(binding.dotsTabLayout, binding.rulesVP) { _, _ -> }.attach()
    }
}