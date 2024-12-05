package com.an.jetpack_compose_playground.ui.component.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * A utility class to observe network connectivity changes using a Flow. This helps
 * to monitor internet connectivity in real-time and provides updates in an asynchronous manner.
 *
 * @param context The application context, used to access system services such as ConnectivityManager.
 */
class NetworkObserver(
    context: Context
) {
    // ConnectivityManager is used to check and monitor the device's network state.
    private var connectivityManager: ConnectivityManager? =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * A [Flow] that emits a boolean value `true` when the device is connected to the internet
     * and `false` when it loses connection. This provides a reactive approach to
     * handling network connectivity changes.
     */
    val isConnectedFlow: Flow<Boolean>
        get() = callbackFlow {
            // Define a NetworkCallback to handle connectivity events such as
            // network becoming available, lost, or changes in capabilities.
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                // Called when a network becomes available.
                override fun onAvailable(network: Network) {
                    // Check if the network has internet capability before emitting true.
                    connectivityManager?.getNetworkCapabilities(network)?.let {
                        if (it.hasCapability(NET_CAPABILITY_INTERNET)) {
                            trySend(true) // Emit `true` when internet capability is available.
                        }
                    }
                }

                // Called when a network is lost.
                override fun onLost(network: Network) {
                    trySend(false) // Emit `false` when the network is lost.
                }

                // Called when no networks are available or a request fails.
                override fun onUnavailable() {
                    trySend(false) // Emit `false` when no network is available.
                }

                // Called when network capabilities change, such as validation status.
                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, capabilities)
                    // Emit `true` if the network is validated, otherwise emit `false`.
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            }

            // Create a NetworkRequest specifying the types of networks and capabilities we are interested in.
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET) // Require internet capability.
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) // Include WiFi networks.
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET) // Include Ethernet networks.
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR) // Include mobile networks.
                .build()

            // Register the NetworkCallback with the ConnectivityManager to start listening for changes.
            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

            // Ensure resources are cleaned up when the flow collector is no longer active.
            awaitClose {
                // Unregister the NetworkCallback to prevent memory leaks.
                connectivityManager?.unregisterNetworkCallback(networkCallback)
            }
        }
}
