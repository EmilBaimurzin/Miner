package com.gonzup.upslt.game.game.dialog

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.DialogChanceBinding
import com.gonzup.upslt.helpful.ViewBindingDialog
import com.gonzup.upslt.helpful.increaseBalance
import com.gonzup.upslt.helpful.shortToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DialogChance : ViewBindingDialog<DialogChanceBinding>(DialogChanceBinding::inflate) {
    private val list: List<ImageView> by lazy {
        listOf(binding.item1, binding.item2, binding.item3)
    }
    private val sp: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
    }
    private val args: DialogChanceArgs by navArgs()
    private var winnings = 0L
    private val viewModel: ChanceViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        winnings = args.winnings
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMenu, false, saveState = false)
                true
            } else {
                false
            }
        }
        viewModel.list.observe(viewLifecycleOwner) { thisList ->
            val isOpened = thisList.find { it.first }
            if (isOpened?.first == true) {
                list.forEach { it.setOnClickListener { } }
                openCard(list[viewModel.list.value!!.indexOf(isOpened)], isOpened.second)
            } else {
                list.forEachIndexed { index, view ->
                    view.setOnClickListener {
                        MediaPlayer.create(requireContext(), R.raw.sound_card).start()
                        viewModel.openCard(index)
                    }
                }
            }
        }
    }

    private fun openCard(view: ImageView, value: Boolean) {
        lifecycleScope.launch {
            view.animate()
                .setDuration(1000)
                .rotationY(180f)
            delay(500)
            view.scaleX = -1f
            setImage(value, view)
            delay(500)
            if (value) {
                increaseBalance(sp, winnings)
                shortToast(requireContext(), "Congratulations! Your winnings are $winnings")
                findNavController().navigate(R.id.fragmentMenu)
                findNavController().navigate(R.id.action_fragmentMenu_to_fragmentGame)
            } else {
                if (sp.getBoolean("SOUND", true)) {
                    MediaPlayer.create(requireContext(), R.raw.sound_lose).start()
                }
                shortToast(requireContext(), "You lost")
                findNavController().navigate(R.id.fragmentMenu)
                findNavController().navigate(R.id.action_fragmentMenu_to_fragmentGame)
            }
        }
    }

    private fun setImage(value: Boolean, view: ImageView) {
        if (value) {
            view.setImageResource(R.drawable.item_winning)
            view.setBackgroundResource(R.drawable.bg_slot_item)
        } else {
            view.setImageResource(R.drawable.item_lost)
            view.setBackgroundResource(R.drawable.bg_slot_item)
        }
    }
}