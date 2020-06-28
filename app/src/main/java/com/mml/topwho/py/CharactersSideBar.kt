package com.mml.topwho.py

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import com.mml.topwho.R

class CharactersSideBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    var characters = arrayOf(
        "#", "A", "B", "C", "D", "E", "F",
        "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
        "X", "Y", "Z"
    )
    private var paint: Paint = getPaint()
    private var defaultTextColor =
        resources.getColor(R.color.black, null) //默认拼音文字的颜色 Color.parseColor("#D2D2D2");
    private var selectedTextColor = resources.getColor(R.color.colorAccent, null)

    //选中后的拼音文字的颜色 Color.parseColor("#2DB7E1");
    private val touchedBgColor = resources.getColor(R.color.gray, null)

    //触摸时的拼音文字的颜色 Color.parseColor("#F5F5F5");
    private var text_dialog: TextView? = null
    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private var position = -1

    init {
        defaultTextColor = paint.color
    }

    fun setTextDialog(textView: TextView?) {
        text_dialog = textView
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = height
        val width = width
        val singleHeight = height / characters.size
        for (i in characters.indices) {
            if (i == position) {
                paint.color = selectedTextColor
            } else {
                paint.color = defaultTextColor
            }
            val xPos = width / 2 - paint.measureText(characters[i]) / 2
            val yPos = singleHeight * i + singleHeight.toFloat()
            canvas.drawText(characters[i], xPos, yPos, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        position = (y / (height / characters.size)).toInt()
        if (position >= 0 && position < CITY_TYPE.size) {
            onTouchingLetterChangedListener?.onTouchingLetterChanged(position, characters[position])
            when (action) {
                MotionEvent.ACTION_UP -> {
                    setBackgroundColor(Color.TRANSPARENT)
                    position = -1
                    invalidate()
                    if (text_dialog != null) {
                        text_dialog!!.visibility = INVISIBLE
                    }
                }
                else -> {
                    setBackgroundColor(touchedBgColor)
                    invalidate()
                    text_dialog!!.visibility = VISIBLE
                    text_dialog!!.text = characters[position]
                }
            }
        } else {
            setBackgroundColor(Color.TRANSPARENT)
            if (text_dialog != null) {
                text_dialog!!.visibility = INVISIBLE
            }
        }
        return true
    }

    fun setPosition(position: String) {
        characters.forEachIndexed { index, it ->
            if (it == position) {
                this.position = index
                invalidate()
            }
        }
    }

    fun setPosition(position: Int) {
        this.position = position
        invalidate()
    }

    fun setOnTouchingLetterChangedListener(
        onTouchingLetterChangedListener: OnTouchingLetterChangedListener?
    ) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(position: Int, character: String)
    }

    companion object {
        val CITY_TYPE = arrayOf(
            "#", "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z"
        )
    }
}