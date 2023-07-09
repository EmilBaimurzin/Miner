package com.gonzup.upslt.game.rps

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.FragmentRpsBinding
import com.gonzup.upslt.game.other.ViewBindingFragment
import com.gonzup.upslt.helpful.increaseBalance
import com.gonzup.upslt.helpful.shortToast
import com.gonzup.upslt.helpful.soundClickListener

class FragmentRPS : ViewBindingFragment<FragmentRpsBinding>(FragmentRpsBinding::inflate) {
    private val viewModel: RPSViewModel by viewModels()
    private val args: FragmentRPSArgs by navArgs()
    private var winnings: Long = 0
    private val sp: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        winnings = args.winnings
        viewModel.gesture.observe(viewLifecycleOwner) {
            binding.apply {
                when (it.first) {
                    1 -> {
                        selectRock.setBackgroundResource(R.drawable.bg_selected_item)
                        selectPaper.setBackgroundColor(resources.getColor(android.R.color.transparent))
                        selectScissors.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    }
                    2 -> {
                        selectRock.setBackgroundColor(resources.getColor(android.R.color.transparent))
                        selectPaper.setBackgroundResource(R.drawable.bg_selected_item)
                        selectScissors.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    }
                    else -> {
                        selectRock.setBackgroundColor(resources.getColor(android.R.color.transparent))
                        selectPaper.setBackgroundColor(resources.getColor(android.R.color.transparent))
                        selectScissors.setBackgroundResource(R.drawable.bg_selected_item)
                    }
                }
            }
        }
        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scoresTextView.text = it.first.toString() + ":" + it.second.toString()
            if (it.first == 3) {
                val reward = winnings * 2
                increaseBalance(sp, reward)
                sp.edit().putLong("LAST_WIN", reward).apply()
                shortToast(requireContext(), "Your winnings are $reward")
                if (reward > sp.getLong("BIGGEST_WIN", 0)) sp.edit()
                    .putLong("BIGGEST_WIN", reward).apply()
                findNavController().popBackStack()
            }
            if (it.second == 3) {
                shortToast(requireContext(), "Better luck next time")
                findNavController().popBackStack()
            }
        }
        viewModel.isOpened.observe(viewLifecycleOwner) { value ->
            binding.apply {
                val selectors = listOf(selectRock, selectPaper, selectScissors)
                if (value) {
                    selectors.forEach { selector ->
                        selector.setOnClickListener { }
                    }
                    playerHand.setImageResource(
                        when (viewModel.gesture.value!!.first) {
                            1 -> R.drawable.img_rock
                            2 -> R.drawable.img_paper
                            else -> R.drawable.img_scissors
                        }
                    )
                    computerHand.setImageResource(
                        when (viewModel.gesture.value!!.second) {
                            1 -> R.drawable.img_rock
                            2 -> R.drawable.img_paper
                            else -> R.drawable.img_scissors
                        }
                    )
                } else {
                    selectors.forEachIndexed { index, selector ->
                        val gestureIndex = index + 1
                        selector.soundClickListener {
                            viewModel.setGesture(gestureIndex)
                        }
                    }
                    playerHand.setImageResource(R.drawable.img_rock)
                    computerHand.setImageResource(R.drawable.img_rock)
                }
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) {
            if (it != 0) {
                binding.timer.text = it.toString()
            } else {
                binding.timer.text = when (viewModel.checkWin()) {
                    true -> "You Win!"
                    false -> "You lose"
                    null -> "Draw"
                }
            }
        }
    }
}