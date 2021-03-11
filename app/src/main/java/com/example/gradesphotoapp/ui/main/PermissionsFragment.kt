package com.example.gradesphotoapp.ui.main

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import java.util.ArrayList

class PermissionsFragment : Fragment() {

    private lateinit var granted: (permissions: List<Permission>) -> Unit
    private var denied: ((permissions: List<Permission>) -> Unit)? = null
    private var rationale: ((permissions: List<Permission>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun requestPermissions(
        permissions: Array<String>,
        granted: (permissions: List<Permission>) -> Unit,
        denied: ((permissions: List<Permission>) -> Unit)? = null,
        rationale: ((permissions: List<Permission>) -> Unit)? = null,
        forceRequest: Boolean = false
    ) {
        this.granted = granted
        this.denied = denied
        this.rationale = rationale
        val needToAskPermissions = forceRequest || permissions.any { permission ->
            val permissionGrantStatus = ActivityCompat.checkSelfPermission(requireContext(), permission)
            permissionGrantStatus != PackageManager.PERMISSION_GRANTED
        }
        if (needToAskPermissions) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        } else {
            onRequestPermissionsResult(
                permissions,
                IntArray(permissions.size).apply {
                    fill(PackageManager.PERMISSION_GRANTED)
                },
                BooleanArray(permissions.size).apply {
                    fill(false)
                }
            )
        }
    }

    internal fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            val permissionGrantStatus = ActivityCompat.checkSelfPermission(requireContext(), permission)
            permissionGrantStatus == PackageManager.PERMISSION_GRANTED
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != PERMISSIONS_REQUEST_CODE) return

        val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)

        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i])
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    private fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray,
        shouldShowRequestPermissionRationale: BooleanArray
    ) {
        var permissionsGranted = true
        var showRationale = false
        val permissionsList = ArrayList<Permission>(permissions.size)
        for (i in permissions.indices) {
            val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            permissionsGranted = permissionsGranted && granted
            if (!showRationale && !granted && shouldShowRequestPermissionRationale[i]) {
                showRationale = true
            }
            permissionsList.add(Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]))
        }

        when {
            permissionsGranted -> granted.invoke(permissionsList)
            showRationale -> rationale?.invoke(permissionsList)
            else -> denied?.invoke(permissionsList) // never ask again
        }
    }

    companion object {
        private val PERMISSIONS_REQUEST_CODE = RequestCodeGenerator.next
    }
}