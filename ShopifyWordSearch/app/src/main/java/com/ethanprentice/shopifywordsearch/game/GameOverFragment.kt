package com.ethanprentice.shopifywordsearch.game

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.WSActivity
import com.ethanprentice.shopifywordsearch.views.WSButton


class GameOverFragment : Fragment() {

    lateinit var root: FrameLayout
    lateinit var content: ConstraintLayout

    private val actionListener
        get() = (activity as? GameOverActionListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.game_over_layout, container, false) as FrameLayout
        content = root.findViewById(R.id.content)

        // Override touches on fragments behind this one
        // Otherwise the user can interact with GameFragment when the GameOverFragment pops up
        root.setOnTouchListener { v, event -> true }

        val playAgainBtn = root.findViewById(R.id.play_again_btn) as WSButton
        playAgainBtn.setOnClickListener {
            actionListener?.playAgain()
        }

        val viewBoardBtn = root.findViewById(R.id.view_board_btn) as WSButton
        viewBoardBtn.setOnClickListener {
            actionListener?.viewBoard()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen for first onMeasure call, then animate fragment
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                slideContentUp()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() !is GameOverActionListener) {
            throw IllegalStateException("GameOverFragment's Activity must implement GameOverActionListener")
        }
        (requireActivity() as? WSActivity)?.setStatusBarColor(R.color.overlay_dialog)
    }

    override fun onDetach() {
        (requireActivity() as? WSActivity)?.clearStatusBarColor()
        super.onDetach()
    }

    /**
     * Enter animation
     */
    private fun slideContentUp() {
        content.alpha = 0f
        content.y = root.height.toFloat()
        content.invalidate()

        val displacement = resources.getDimensionPixelOffset(R.dimen.LU_3).toFloat()

        content.animate()
            .alpha(1.0f)
            .translationY(-displacement)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) { }
                override fun onAnimationCancel(animation: Animator?) { }
                override fun onAnimationStart(animation: Animator?) { }

                override fun onAnimationEnd(animation: Animator?) {
                    content.animate()
                        .translationY(0f)
                        .setDuration(100)
                        .setInterpolator(AccelerateInterpolator())
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) { }
                            override fun onAnimationCancel(animation: Animator?) { }
                            override fun onAnimationStart(animation: Animator?) { }
                            override fun onAnimationEnd(animation: Animator?) {
                                content.animate()
                                    .translationY(0f)
                                    .setDuration(100)
                                    .setInterpolator(AccelerateInterpolator())
                                    .setListener(null)
                            }
                        })
                }
            })
    }

    /**
     * Exit animation
     */
    fun slideContentDown(onAnimationEndCallback: () -> Any) {
        val displacement = resources.getDimensionPixelOffset(R.dimen.LU_3).toFloat()

        content.animate()
            .translationY(-displacement)
            .setDuration(100)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) { }
                override fun onAnimationCancel(animation: Animator?) { }
                override fun onAnimationStart(animation: Animator?) { }

                override fun onAnimationEnd(animation: Animator?) {
                    content.animate()
                        .alpha(0f)
                        .translationY(root.height.toFloat() / 2)
                        .setDuration(300)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) { }
                            override fun onAnimationCancel(animation: Animator?) { }
                            override fun onAnimationStart(animation: Animator?) { }
                            override fun onAnimationEnd(animation: Animator?) {
                                onAnimationEndCallback()
                            }
                        })
                }
            })
    }

    interface GameOverActionListener {
        fun playAgain()
        fun viewBoard()
    }

    companion object {
        const val TAG = "GameOverFragment"
    }
}