package com.letit0or1.akimaleo.qrbarcodeprocessor


import android.app.Fragment
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.*
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.fragment_generate_code.*


class GenerateCodeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_generate_code, container, false)
    }

    val barCodeTypes = arrayOf(BarcodeFormat.CODE_128.toString(),
            BarcodeFormat.EAN_8.toString(),
            BarcodeFormat.EAN_13.toString(),
            BarcodeFormat.CODE_39.toString(),
            BarcodeFormat.UPC_A.toString(),
            BarcodeFormat.CODE_39.toString(),
            BarcodeFormat.QR_CODE.toString())

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        paste_from_clipboard.setOnClickListener {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            text_input.setText(clipboard.text, TextView.BufferType.EDITABLE)
        }
        //BARCODE TYPE
        val adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, barCodeTypes)
        types.adapter = adapter
    }

    @Throws(WriterException::class)
    fun generateCode(data: String, height: Int, width: Int, format: BarcodeFormat?): Bitmap {
        var format = format

        var writer: com.google.zxing.Writer = QRCodeWriter();
        val finaldata = Uri.encode(data, "utf-8")
        var bm: BitMatrix? = null
        if (format == null) {
            writer = Code128Writer()
            format = BarcodeFormat.CODE_128
        } else
            when (format) {
                BarcodeFormat.QR_CODE -> writer = QRCodeWriter()
                BarcodeFormat.UPC_A -> writer = UPCAWriter()
                BarcodeFormat.CODE_39 -> writer = Code39Writer()
                BarcodeFormat.EAN_13 -> writer = EAN13Writer()
                BarcodeFormat.EAN_8 -> writer = EAN8Writer()
                else -> writer = Code128Writer()
            }
        bm = writer.encode(finaldata, format, width, height)

        val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in 0..width - 1) {//width
            for (j in 0..height - 1) {//height
                imageBitmap.setPixel(i, j, if (bm!!.get(i, j)) Color.BLACK else Color.WHITE)
            }
        }
        return imageBitmap
    }
}
