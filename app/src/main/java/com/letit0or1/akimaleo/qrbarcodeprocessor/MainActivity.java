package com.letit0or1.akimaleo.qrbarcodeprocessor;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class MainActivity extends Activity implements ZBarScannerView.ResultHandler, View.OnTouchListener {

    private ZBarScannerView mScannerView;
    private RelativeLayout baseLayout;

    private float startY;
    private int startHeight;

    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;

    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        baseLayout = (RelativeLayout) findViewById(R.id.base_popup_layout);
        baseLayout.setOnTouchListener(this);
        startHeight = baseLayout.getHeight();
        startY = baseLayout.getY();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            mScannerView = (ZBarScannerView) findViewById(R.id.scanner);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView = (ZBarScannerView) findViewById(R.id.scanner);
                } else {
                    Toast.makeText(this, "Permission denied to read your camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mScannerView != null) {
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();
        }          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null)
            mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
//        mScannerView.stopCamera();

        Intent intent = new Intent();
        intent.putExtra("code", rawResult.getContents());
        intent.putExtra("format", rawResult.getBarcodeFormat().getName());

        setResult(RESULT_OK, intent);
//        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
//        super.onBackPressed();
    }

    public void closeUpAndDismissDialog(int currentPosition) {
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, -baseLayout.getHeight() * 2);
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        isClosing = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, screenHeight + baseLayout.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        positionAnimator.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Get finger position on screen
        final int Y = (int) motionEvent.getRawY();

        // Switch on motion event type
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // save default base layout height
                defaultViewHeight = baseLayout.getHeight();

                // Init finger and view position
                previousFingerPosition = Y;
                baseLayoutPosition = (int) baseLayout.getY();
                break;

            case MotionEvent.ACTION_UP:
                // If user was doing a scroll up
                if (isScrollingUp) {

                    // Reset baselayout position
//                    baseLayout.setY(startY);

                    // We are not in scrolling up mode anymore
                    isScrollingUp = false;
                }

                // If user was doing a scroll down
                if (isScrollingDown) {
                    // Reset baselayout position
//                    baseLayout.setY(startY);
                    // Reset base layout size
                    baseLayout.requestLayout();
                    // We are not in scrolling down mode anymore
                    isScrollingDown = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Point size = new Point();
                WindowManager w = getWindowManager();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    w.getDefaultDisplay().getSize(size);
                } else {
                    Display d = w.getDefaultDisplay();
                    size.x = d.getWidth();
                    size.y = d.getHeight();
                }
                ;
                if (!isClosing) {
                    int currentYPosition = (int) baseLayout.getY();

                    // If we scroll up

                    if (previousFingerPosition > Y) {
                        // First time android rise an event for "up" move
                        if (!isScrollingUp) {
                            isScrollingUp = true;
                        }

                        // Has user scroll enough to "auto close" popup ?
                        //TODO: IF ENOUGHT TO SWIPE-CLOSE UP
//                        if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
//                            closeUpAndDismissDialog(currentYPosition);
//                            return true;
//                        }

                        //
                        baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));
                        if (baseLayout.getY() < 0)
                            baseLayout.setY(0);
                    }
                    // If we scroll down
                    else {

                        // First time android rise an event for "down" move
                        if (!isScrollingDown) {
                            isScrollingDown = true;
                        }
                        //TODO: IF ENOUGHT TO SWIPE-CLOSE DOWN

                        // Has user scroll enough to "auto close" popup ?
//                        if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
//                            closeDownAndDismissDialog(currentYPosition);
//                            return true;
//                        }

                        // Change base layout size and position (must change position because view anchor is top left corner)
                        baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));
                        if (baseLayout.getY() + baseLayout.getHeight() > size.y)
                            baseLayout.setY(size.y - baseLayout.getHeight());
                    }

                    // Update position
                    previousFingerPosition = Y;
                }
                break;
        }
        return true;
    }
}
