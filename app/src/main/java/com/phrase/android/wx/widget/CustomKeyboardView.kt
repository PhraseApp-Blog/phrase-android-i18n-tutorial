package com.phrase.android.wx.widget

import android.content.Context
import android.icu.text.NumberFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.phrase.android.wx.R
import kotlinx.android.synthetic.main.view_keyboard.view.*

class CustomKeyboardView : FrameLayout {

    private var _adapter: KeyboardAdapter

    interface OnKeyboardClickListener {
        fun onKeyboardDigitClick(value: String)
        fun onKeyboardDeleteClick()
    }

    companion object {
        const val SPAN_COUNT = 3
    }

    constructor(context: Context) : this(
        context,
        null
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        0
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        View.inflate(context, R.layout.view_keyboard, this)
        keyboard.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        _adapter = KeyboardAdapter().also {
            keyboard.adapter = it
        }
    }

    fun setOnKeyboardListener(listener: OnKeyboardClickListener) {
        _adapter.clickListener = listener
    }

    class TextHolder(inflater: LayoutInflater, parent: ViewGroup) :
        ViewHolder(inflater.inflate(R.layout.item_keyboard, parent, false)) {
        fun bind(value: String) {
            (itemView as Button).text = value;
        }

    }

    class IconHolder(inflater: LayoutInflater, parent: ViewGroup) :
        ViewHolder(inflater.inflate(R.layout.item_keyboard_icon, parent, false)) {
        fun bind(resourceId: Int) {
            (itemView as ImageButton).setImageResource(resourceId);
        }
    }

    class KeyboardAdapter : RecyclerView.Adapter<ViewHolder>() {
        var clickListener: OnKeyboardClickListener? = null

        companion object {
            private const val TYPE_ICON = 0
            private const val TYPE_TEXT = 1
        }

        private val list = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "x")

        override fun getItemViewType(position: Int): Int {
            return when (list[position]) {
                "x" -> TYPE_ICON
                else -> TYPE_TEXT
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                TYPE_ICON -> IconHolder(LayoutInflater.from(parent.context), parent)
                else -> TextHolder(LayoutInflater.from(parent.context), parent)
            }
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                when (val value = list[position]) {
                    "x" -> clickListener?.onKeyboardDeleteClick()
                    else -> clickListener?.onKeyboardDigitClick(value)
                }
            }

            when (holder) {
                is IconHolder -> {
                    if (list[position] == "x") {
                        holder.bind(R.drawable.ic_backspace)
                    }
                }
                is TextHolder -> {
                    when (val value = list[position]) {
                        "" -> holder.itemView.isEnabled = false;
                        else -> holder.bind(
                            NumberFormat.getNumberInstance().format(Integer.parseInt(value))
                        )
                    }
                }
            }
        }
    }

}