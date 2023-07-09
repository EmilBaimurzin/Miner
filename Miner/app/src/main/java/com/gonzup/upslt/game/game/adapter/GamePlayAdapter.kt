package com.gonzup.upslt.game.game.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.gonzup.upslt.R
import com.gonzup.upslt.databinding.ItemMinerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class GamePlayAdapter(private var itemClickListener: (Int, Boolean?) -> Unit) :
    RecyclerView.Adapter<GamePlayViewHolder>() {
    var items = mutableListOf<GamePlayItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamePlayViewHolder {
        return GamePlayViewHolder(
            ItemMinerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: GamePlayViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = items.size
}

class GamePlayViewHolder(
    private val binding: ItemMinerBinding,
    private val context: Context,
) :
    RecyclerView.ViewHolder(binding.root) {
    var itemClickListener: ((Int, Boolean?) -> Unit)? = null
    fun bind(item: GamePlayItem) {

        if (item.isCurrent) {
            flipImage(binding.itemIMG, getImageByValue(item))
        }

        CoroutineScope(Dispatchers.Main).launch {
            if (item.isOpened) {
                setImage(binding.itemIMG, item.value)
            }
        }

        if (item.isFinished) {
            binding.itemIMG.isEnabled = false
        }

        binding.itemIMG.setOnClickListener {
            binding.itemIMG.isEnabled = false
            itemClickListener?.invoke(adapterPosition, item.value)
            val image = getImageByValue(item)
            flipImage(binding.itemIMG, image)
        }
    }

    private fun getImageByValue(item: GamePlayItem): Int {
        return when (item.value) {
            true -> R.drawable.item_winning
            false -> R.drawable.item_lost
            null -> R.drawable.item_huge_win
        }
    }

    private fun setImage(img: ImageView, value: Boolean?) {
        when (value) {
            true -> img.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.item_winning,
                    null
                )
            )
            false -> img.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.item_lost,
                    null
                )
            )
            null -> img.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.item_huge_win,
                    null
                )
            )
        }
    }

    private fun flipImage(
        imgView: ImageView,
        @DrawableRes img: Int,
        onFinish: () -> Unit = {},
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            imgView.animate()
                .setDuration(1000)
                .rotationY(180F)
            delay(500)
            imgView.scaleX = -1f
            imgView.setImageResource(img)
            delay(500)
            onFinish.invoke()
        }
    }
}

data class GamePlayItem(
    var value: Boolean?,
    var isOpened: Boolean = false,
    var isFinished: Boolean = false,
    var isCurrent: Boolean = false,
    var imgValue: Boolean = Random().nextBoolean(),
)