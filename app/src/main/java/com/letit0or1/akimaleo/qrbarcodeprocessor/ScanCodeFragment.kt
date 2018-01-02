package com.letit0or1.akimaleo.qrbarcodeprocessor

import android.Manifest
import android.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_scan_code.*
import kotlinx.android.synthetic.main.fragment_scan_code.view.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class ScanCodeFragment : Fragment(), ZBarScannerView.ResultHandler {

    private lateinit var mScannerView: ZBarScannerView
    private lateinit var baseLayout: RelativeLayout
    private lateinit var result: EditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_scan_code, container, false)

        result = view.result_output
        baseLayout = view.base_popup_layout
        mScannerView = view.scanner

        view.copyToClipboard.setOnClickListener {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Code info", result.text)
            clipboard.primaryClip = clip
            Toast.makeText(activity, getString(R.string.copied), Toast.LENGTH_SHORT).show()
        }

        mScannerView.setOnClickListener {
            mScannerView.resumeCameraPreview(this)
        }

        return view
    }

    private fun checkPermissions() {
        
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView = scanner
                } else {
                    Toast.makeText(activity, "Permission denied to read your camera", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mScannerView != null) {
            mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
            mScannerView!!.startCamera()
        }          // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        if (mScannerView != null) {// Stop camera on pause
            mScannerView!!.stopCamera()
        }
    }

    override fun handleResult(rawResult: Result) {
        result!!.setText(rawResult.contents)
    }
}
