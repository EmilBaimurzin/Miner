package com.gonzup.upslt.game.rps

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.DialogBonusBinding
import com.gonzup.upslt.game.menu.FragmentGameMenuDirections
import com.gonzup.upslt.helpful.balance
import com.gonzup.upslt.helpful.increaseBalance
import com.gonzup.upslt.helpful.shortToast
import com.gonzup.upslt.helpful.soundClickListener

class DialogBonus : DialogFragment() {
    private lateinit var binding: DialogBonusBinding
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogBonusBinding.inflate(layoutInflater)
        dialog!!.requestWindowFeature(STYLE_NO_TITLE)
        dialog!!.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.balanceTextView.text = balance(sharedPreferences).toString()
        binding.closeButton.soundClickListener {
            findNavController().popBackStack()
        }
        binding.startButton.soundClickListener {
            val balance = balance(sharedPreferences)
            if (balance >= 300) {
                increaseBalance(sharedPreferences, -300)
                findNavController().popBackStack()
                findNavController().navigate(
                    FragmentGameMenuDirections.actionFragmentMenuToFragmentRPS(
                        300
                    )
                )
            } else {
                shortToast(requireContext(), "Not enough money")
            }
        }
    }
}