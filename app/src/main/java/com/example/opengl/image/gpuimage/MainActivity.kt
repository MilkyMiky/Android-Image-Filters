package com.example.opengl.image.gpuimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val REQUEST_IMAGE_GALLERY = 100

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_STORAGE = 1
    }

    private var uri: Uri = Uri.EMPTY

    private val filterService = FilterService()

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

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    setFilter(seekBar!!.progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
    }

    private fun setFilter(progress: Int) {
        gpuimageview.filter = filterService.getFilter(FilterType.CLARITY, progress)
    }

    private fun mockPoints() =
        arrayListOf(
            PointF(0.0090758824112391716f, 0.0f),
            PointF(0.19306930693069307f, 0.099835008677869763f),
            PointF(0.44719469429242731f, 0.39191421659866177f),
            PointF(0.67821782178217827f, 0.68811881188118806f),
            PointF(0.87211219863136213f, 0.86798681126962796f),
            PointF(0.98102308972047103f, 0.92244225681418235f)
        )

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
