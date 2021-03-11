package com.example.gradesphotoapp.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gradesphotoapp.R
import java.io.File
import java.io.FileOutputStream

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var permissionManager: PermissionsManager

    private lateinit var vCamera: ImageButton
    private lateinit var vText: TextView
    private lateinit var vImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = PermissionsManager(childFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vCamera = view.findViewById(R.id.vCamera)
        vText = view.findViewById(R.id.vText)
        vImage = view.findViewById(R.id.vImage)
        vCamera.setOnClickListener {
            onCameraClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PHOTO_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val bitmap = data.extras?.get("data") as Bitmap
                    val path = saveToInternalStorage(bitmap)
                    vText.text = path
                    Toast.makeText(context, path, Toast.LENGTH_LONG).show()
                    path?.let {
                        openImageFromFile(it)
                    }
                } else {
                    Toast.makeText(context, "path", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                // nothing to do
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onCameraClick() {
        permissionManager.requestPermissions(
            permissions = arrayOf(Manifest.permission.CAMERA),
            granted = {
                captureImage()
            },
            denied = {
                // TODO
            },
            rationale = {
                // TODO
            }
        )
    }

    private fun captureImage() {
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE),
            PHOTO_REQUEST_CODE
        )
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val mypath = File(requireContext().filesDir, "profile.jpg")
        FileOutputStream(mypath).use {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return mypath.absolutePath
    }

    private fun openImageFromFile(path: String) {
        val image = File(path)
        if (image.exists()) {
            val bitmap = BitmapFactory.decodeFile(image.absolutePath)
            vImage.setImageBitmap(bitmap)
        }
    }

    companion object {
        fun newInstance() = MainFragment()
        private const val PHOTO_REQUEST_CODE = 1001
    }
}
