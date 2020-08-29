package com.examle.biometriclogin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.biometriclogin.R
import com.examle.biometriclogin.communication.OnButtonSelection
import com.examle.biometriclogin.utility.BiometricUtility
import com.examle.biometriclogin.utility.BiometricUtility.displayPromptForAuthentication
import com.examle.biometriclogin.utility.BiometricUtility.setBiometricType
import com.examle.biometriclogin.utility.Constants.IS_BIO_ENROLLED
import com.examle.biometriclogin.utility.Constants.TYPE_OF_BIO
import com.examle.biometriclogin.utility.DisplayAlert


class ContainerActivity : AppCompatActivity(), OnButtonSelection {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        addFragment(LoginFragment.newInstance())
    }

    override fun onClick(tag: String) {
        when (tag) {
            "SignIn" -> signIn()
            "RegisterBio" -> registerBio()
            "NotNow" -> notNow()
            "Enable" -> enableBiometric()
            "DisableBio" -> disableBio()
            "navigateHomeScreen" -> navigateUserToHomeScreen()
            "logout" -> logoutUser()
            else -> Throwable("No action found")
        }
    }

    private fun logoutUser() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private fun disableBio() {
        BiometricUtility.storeBooleanPreference(this, IS_BIO_ENROLLED, false)
    }

    private fun enableBiometric() {
        BiometricUtility.displayBiometricPromptForAuthentication(this)
    }

    private fun notNow() {
        supportFragmentManager.popBackStack()
    }

    private fun registerBio() {
        if (!BiometricUtility.isBiometricHardwareAvailable(this)) {
            BiometricUtility.displayHardwareNotSupportError(this)
            return
        }
        setBiometricType(this)
        replaceFragment(EnrollBiometricFragment.newInstance())
    }

    private fun signIn() {
        if (BiometricUtility.loadBooleanPreference(this, IS_BIO_ENROLLED)) {
            displayPromptForAuthentication(this)
            return
        } else {
            val bioType = BiometricUtility.loadStringPreference(this, TYPE_OF_BIO)
            val alert = DisplayAlert()
            alert.displayTwoButtonDialog(
                this,
                "You are not enrolled in $bioType",
                "Please enroll your $bioType to sign-in using $bioType."
            )
        }
    }

    private fun navigateUserToHomeScreen() {
        replaceFragment(HomeFragment.newInstance())
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment, "login")
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment, "register")
            .addToBackStack(null)
            .commit()
    }

}
