package com.example.opengl.image.gpuimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import com.example.opengl.image.gpuimage.filters.HSV
import com.example.opengl.image.gpuimage.filters.RGB
import com.example.opengl.image.gpuimage.widgets.OnColorPickedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_IMAGE_GALLERY = 100

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_STORAGE = 1
    }

    private var uri: Uri = Uri.EMPTY

    private val filterService = FilterService()

    private var rgb = RGB()
    private var rgb2 = RGB()
    private var balance = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonPick.setOnClickListener { dispatchGalleryIntent() }
        buttonSave.setOnClickListener {
            if (uri != Uri.EMPTY)
                if (!hasStoragePermission())
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE
                    )
                else saveImage(uri)
            else
                Toast.makeText(this, "Select image", Toast.LENGTH_SHORT).show()
        }

        gradient_view.setOnColorPickedListener(object : OnColorPickedListener {
            override fun onColorPicked(hue: Float, sat: Float) {
                rgb = HSV.customHsvToRgb(HSV(hue = hue, saturation = sat, value = 1.0f))
                rgb.r /= 255
                rgb.g /= 255
                rgb.b /= 255
                setFilter()
            }

        })


        seekBalance.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    balance = seekBar!!.progress / 100.0f
                    setFilter()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
    }

    private fun scaleProgress(percentage: Int): Float =
        (100 + 100) * percentage / 100.0f - 100

    private fun setFilter(progress: Int = 0, HSV: HSV = HSV()) {

        Log.d("log", "COLOR r=${rgb.r}, g=${rgb.g}, b=${rgb.b}")

        gpuimageview.filter = filterService.getFilter(
            FilterType.SPLIT_TONING, rgb = RGB(), rgb2 = rgb, balance = balance
        )
    }

    private fun setImage(imgUri: Uri) {
        gpuimageview.setImage(imgUri)
    }

    private fun saveImage(imgUri: Uri) {
        gpuimageview.saveToPictures(
            "WonderPic",
            "${imgUri.lastPathSegment}_${SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Calendar.getInstance().time)}.jpg"
        ) { uri ->
            Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> if (data?.data != null) {
                    uri = data.data!!
                    setImage(uri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage(uri)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}
