package com.gonzup.upslt.game.wheel

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.FragmentBonusWheelBinding
import com.gonzup.upslt.game.other.ViewBindingFragment
import com.gonzup.upslt.helpful.soundClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class FragmentSpinningWheel :
    ViewBindingFragment<FragmentBonusWheelBinding>(FragmentBonusWheelBinding::inflate) {

    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
    }
    private val args: FragmentSpinningWheelArgs by navArgs()
    private var winnings = 0L
    private val sectorsList = mutableListOf(
        0, 2, 3, 4, 5, 6, 0, 2, 3, 4, 5, 6
    )
    private val sectorDegrees: MutableList<Int> = MutableList(sectorsList.size) { it }
    private var isSpinning = false
    private var deegree = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        winnings = args.winnings
        getDegreesForSectors()
        binding.startWheel.soundClickListener {
            spin()
        }
        binding.mainWheel.soundClickListener {
            spin()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })
    }

    private fun spin() {
        isSpinning = true

        deegree = Random.nextInt(sectorsList.size - 1)
        val rotateAnimation = RotateAnimation(
            0F, ((360 * sectorsList.size) + sectorDegrees[deegree] + sectorDegrees[1]).toFloat(),
            1, 0.5F, 1, 0.5F
        ).apply {
            duration = 3600
            fillAfter = true
            setInterpolator { amount ->
                amount * amount * (3f - 2f * amount)
            }
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    Log.d("TAGGER", "onAnimationStart")
                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding.mainWheel.isEnabled = false
                    binding.startWheel.isEnabled = false
                    val result = sectorsList[sectorsList.size - (deegree + 1)]
                    val balance = sharedPreferences.getLong("BALANCE", 5000)
                    lifecycleScope.launch {
                        delay(500)
                        if (result != 0) {
                            val total = winnings * result
                            sharedPreferences.edit().putLong("LAST_WIN", total).apply()
                            sharedPreferences.edit().putLong("BALANCE", balance + total).apply()
                            Toast.makeText(requireContext(),
                                "Your winnings multiplied $result times! Total is $total",
                                Toast.LENGTH_LONG).show()
                            findNavController().popBackStack(R.id.fragmentMenu, false)
                            findNavController().navigate(R.id.action_fragmentMenu_to_fragmentGame)
                        } else {
                            Toast.makeText(requireContext(),
                                "Your reward was canceled",
                                Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack(R.id.fragmentMenu, false)
                            findNavController().navigate(R.id.action_fragmentMenu_to_fragmentGame)
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    Log.d("TAGGER", "onAnimationRepeat")
                }
            })
        }

        binding.mainWheel.startAnimation(rotateAnimation)
    }

    private fun getDegreesForSectors() {
        val sectorDeegree = 360 / sectorsList.size

        for (i in 0 until sectorsList.size) {
            Log.d("TAGGER", "I = $i, SD = $sectorDeegree, result = ${i * sectorDeegree}")
            sectorDegrees[i] = i * sectorDeegree
        }
    }
}