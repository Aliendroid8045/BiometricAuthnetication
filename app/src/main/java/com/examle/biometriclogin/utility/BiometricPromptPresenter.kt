package com.examle.biometriclogin.utility

import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.examle.biometriclogin.communication.OnButtonSelection
import com.examle.biometriclogin.utility.Constants.IS_BIO_ENROLLED
import com.examle.biometriclogin.utility.Constants.TYPE_OF_BIO


class BiometricPromptPresenter {
    private var mBiometricPrompt: BiometricPrompt? = null
    private lateinit var listener: OnButtonSelection


    fun startAuth(
        promptInfo: BiometricPrompt.PromptInfo,
        cryptoObject: BiometricPrompt.CryptoObject,
        activity: FragmentActivity
    ) {
        displayBiometricPrompt(activity)
        try {
            mBiometricPrompt?.authenticate(promptInfo, cryptoObject)
        } catch (ex: Exception) {
            Log.d("Auth Error", "" + ex.message)
        }
    }


    private fun displayBiometricPrompt(activity: FragmentActivity) {
        Log.d("AUTH", "initializing prompt")

        val mExecutor = ContextCompat.getMainExecutor(activity)
        if (activity is OnButtonSelection) listener = activity

        mBiometricPrompt =
            BiometricPrompt(activity, mExecutor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Log.d("BioFailed", "" + errString)
                    Log.d("AUTH", "onAuthenticationError")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("AUTH", "onAuthenticationSucceeded")
                    displayBioEnableAlert(activity)
                    listener.onClick("registeredSuccessfully")


                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d("AUTH", "onAuthenticationFailed ")

                }

            })
    }

    private fun displayBioEnableAlert(activity: FragmentActivity) {
        val biometricType =
            BiometricUtility.loadStringPreference(activity, TYPE_OF_BIO)
        BiometricUtility.storeBooleanPreference(
            activity,
            IS_BIO_ENROLLED,
            true
        )
        val alertDialog = DisplayAlert()
        alertDialog.displayTwoButtonDialog(
            activity,
            "$biometricType enabled",
            "you are enrolled in $biometricType. Please use $biometricType to login."
        )
    }
}
