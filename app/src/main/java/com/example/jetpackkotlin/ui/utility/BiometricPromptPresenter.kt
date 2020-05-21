package com.example.jetpackkotlin.ui.utility

import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.jetpackkotlin.ui.utility.BiometricUtility.IS_BIO_ENROLLED
import com.example.jetpackkotlin.ui.utility.BiometricUtility.TYPE_OF_BIO


class BiometricPromptPresenter {
    private var mBiometricPrompt: BiometricPrompt? = null


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

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d("AUTH", "onAuthenticationFailed ")

                }

            })
    }

    private fun displayBioEnableAlert(activity: FragmentActivity) {
        val biometricType =
            BiometricUtility.loadStringValue(activity, TYPE_OF_BIO)
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

/*mActivity?.let {
    BiometricPrompt(
        it,
        mExecutor!!,
        object : BiometricPrompt.AuthenticationCallback() {
            fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                Log.d("BioFailed", "" + errString)
                Log.d("AUTH", "onAuthenticationError prompt")

            }

            fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d("AUTH", "onAuthenticationSucceeded prompt")
            }

            fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("BioFailed", " in Authentication method")
                Log.d("AUTH", "onAuthenticationFailed prompt")
            }
        })
}*/
