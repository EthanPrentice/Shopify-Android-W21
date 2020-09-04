package com.ethanprentice.shopifywordsearch.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.util.ColorUtil

/**
 * FYI: this class was copied from another personal project of mine which is why it has more functionality than needed for this app
 */
class WSButton(context: Context, attrs: AttributeSet?, defStyle: Int) : FrameLayout(context, attrs, defStyle), Checkable {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private enum class ButtonType(private val value: Int) {
        PRIMARY(0),
        SECONDARY(1),
        TERTIARY(2);
        companion object {
            private val values = values()
            fun fromValue(value: Int) = values.firstOrNull { it.value == value } ?: PRIMARY
        }
    }

    private enum class ButtonMode(private val value: Int) {
        BLOCK(0),
        INLINE(1);
        companion object {
            private val values = values()
            fun fromValue(value: Int) = values.firstOrNull { it.value == value } ?: BLOCK
        }
    }

    private enum class IconLocation(private val value: Int) {
        LEFT(0),
        RIGHT(1);
        companion object {
            private val values = values()
            fun fromValue(value: Int) = values.firstOrNull { it.value == value } ?: LEFT
        }
    }

    private var isChecked = false
    private var onCheckedChangedListener: OnCheckedChangeListener? = null

    private val buttonType: ButtonType
    private val buttonMode: ButtonMode
    private val iconLocation: IconLocation

    var text: String = ""
        set(value) {
            field = value
            textView.text = value
        }

    var icon: Drawable? = null
        set(value) {
            field = value
            initImageView()
        }

    private val textView: TextView
    private val imageView: ImageView

    private val btnBackgroundColor: Int
        get() = when(buttonType) {
            ButtonType.PRIMARY -> context.getColor(R.color.button_background)
            ButtonType.SECONDARY -> when(buttonMode) {
                ButtonMode.BLOCK -> context.getColor(R.color.button_background)
                ButtonMode.INLINE -> context.getColor(android.R.color.transparent)
            }
            ButtonType.TERTIARY -> context.getColor(android.R.color.transparent)
        }

    private val btnStrokeColor
        get() = when(buttonType) {
            ButtonType.TERTIARY -> context.getColor(android.R.color.transparent)
            else -> context.getColor(R.color.button_background)
        }

    private val btnForegroundColor: Int
        get() = when(buttonType) {
            ButtonType.PRIMARY -> context.getColor(R.color.button_foreground)
            ButtonType.SECONDARY -> when(buttonMode) {
                ButtonMode.BLOCK -> context.getColor(R.color.button_foreground)
                ButtonMode.INLINE -> context.getColor(R.color.button_background)
            }
            ButtonType.TERTIARY -> context.getColor(R.color.button_background)
        }

    private val rippleColor: Int
        get() = when(buttonType) {
            ButtonType.PRIMARY -> context.getColor(R.color.overlay_light)
            ButtonType.SECONDARY -> when(buttonMode) {
                ButtonMode.BLOCK -> context.getColor(R.color.overlay_light)
                ButtonMode.INLINE -> context.getColor(R.color.overlay_dark)
            }
            ButtonType.TERTIARY -> context.getColor(R.color.overlay_dark)
        }

    init {
        inflate(context, R.layout.button_layout, this)
        isClickable = true
        setOnClickListener {
            toggle()
        }

        textView = findViewById(R.id.btn_text_view)

        val typedArr = context.obtainStyledAttributes(attrs, R.styleable.WSButton, defStyle, 0)
        buttonType = ButtonType.fromValue(typedArr.getInt(R.styleable.WSButton_buttonType, 0))
        buttonMode = ButtonMode.fromValue(typedArr.getInt(R.styleable.WSButton_buttonMode, 0))
        text = typedArr.getString(R.styleable.WSButton_text) ?: ""
        iconLocation = IconLocation.fromValue(typedArr.getInt(R.styleable.WSButton_iconOn, 0))
        isClickable = typedArr.getBoolean(R.styleable.WSButton_android_clickable, true)

        imageView = when (iconLocation) {
            IconLocation.LEFT -> findViewById(R.id.btn_icon_left)
            IconLocation.RIGHT -> findViewById(R.id.btn_icon_right)
        }
        icon = typedArr.getDrawable(R.styleable.WSButton_icon)
        typedArr.recycle()


        if (text.isEmpty() && icon == null) {
            throw RuntimeException("Button must have text or icon")
        }

        initImageView()
        initTextView()
        setPadding()

        initForeground()
        initBackground()
    }

    fun resetStyling() {
        initImageView()
        initTextView()
        setPadding()

        initForeground()
        initBackground()
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    override fun setChecked(checked: Boolean) {
        this.isChecked = checked
        onCheckedChangedListener?.onCheckedChanged(this, checked)
    }

    fun setOnCheckedChangedListener(listener: OnCheckedChangeListener) {
        onCheckedChangedListener = listener
    }

    fun setOnCheckedChangedListener(func: (WSButton, Boolean) -> Unit) {
        onCheckedChangedListener = object : OnCheckedChangeListener {
            override fun onCheckedChanged(btn: WSButton, isChecked: Boolean) {
                func(btn, isChecked)
            }
        }
    }

    private fun setPadding() {
        if (icon != null && text.isEmpty()) { // icon only
            val padding = context.resources.getDimensionPixelOffset(R.dimen.LU_1)
            imageView.setPadding(padding, 0, padding, 0)
        }
        else if (icon == null && text.isNotEmpty()) { // text only
            val padding = context.resources.getDimensionPixelOffset(R.dimen.LU_3)
            textView.setPadding(padding, 0, padding, 0)
        }
        else { // image and text
            val innerPadding = context.resources.getDimensionPixelOffset(R.dimen.LU_2)
            val outerPadding = if (buttonType != ButtonType.TERTIARY) {
                context.resources.getDimensionPixelOffset(R.dimen.LU_3)
            } else {
                context.resources.getDimensionPixelOffset(R.dimen.LU_1_5)
            }

            if (iconLocation == IconLocation.LEFT) {
                imageView.setPadding(outerPadding, 0, 0, 0)
                textView.setPadding(innerPadding, 0, outerPadding, 0)
            } else {
                imageView.setPadding(0, 0, outerPadding, 0)
                textView.setPadding(outerPadding, 0, innerPadding, 0)
            }
        }
    }

    private fun initImageView() {
        icon?.let {
            it.setTint(btnForegroundColor)
            imageView.setImageDrawable(it)
            imageView.visibility = View.VISIBLE
        }
    }

    private fun initTextView() {
        if (text.isNotEmpty()) {
            textView.text = text
            textView.setTextColor(btnForegroundColor)
            textView.visibility = View.VISIBLE
        }
    }

    private fun initForeground() {
        foreground?.clearColorFilter()
        val foregroundDrawable = ContextCompat.getDrawable(context, R.drawable.button_ripple) as RippleDrawable
        val fgShape = foregroundDrawable.findDrawableByLayerId(android.R.id.mask) as GradientDrawable
        fgShape.color = ColorUtil.getSingleColorStateList(rippleColor)
        foregroundDrawable.setColor(fgShape.color)
        foreground = foregroundDrawable
    }

    private fun initBackground() {
        background?.clearColorFilter()
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.button_background) as GradientDrawable
        backgroundDrawable.color = ColorUtil.getSingleColorStateList(btnBackgroundColor)
        backgroundDrawable.setStroke(context.resources.getDimensionPixelOffset(R.dimen.LU_0_5), btnStrokeColor)
        background = backgroundDrawable
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(btn: WSButton, isChecked: Boolean)
    }
}