package com.gonzup.upslt.game.game.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.DialogLoseBinding
import com.gonzup.upslt.game.menu.FragmentGameMenuDirections
import com.gonzup.upslt.helpful.soundClickListener

class DialogLose : DialogFragment() {
    private lateinit var binding: DialogLoseBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogLoseBinding.inflate(layoutInflater)
        dialog!!.requestWindowFeature(STYLE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().navigate(R.id.fragmentMenu)
                true
            } else {
                false
            }
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.leaveButton.soundClickListener {
            findNavController().navigate(R.id.fragmentMenu)
        }

        binding.tryAgainButton.soundClickListener {
            findNavController().navigate(R.id.fragmentMenu)
            findNavController().navigate(FragmentGameMenuDirections.actionFragmentMenuToFragmentGame())
        }
    }
}