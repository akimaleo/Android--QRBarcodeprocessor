package com.letit0or1.akimaleo.qrbarcodeprocessor

import android.app.Activity
import android.app.FragmentManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), View.OnTouchListener {

    private lateinit var generateFrg: GenerateCodeFragment
    private lateinit var scanFrg: ScanCodeFragment

    private lateinit var baseLayout: RelativeLayout
    private var previousFingerPosition = 0

    private val isClosing = false
    private var isScrollingUp = false
    private var isScrollingDown = false

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        generateFrg = GenerateCodeFragment()
        scanFrg = ScanCodeFragment()
        baseLayout = base_popup_layout
        baseLayout.setOnTouchListener(this)

        switchToScan()

        reading.setOnClickListener {
            switchToScan()
        }
        generate.setOnClickListener {
            switchToGenerate()
        }
    }

    private fun switchToScan() {
        val fManager: FragmentManager = fragmentManager
        val transaction = fManager.beginTransaction()
        transaction.replace(R.id.fragment_container, scanFrg)
        transaction.commit()
        reading.setBackgroundResource(R.color.colorAccent)
        generate.setBackgroundResource(R.color.colorSecondary)
    }

    private fun switchToGenerate() {
        val fManager: FragmentManager = fragmentManager
        val transaction = fManager.beginTransaction()
        transaction.replace(R.id.fragment_container, generateFrg)
        transaction.commit()
        generate.setBackgroundResource(R.color.colorAccent)
        reading.setBackgroundResource(R.color.colorSecondary)
    }

    //MOVE HANDLER
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        // Get finger position on screen
        val Y = motionEvent.rawY.toInt()
        // Switch on motion event type

        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN ->
                // save default base layout height

                // Init finger and view position
                previousFingerPosition = Y

            MotionEvent.ACTION_UP -> {
                // If user was doing a scroll up
                if (isScrollingUp) {

                    // Reset baselayout position
                    //                    baseLayout.setY(startY);

                    // We are not in scrolling up mode anymore
                    isScrollingUp = false
                }

                // If user was doing a scroll down
                if (isScrollingDown) {
                    // Reset baselayout position
                    //                    baseLayout.setY(startY);
                    // Reset base layout size
                    baseLayout!!.requestLayout()
                    // We are not in scrolling down mode anymore
                    isScrollingDown = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val size = Point()
                val w = windowManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    w.defaultDisplay.getSize(size)
                } else {
                    val d = w.defaultDisplay
                    size.x = d.width
                    size.y = d.height
                }
                if (!isClosing) {
                    val currentYPosition = baseLayout!!.y.toInt()

                    // If we scroll up

                    if (previousFingerPosition > Y) {
                        // First time android rise an event for "up" move
                        if (!isScrollingUp) {
                            isScrollingUp = true
                        }

                        // Has user scroll enough to "auto close" popup ?
                        //TODO: IF ENOUGHT TO SWIPE-CLOSE UP
                        //                        if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                        //                            closeUpAndDismissDialog(currentYPosition);
                        //                            return true;
                        //                        }

                        //
                        baseLayout.y = baseLayout.y + (Y - previousFingerPosition)
                        if (baseLayout.y < 0)
                            baseLayout.y = 0f
                    } else {

                        // First time android rise an event for "down" move
                        if (!isScrollingDown) {
                            isScrollingDown = true
                        }
                        //TODO: IF ENOUGHT TO SWIPE-CLOSE DOWN

                        // Has user scroll enough to "auto close" popup ?
                        //                        if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                        //                            closeDownAndDismissDialog(currentYPosition);
                        //                            return true;
                        //                        }

                        // Change base layout size and position (must change position because view anchor is top left corner)
                        baseLayout.y = baseLayout.y + (Y - previousFingerPosition)
                        if (baseLayout.y + baseLayout.height > size.y)
                            baseLayout.y = (size.y - baseLayout.height).toFloat()
                    }// If we scroll down

                    // Update position
                    previousFingerPosition = Y
                }
            }
        }

        return true
    }
}
