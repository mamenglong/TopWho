package com.mml.topwho

import android.content.Context
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

typealias Gesture = (MotionEvent) -> Unit

class GestureCallback {
    private var onLeftSlide: Gesture? = null
    private var onRightSlide: Gesture? = null
    private var onUpSlide: Gesture? = null
    private var onDownSlide: Gesture? = null
    private var whenLeftSlide: Gesture? = null
    private var whenRightSlide: Gesture? = null
    private var whenUpSlide: Gesture? = null
    private var whenDownSlide: Gesture? = null
    fun onLeftSlide(onLeft: Gesture) {
        onLeftSlide = onLeft
    }

    fun doOnLeftSlide(event: MotionEvent) {
        onLeftSlide?.invoke(event)
    }

    fun onRightSlide(onRight: Gesture) {
        onRightSlide = onRight
    }

    fun doOnRightSlide(event: MotionEvent) {
        onRightSlide?.invoke(event)
    }

    fun onUpSlide(onUp: Gesture) {
        onUpSlide = onUp
    }

    fun doOnUpSlide(event: MotionEvent) {
        onUpSlide?.invoke(event)
    }

    fun onDownSlide(onDown: Gesture) {
        onDownSlide = onDown
    }

    fun doOnDownSlide(event: MotionEvent) {
        onDownSlide?.invoke(event)
    }

    fun whenLeftSlide(whenLeft: Gesture) {
        whenLeftSlide = whenLeft
    }

    fun doWhenLeftSlide(event: MotionEvent) {
        whenLeftSlide?.invoke(event)
    }

    fun whenRightSlide(whenRight: Gesture) {
        whenRightSlide = whenRight
    }

    fun doWhenRightSlide(event: MotionEvent) {
        whenRightSlide?.invoke(event)
    }

    fun doWhenUpSlide(event: MotionEvent) {
        whenUpSlide?.invoke(event)
    }

    fun whenUpSlide(whenUp: Gesture) {
        whenUpSlide = whenUp
    }

    fun doWhenDownSlide(event: MotionEvent) {
        whenDownSlide?.invoke(event)
    }

    fun whenDownSlide(whenDown: Gesture) {
        whenDownSlide = whenDown
    }

}

class TouchContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var mIsLongTouching: Boolean = false
    private val LONG_CLICK_LIMIT: Long = 500
    private val TAG = "TouchContainerLayout"
    private var mIsScrolling = false
    private var downMillisTime: Long = 0L
    private var touchDownX = 0f
    private var touchDownY = 0f
    private val mVibrator: Vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val mPattern = longArrayOf(0, 100)
    private var gestureCallback: GestureCallback? = null
    private var textStr = "指示器"

    /**
     * 注册手势监听
     */
    fun registerGestureCallback(callback: GestureCallback.() -> Unit) {
        gestureCallback = GestureCallback().also(callback)
    }

    init {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            doOnTouchEvent(it, "onInterceptTouchEvent")
            // 是滑动事件就进行拦截，反之不拦截
            // 拦截后将不再回调该方法，所以后续事件需要在onTouchEvent中回调
            val value = mIsScrolling || super.onInterceptTouchEvent(ev)
            Log.i(TAG, "onInterceptTouchEvent  return:$value mScrolling:$mIsScrolling")
            return value
        } ?: return super.onInterceptTouchEvent(ev)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            doOnTouchEvent(it, "onTouchEvent")
            //拦截处理了事件
            return true
        } ?: return super.onTouchEvent(event)
    }

    private fun doOnTouchEvent(it: MotionEvent, msg: String) {
        when (it.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = it.x
                touchDownY = it.y
                downMillisTime = System.currentTimeMillis()
                Log.i(
                    TAG,
                    "$msg ACTION_DOWN mScrolling:$mIsScrolling  touchDownX:$touchDownX touchDownY:$touchDownY"
                )
                mIsScrolling = false
                mIsLongTouching = false
                // 父布局不要拦截子布局的监听
                parent.requestDisallowInterceptTouchEvent(true)
                textStr = "ACTION_DOWN"
                /*  postDelayed({
                      if (isLongTouch()) {
                          Log.i(
                              TAG,
                              "$msg ACTION_DOWN mScrolling:$mIsScrolling mIsLongTouching:$mIsLongTouching"
                          )
                          mIsLongTouching = true
                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                              mVibrator.vibrate(
                                  VibrationEffect.createWaveform(mPattern, -1),
                                  null
                              )
                          } else {
                              mVibrator.vibrate(mPattern, -1)
                          }
                          textStr = "ACTION_DOWN Long"
                      } else {
                          mIsLongTouching = false
                      }
                  }, LONG_CLICK_LIMIT)*/

            }
            MotionEvent.ACTION_MOVE -> {
                Log.i(
                    TAG,
                    "$msg ACTION_MOVE mScrolling:$mIsScrolling  touchDownX:$touchDownX touchDownY:$touchDownY"
                )
                doWhenActionMove(it, msg)
            }
            MotionEvent.ACTION_UP -> {
                Log.i(
                    TAG,
                    "$msg ACTION_UP mScrolling:$mIsScrolling  touchDownX:$touchDownX touchDownY:$touchDownY"
                )
                doWhenActionUp(it, msg)
                mIsScrolling = false
                mIsLongTouching = false
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "$msg ACTION_CANCEL mScrolling:$mIsScrolling ")
                mIsScrolling = false
            }
        }
    }

    private fun doWhenActionMove(event: MotionEvent, msg: String) {
        var msg = "$msg ACTION_MOVE doWhenActionMove"
        mIsScrolling = !isTouchSlop(event)
        Log.i(TAG, "$msg mScrolling:$mIsScrolling ")
        if (mIsScrolling) {
            val vertical = event.y - touchDownY
            val horizontal = event.x - touchDownX
            if (abs(vertical) < abs(horizontal)) {
                if (horizontal > 0) {
                    textStr = "ACTION_MOVE doWhenRightSlide"
                    msg = "$msg doWhenRightSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doWhenRightSlide(event)
                } else {
                    msg = "$msg doWhenLeftSlide mScrolling:$mIsScrolling "
                    textStr = "ACTION_MOVE doWhenLeftSlide"
                    gestureCallback?.doWhenLeftSlide(event)
                }
            } else {
                if (vertical < 0) {
                    textStr = "ACTION_MOVE doWhenUpSlide"
                    msg = "$msg doWhenUpSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doWhenUpSlide(event)
                } else {
                    textStr = "ACTION_MOVE doWhenDownSlide"
                    msg = "$msg doWhenDownSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doWhenDownSlide(event)
                }
            }
        } else {
            textStr = "ACTION_MOVE click"
            msg = "$msg click"
            if (mIsLongTouching) {
                msg = "$msg performLongClick mScrolling:$mIsScrolling "
                performLongClick()
            } else {
                msg = "$msg performClick mScrolling:$mIsScrolling "
                performClick()
            }
        }
        Log.i(TAG, msg)
    }

    private fun doWhenActionUp(event: MotionEvent, msg: String) {
        var msg = "$msg ACTION_UP doWhenActionUp"
        mIsScrolling = !isTouchSlop(event)
        Log.i(TAG, "$msg mScrolling:$mIsScrolling ")
        if (mIsScrolling) {
            val vertical = event.y - touchDownY
            val horizontal = event.x - touchDownX
            if (abs(vertical) < abs(horizontal)) {
                if (horizontal > 0) {
                    textStr = "ACTION_UP onRightSlide"
                    msg = "$msg onRightSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doOnRightSlide(event)
                } else {
                    textStr = "ACTION_UP onLeftSlide"
                    msg = "$msg onLeftSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doOnLeftSlide(event)
                }
            } else {
                if (vertical < 0) {
                    textStr = "ACTION_UP onUpSlide"
                    msg = "$msg onUpSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doOnUpSlide(event)
                } else {
                    textStr = "ACTION_UP onDownSlide"
                    msg = "$msg onDownSlide mScrolling:$mIsScrolling "
                    gestureCallback?.doOnDownSlide(event)
                }
            }
        } else {
            textStr = "ACTION_UP click"
            msg = "$msg click"
            if (mIsLongTouching) {
                msg = "$msg performLongClick mScrolling:$mIsScrolling "
                performLongClick()
            } else {
                msg = "$msg performClick mScrolling:$mIsScrolling "
                performClick()
            }
        }
        Log.i(TAG, msg)
    }

    /**
     * 判断是否是轻微滑动
     *
     * @param event
     * @return
     */
    private fun isTouchSlop(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        return abs(x - touchDownX) <= mTouchSlop * 2 && abs(y - touchDownY) <= mTouchSlop * 2
    }

    /**
     * 判断是否长按
     */
    private fun isLongTouch(): Boolean {
        val time = System.currentTimeMillis()
        return time - downMillisTime >= LONG_CLICK_LIMIT
    }

    /**
     * 判断是否是单击
     *
     * @param event
     * @return
     */
    private fun isClick(event: MotionEvent): Boolean {
        val offsetX = abs(event.x - touchDownX)
        val offsetY = abs(event.y - touchDownY)
        val time = System.currentTimeMillis() - downMillisTime
        val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        return offsetX < mTouchSlop && offsetY < mTouchSlop && time < LONG_CLICK_LIMIT
    }
}