package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.an.jetpack_compose_playground.R
import com.an.jetpack_compose_playground.ui.common.MainScaffold
import com.an.jetpack_compose_playground.ui.component.BiometricAuthUtil

@Composable
fun BiometricAuthScreen() {
    val context = LocalContext.current

    val biometricAuthUtil = remember { BiometricAuthUtil(context) }
    val text = remember { mutableStateOf("") }

    MainScaffold(R.string.btn_txt_biometric_auth) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val title = stringResource(R.string.btn_txt_biometric_auth)
            val subtitle = stringResource(R.string.biometric_subtitle)
            val desc = stringResource(R.string.biometric_desc)

            Button(
                onClick = {
                    biometricAuthUtil.authenticate(
                        title = title,
                        subtitle = subtitle,
                        description = desc,
                        allowDeviceCredential = false,
                        listener = object : BiometricAuthUtil.BiometricAuthListener {
                            override fun onAuthenticationSuccess() {
                                // Handle success
                                text.value = context.getString(R.string.biometric_auth_success)
                            }

                            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                                // Handle error
                                text.value = String.format(
                                    format = context.getString(R.string.biometric_auth_failure),
                                    errorMessage
                                )
                            }
                        }
                    )
                }
            ) {
                Text(stringResource(R.string.btn_biometrics))
            }

            Text(text.value)
        }
    }
}