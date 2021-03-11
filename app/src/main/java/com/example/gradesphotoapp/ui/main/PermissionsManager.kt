package com.example.gradesphotoapp.ui.main

import androidx.fragment.app.FragmentManager

class PermissionsManager(
    fragmentManager: FragmentManager
) {

    private val permissionsFragment: PermissionsFragment = getPermissionsFragment(fragmentManager)

    private fun getPermissionsFragment(fragmentManager: FragmentManager): PermissionsFragment {
        var permissionsFragment: PermissionsFragment? = findPermissionsFragment(fragmentManager)
        if (permissionsFragment == null) {
            permissionsFragment = PermissionsFragment()
            fragmentManager
                .beginTransaction()
                .add(permissionsFragment, TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
            return permissionsFragment
        }
        return permissionsFragment
    }

    private fun findPermissionsFragment(fragmentManager: FragmentManager): PermissionsFragment? {
        return fragmentManager.findFragmentByTag(TAG) as PermissionsFragment?
    }

    fun requestPermissions(
        permissions: Array<String>,
        denied: ((permissions: List<Permission>) -> Unit)? = null,
        rationale: ((permissions: List<Permission>) -> Unit)? = null,
        granted: (permissions: List<Permission>) -> Unit,
        forceRequest: Boolean = false
    ) {
        permissionsFragment.requestPermissions(
            permissions,
            granted,
            denied,
            rationale,
            forceRequest
        )
    }

    fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return permissionsFragment.isPermissionsGranted(permissions)
    }

    companion object {
        private const val TAG = "PermissionsManager"
    }
}
