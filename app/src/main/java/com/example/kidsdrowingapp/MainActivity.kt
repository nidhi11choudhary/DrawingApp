package com.example.kidsdrowingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.CompactDecimalFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.diff_brush_size.*
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    companion object {

        const val STORAGE_PERMISSION_CODE = 1
        const val GALLARY = 2
    }

    private var mImageButtonSelectedColor : ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            draw.setBrushSize(20.toFloat())

        mImageButtonSelectedColor = ib_layout[1] as ImageButton
        mImageButtonSelectedColor!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )
        ib_brush.setOnClickListener {
            selectBrushSizeDialog()
        }

            ib_gallary.setOnClickListener {
                if(isReadStorageAllowed())
                {
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, GALLARY)
                }
                else{
                    requestStoragePermission()
                }
            }

        ib_undo.setOnClickListener {
            draw.onUndoClick()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
        {if(requestCode == GALLARY){
                if(data!!.data!=null){
                    iv_for_background.visibility = View.VISIBLE
                    iv_for_background.setImageURI(data.data)
                }
                    else{
                    Toast.makeText(this, "Error while parsing the image.",Toast.LENGTH_LONG).show()
                    }
             }
        }
    }



    fun selectBrushSizeDialog(){
        val brushSizeDialog = Dialog(this)
        brushSizeDialog.setContentView(R.layout.diff_brush_size)
        brushSizeDialog.setTitle("Brush Size")
        val small_button = brushSizeDialog.brush_smallSize
        small_button.setOnClickListener{
            draw.setBrushSize(10.toFloat())
            brushSizeDialog.dismiss()
        }
        val midium_button = brushSizeDialog.brush_mediumSize
        midium_button.setOnClickListener{
            draw.setBrushSize(20.toFloat())
            brushSizeDialog.dismiss()
        }
        val large_button = brushSizeDialog.brush_largeSize
        large_button.setOnClickListener{
            draw.setBrushSize(30.toFloat())
            brushSizeDialog.dismiss()
        }
        brushSizeDialog.show()
    }

    fun colorSelected(view : View){
        if(view !== mImageButtonSelectedColor){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            draw.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
            mImageButtonSelectedColor!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonSelectedColor = view
        }
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "Need permission to upload an image",Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "You denied the permission.",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun isReadStorageAllowed(): Boolean{
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

}

