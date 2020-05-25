package com.example.jetpackkotlin.ui.utility

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.security.keystore.KeyGenParameterSpec
import android.util.Log
import android.security.keystore.KeyProperties.*
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.jetpackkotlin.ui.utility.Constants.INIT_BIO_ENCRYPT
import com.example.jetpackkotlin.ui.utility.Constants.IS_BIO_CHANGED
import com.example.jetpackkotlin.ui.utility.Constants.IS_BIO_ENROLLED
import com.example.jetpackkotlin.ui.utility.Constants.KEY_NAME
import com.example.jetpackkotlin.ui.utility.Constants.PREFERENCE_NAME
import com.example.jetpackkotlin.ui.utility.Constants.TYPE_OF_BIO
import com.example.jetpackkotlin.ui.utility.Constants.FACE_AVAILABLE
import com.example.jetpackkotlin.ui.utility.Constants.FINGERPRINT_AVAILABLE
import java.security.InvalidKeyException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object BiometricUtility {

    private lateinit var mBiometricPrompt: BiometricPrompt
    private lateinit var mBiometricPromptInfo: BiometricPrompt.PromptInfo

    private lateinit var keyStore: KeyStore


    fun storeBooleanPreference(context: Context, key: String, value: Boolean) {
        val preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(key, value)
        editor.apply()

    }

    fun storeStringPreference(context: Context, key: String, value: String) {
        val preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun loadBooleanValue(context: Context, key: String): Boolean {
        val preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return preference.getBoolean(key, false)
    }

    fun loadStringValue(context: Context, key: String): String? {
        val preference = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return preference.getString(key, "")
    }


    fun setBiometrictype(context: Context) {
        if (!isBiometricHardwareAvailable(context)) return
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)) {
            storeBooleanPreference(context, FACE_AVAILABLE, true)
            storeStringPreference(context, TYPE_OF_BIO, "Face recognition")
        }

        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            storeBooleanPreference(context, FINGERPRINT_AVAILABLE, true)
            storeStringPreference(context, TYPE_OF_BIO, "Fingerprint ")
        }

    }

    private fun isBiometricHardwareAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> return true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> return false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> return false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> return false
        }
        return false
    }

    fun getBioType(fragmentActivity: FragmentActivity): String? {
        val bioType = loadStringValue(fragmentActivity, TYPE_OF_BIO)
        return bioType
    }

    fun displayBiometricPromptForAuthentication(activity: FragmentActivity) {
        if (loadBooleanValue(activity, IS_BIO_ENROLLED)) {
            val bioType = loadStringValue(activity, TYPE_OF_BIO)

            val alert = DisplayAlert()
            alert.displayTwoButtonDialog(
                activity,
                "You are already enrolled in $bioType",
                "Please use your $bioType to sign-in using $bioType."
            )
            return
        }

        invokeAsyncTask(activity)

    }

    fun displayPromptForAuthentication(activity: FragmentActivity) {
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

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d("AUTH", "onAuthenticationFailed ")

                }

            })
        generateSecretKey()
        val SecretKey = getSecretKey()
        if (retrieveSecretKey(activity, SecretKey)) {
            mBiometricPromptInfo =
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle("${getBioType(activity)} authentication for AndroidX")
                    .setSubtitle("Authenticate using ${getBioType(activity)}.")
                    .setNegativeButtonText("Cancel").setConfirmationRequired(true)
                    .build()
            mBiometricPrompt.authenticate(mBiometricPromptInfo)

        }
    }

    private fun retrieveSecretKey(activity: FragmentActivity, secretKey: SecretKey?): Boolean {
        val iv = Base64.decode(loadStringValue(activity, INIT_BIO_ENCRYPT), Base64.NO_WRAP)
        val ivParameterSpec = IvParameterSpec(iv)
        try {
            getCipher()?.init(DECRYPT_MODE, secretKey, ivParameterSpec)

        } catch (e: InvalidKeyException) {
            Log.d("BIOFAIL", "biometric changed")
            displayBioChangePrompt(activity)
            return false
        }
        Log.d("BIOSUCCESS", "biometric not changed")
        return true
    }

    private fun displayBioChangePrompt(activity: FragmentActivity) {
        storeBooleanPreference(activity, IS_BIO_CHANGED, true)
        val displayAlertPrompt = DisplayAlert()
        displayAlertPrompt.displayTwoButtonDialog(
            activity,
            " your ${getBioType(activity)}  has been changed.",
            "To use your  ${getBioType(activity)}, please re enroll it."
        )
    }

    private fun generateSecretKey() {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_NAME)) {
            createKey()
        }

    }

    private fun getCipher(): Cipher? {

        return Cipher.getInstance(
            KEY_ALGORITHM_AES +
                    "/" + BLOCK_MODE_CBC +
                    "/" + ENCRYPTION_PADDING_PKCS7
        )
    }

    private fun createKey() {
        val keyGenParameter = getKeyGenParameter()
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameter)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey? {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey?

    }

    private fun getKeyGenParameter(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            KEY_NAME,
            PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        ).setBlockModes(BLOCK_MODE_CBC)
            .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true).build()
    }

    private fun invokeAsyncTask(activity: FragmentActivity) {
        val async = BiometricRegistrationAsync(activity)
        async.execute(null, null, null)
    }

    class BiometricRegistrationAsync(val activity: FragmentActivity) :
        AsyncTask<Void, Void, Void>() {
        val genratePrompt = CreateAndGenerateKey()
        override fun doInBackground(vararg params: Void?): Void? {
            genratePrompt.createKeyAndEncryptPinForBiometric(activity)
            return null
        }

        override fun onPostExecute(result: Void?) {
            genratePrompt.generateBioPrompt(activity)
        }
    }
}