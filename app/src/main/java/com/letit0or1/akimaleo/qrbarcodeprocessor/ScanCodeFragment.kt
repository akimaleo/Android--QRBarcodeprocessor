package com.letit0or1.akimaleo.qrbarcodeprocessor

import android.Manifest
import android.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_scan_code.*
import kotlinx.android.synthetic.main.fragment_scan_code.view.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.content.Intent
import android.net.Uri
import android.provider.Settings


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

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {

        fun startCamera() {
            mScannerView.startCamera()
            grand_permissions.visibility = View.GONE
        }

        fun requestPermissions() {
            grand_permissions.visibility = View.VISIBLE
            grand_permissions.setOnClickListener { checkPermissions() }
        }

        fun navigateSettingsForPermissions() {
            grand_permissions.visibility = View.VISIBLE
            grand_permissions.setOnClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

        RxPermissions(activity)
                .requestEach(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe { permission ->
                    when {
                        permission.granted -> startCamera()
                        permission.shouldShowRequestPermissionRationale -> requestPermissions()
                        else -> navigateSettingsForPermissions()
                    }
                }
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
