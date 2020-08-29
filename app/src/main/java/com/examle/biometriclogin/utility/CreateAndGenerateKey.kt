package com.examle.biometriclogin.utility

import android.app.Activity
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import android.util.Base64
import android.util.Base64.NO_WRAP
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.examle.biometriclogin.utility.Constants.INIT_BIO_ENCRYPT
import com.examle.biometriclogin.utility.Constants.KEY_NAME
import com.examle.biometriclogin.utility.Constants.TYPE_OF_BIO
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.KeyGenerator


class CreateAndGenerateKey {
    private var keyStore: KeyStore? = null
    private var cipher: Cipher? = null

    fun createKeyAndEncryptPinForBiometric(activity: Activity) {

        getKeyStore()
        createNewKey()
        getCipher()
        initCipher(activity)
        initCryptObject()
    }

    private fun createNewKey() {
        keyStore?.deleteEntry(KEY_NAME)
        val keyGenParameterSpec = getKeyGenParameter()
        val keyGenerator =
            KeyGenerator.getInstance(KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getKeyGenParameter(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            KEY_NAME,
            PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        ).setBlockModes(BLOCK_MODE_CBC)
            .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true).build()
    }

    private fun getKeyStore() {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore?.load(null)
    }

    private fun getCipher(): Cipher? {
        cipher =
            Cipher.getInstance(
                KEY_ALGORITHM_AES +
                        "/" + BLOCK_MODE_CBC +
                        "/" + ENCRYPTION_PADDING_PKCS7
            )
        return cipher
    }

    private fun initCipher(activity: Activity) {

        keyStore?.load(null)
        val keyspec = keyStore?.getKey(KEY_NAME, null)

        cipher?.init(ENCRYPT_MODE, keyspec)

        BiometricUtility.storeStringPreference(
            activity, INIT_BIO_ENCRYPT,
            Base64.encodeToString(cipher?.iv, NO_WRAP)
        )
    }

    private fun initCryptObject(): BiometricPrompt.CryptoObject? {
        return cipher?.let { BiometricPrompt.CryptoObject(it) }
    }

    fun generateBioPrompt(activity: FragmentActivity) {
        val biometricType = BiometricUtility.loadStringPreference(activity, TYPE_OF_BIO)
        val biometricPromptHelper = BiometricPromptPresenter()
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("$biometricType authentication for AndroidX")
            .setSubtitle("Authenticate using $biometricType.")
            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(false)
            .build()
        initCryptObject()?.let { biometricPromptHelper.startAuth(promptInfo, it, activity) }
    }
}