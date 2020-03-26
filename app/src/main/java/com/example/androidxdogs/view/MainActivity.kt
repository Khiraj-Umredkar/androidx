package com.example.androidxdogs.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.androidxdogs.R
import com.example.androidxdogs.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {

    private lateinit var navcontroller:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navcontroller = Navigation.findNavController(this,R.id.main_fragment)
        NavigationUI.setupActionBarWithNavController(this,navcontroller)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navcontroller , null)
    }

    fun checkSmsPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.SEND_SMS)){
                AlertDialog.Builder(this)
                    .setTitle("Send Sms Permission")
                    .setMessage("this App require to send an SMS")
                        .setPositiveButton("Ask ME"){ dialog, which ->  requestPermission()}
                    .setNegativeButton("No"){dialog, which ->   notifyDetailFragment(false)}
                    .show()
            }else{
                requestPermission()
            }
        }else{
            notifyDetailFragment(true)
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_SEND_SMS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyDetailFragment(true)
                }else{
                    notifyDetailFragment(false)
                }
            }
        }
    }
    private fun notifyDetailFragment(permissionGranted: Boolean) {
        val activeFragment = main_fragment.childFragmentManager.primaryNavigationFragment
        if(activeFragment is DetailedFragment) {
           (activeFragment as DetailedFragment).onPermissionResult(permissionGranted)
        }
    }
}
