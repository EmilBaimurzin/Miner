package com.gonzup.upslt.game.game

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.FragmentGameBinding
import com.gonzup.upslt.game.game.adapter.GamePlayAdapter
import com.gonzup.upslt.game.other.ViewBindingFragment
import com.gonzup.upslt.helpful.balance
import com.gonzup.upslt.helpful.random
import com.gonzup.upslt.helpful.soundClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentGamePlay : ViewBindingFragment<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private lateinit var gameAdapter: GamePlayAdapter
    private val viewModel: GamePlayViewModel by viewModels()
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        binding.bidSlider.valueTo = balance(sharedPreferences).toFloat()
        if (balance(sharedPreferences) < 100) viewModel.changeBidValue(0)
        binding.bidSlider.value =
            if (balance(sharedPreferences) > 100) viewModel.bid.value!!.toFloat() else 0f
        binding.collectButton.soundClickListener { collect() }
        binding.betButton.soundClickListener { bet() }
        binding.backButton.soundClickListener {
            findNavController().popBackStack()
        }
        binding.lastWinTextV.text =
            "Last Win: " + sharedPreferences.getLong("LAST_WIN", 0).toString()
        binding.balanceTextView.text =
            "Balance: " + sharedPreferences.getLong("BALANCE", 5000).toString()
        viewModel.list.observe(viewLifecycleOwner) {
            gameAdapter.items = it
            gameAdapter.notifyDataSetChanged()
        }
        viewModel.bid.observe(viewLifecycleOwner) {
            binding.bidTextView.text = it.toString()
        }
        binding.lowDifficultyText.soundClickListener {
            viewModel.changeDifficulty(true)
        }
        binding.normalDifficultyText.soundClickListener {
            viewModel.changeDifficulty(false)
        }
        binding.hardDifficultyText.soundClickListener {
            viewModel.changeDifficulty(null)
        }

        binding.bidSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.changeBidValue(value.toLong())
        }
        viewModel.difficulty.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.lowDifficultyText.setBackgroundResource(R.drawable.bg_selected)
                    binding.normalDifficultyText.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    binding.hardDifficultyText.setBackgroundColor(resources.getColor(android.R.color.transparent))
                }

                false -> {
                    binding.lowDifficultyText.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    binding.normalDifficultyText.setBackgroundResource(R.drawable.bg_selected)
                    binding.hardDifficultyText.setBackgroundColor(resources.getColor(android.R.color.transparent))
                }

                null -> {
                    binding.lowDifficultyText.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    binding.normalDifficultyText.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    binding.hardDifficultyText.setBackgroundResource(R.drawable.bg_selected)
                }
            }
        }
        viewModel.coefficient.observe(viewLifecycleOwner) {
            binding.apply {
                val coefficient = String.format("%.2f", it).replace(",", ".").toDouble()
                if (coefficient > 1.0) binding.collectButton.visibility = View.VISIBLE
                rateTextView.text = coefficient.toString() + "x"
                winningsTextView.text = (viewModel.bid.value!! * coefficient).toInt().toString();
                if (coefficient == 1.0) winningsTextView.text = "0"
            }
        }
        viewModel.bidState.observe(viewLifecycleOwner) { value ->
            binding.apply {
                winningsTextView.isVisible = value
                winningsFixedView.isVisible = value
                gameRV.isVisible = value
                rateTextView.isVisible = value
                rateFixedView.isVisible = value
                balanceTextView.isVisible = !value
                textLinearLayout.isVisible = !value
                difficultyLayout.isVisible = !value
                backButton.isVisible = !value
                betButton.isVisible = !value
                logo.isVisible = !value
                mainContainer.isVisible = !value
                fixedDifficulty.isVisible = !value
            }
        }
    }

    private fun bet() {
        val text = viewModel.bid.value!!
        val balance = sharedPreferences.getLong("BALANCE", 5000)
        if (text != 0L) {
            if (text < balance) {
                sharedPreferences.edit().putLong("BALANCE", balance - text).apply()
                viewModel.changeBidState()
                viewModel.generateList()
            } else {
                Toast.makeText(requireContext(), "Not enough money", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Place a bet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAdapter() {
        gameAdapter = GamePlayAdapter { position, value ->
            if (sharedPreferences.getBoolean("SOUND", true)) {
                MediaPlayer.create(requireContext(), R.raw.sound_card).start()
            }
            when (value) {
                true -> {
                    viewModel.openCard(position)
                    binding.collectButton.visibility = View.VISIBLE
                    viewModel.increaseCoefficient()
                }

                false -> {
                    viewModel.endGame(position)
                    lifecycleScope.launch {
                        if (1 random 3 == 1 && viewModel.coefficient.value != 1.0) {
                            delay(1500)
                            findNavController().navigate(
                                FragmentGamePlayDirections.actionFragmentGameToDialogChance(
                                    (viewModel.coefficient.value!! * viewModel.bid.value!!).toLong()
                                )
                            )
                        } else {
                            delay(1500)
                            if (sharedPreferences.getBoolean("SOUND", true)) {
                                MediaPlayer.create(requireContext(), R.raw.sound_lose).start()
                            }
                            findNavController().navigate(R.id.action_fragmentGame_to_dialogLose)
                        }
                    }
                }

                null -> {
                    viewModel.openCard(position)
                    binding.collectButton.visibility = View.GONE
                    viewModel.jackpot()
                }
            }
        }
        with(binding.gameRV) {
            adapter = gameAdapter
            layoutManager = GridLayoutManager(requireContext(), 5).apply {
                orientation = GridLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
    }

    private fun collect() {
        val winnings = (viewModel.coefficient.value!! * viewModel.bid.value!!).toLong()
        val random = (1..5).random()
        if (random == 1) {
            findNavController().navigate(
                FragmentGamePlayDirections.actionFragmentGameToWheelFragment(
                    winnings
                )
            )
        } else {
            val balance = sharedPreferences.getLong("BALANCE", 5000)
            sharedPreferences.edit().putLong(
                "BALANCE",
                (balance + winnings)
            ).apply()
            Toast.makeText(requireContext(), "Your winnings are $winnings!", Toast.LENGTH_SHORT)
                .show()
            sharedPreferences.edit().putLong("LAST_WIN", winnings).apply()
            findNavController().popBackStack()
            findNavController().navigate(R.id.action_fragmentMenu_to_fragmentGame)
        }
    }
}