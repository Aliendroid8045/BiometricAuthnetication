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
            "signIn" -> signIn()
            "enrollBiometric" -> enrollBiometric()
            "notNow" -> notNow()
            "enable" -> enableBiometric()
            "disableBio" -> disableBio()
            "navigateHomeScreen" -> navigateUserToHomeScreen()
            "logout" -> presentSignInScreen()
            "registeredSuccessfully" -> presentSignInScreen()
            else -> Throwable("No action found")
        }
    }

    private fun presentSignInScreen() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private fun disableBio() {
        BiometricUtility.storeBooleanPreference(this, IS_BIO_ENROLLED, false)
        BiometricUtility.displayAlert(this,R.string.disable_bio_title,R.string.successfully_disable_bio)
    }

    private fun enableBiometric() {
        BiometricUtility.displayBiometricPromptForAuthentication(this)
    }

    private fun notNow() {
        supportFragmentManager.popBackStack()
    }

    private fun enrollBiometric() {
        if (!BiometricUtility.isBiometricHardwareAvailable(this)) {
            BiometricUtility.displayAlert(
                this,
                R.string.harware_not_support,
                R.string.phone_not_support_biometric
            )
            return
        }
        setBiometricType(this)
        replaceFragment(EnrollBiometricFragment.newInstance())
    }

    private fun signIn() {
        if (!BiometricUtility.isBiometricHardwareAvailable(this)) {
            BiometricUtility.displayAlert(
                this,
                R.string.harware_not_support,
                R.string.phone_not_support_biometric
            )
            return
        }
        if (BiometricUtility.loadBooleanPreference(this, IS_BIO_ENROLLED)) {
            displayPromptForAuthentication(this)
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
