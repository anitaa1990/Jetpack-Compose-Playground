package com.an.jetpack_compose_playground.ui.component

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * A utility class to handle biometric (fingerprint and facial recognition) authentication functionality.
 */
class BiometricAuthUtil(
    private val context: Context
) {

    // Callback interface to handle the result of biometric authentication
    interface BiometricAuthListener {
        fun onAuthenticationSuccess()
        fun onAuthenticationError(errorCode: Int, errorMessage: String)
    }

    private val biometricManager: BiometricManager by lazy {
        BiometricManager.from(context)
    }

    /**
     * Checks if the device supports both fingerprint and facial recognition authentication and if it's available for use.
     * @return Boolean indicating whether biometric (fingerprint or facial recognition) authentication is supported and available.
     */
    private fun isBiometricAvailable(): Boolean {
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Creates a BiometricPrompt instance with the provided callback listener.
     * @param listener The BiometricAuthListener implementation to handle the callback events.
     * @return A BiometricPrompt instance.
     */
    private fun createBiometricPrompt(listener: BiometricAuthListener): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)

        // BiometricPrompt callback to handle success, error, or failure events
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener.onAuthenticationSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener.onAuthenticationError(errorCode, errString.toString())
            }
        }

        return BiometricPrompt(context as FragmentActivity, executor, callback)
    }

    /**
     * Builds and shows the biometric authentication prompt.
     * @param title The title of the biometric prompt.
     * @param subtitle The subtitle of the biometric prompt (optional).
     * @param description The description of the biometric prompt (optional).
     * @param allowDeviceCredential Flag to indicate if device PIN/password fallback should be allowed.
     * @param listener The BiometricAuthListener implementation to handle authentication events.
     */
    fun authenticate(
        title: String,
        subtitle: String? = null,
        description: String? = null,
        allowDeviceCredential: Boolean = false,
        listener: BiometricAuthListener
    ) {
        // Ensure that biometric is available before proceeding
        if (!isBiometricAvailable()) {
            listener.onAuthenticationError(
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                "Biometric authentication is not supported on this device."
            )
            return
        }

        // Configure allowed authenticators: fingerprint, facial recognition
        // and optionally device credentials.
        val authenticators = if (allowDeviceCredential) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .apply {
                subtitle?.let { setSubtitle(it) }
                description?.let { setDescription(it) }
            }
            .setAllowedAuthenticators(authenticators)
            // This is required unless device credentials are enabled
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = createBiometricPrompt(listener)
        biometricPrompt.authenticate(promptInfo)
    }
}
