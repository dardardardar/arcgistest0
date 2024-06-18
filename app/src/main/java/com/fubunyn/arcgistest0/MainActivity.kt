@file:OptIn(ExperimentalMaterial3Api::class)

package com.fubunyn.arcgistest0

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.fubunyn.arcgistest0.ui.theme.Arcgistest0Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey()
        requestPermissions()
        setContent {
            Arcgistest0Theme {
                MainScreen()
            }
        }
    }



    private fun requestPermissions() {
        // coarse location permission
        val permissionCheckCoarseLocation =
            ContextCompat.checkSelfPermission(this@MainActivity, ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        // fine location permission
        val permissionCheckFineLocation =
            ContextCompat.checkSelfPermission(this@MainActivity, ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        // if permissions are not already granted, request permission from the user
        if (!(permissionCheckCoarseLocation && permissionCheckFineLocation)) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
                2
            )
        } else {
            // permission already granted, so start the location display
            lifecycleScope.launch {

            }
        }
    }
    private fun setApiKey() {
        ArcGISEnvironment.applicationContext = applicationContext
        ArcGISEnvironment.apiKey =
            ApiKey.create("AAPK5d3b2261bd3c46da9e26f733d1d27eac4A7jWaJkiSwOuuLOhkiuRkNNRhAN7HOPXnu20voI17ehi_NUEnm2a4CuQIpI8iD1")

    }

    @Composable
    fun MainScreen() {

        val map = remember {
            createMap()
        }
        Scaffold(
            topBar = { TopAppBar(title = { Text(text = "Maps") }) }
        ) {

            MapView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                arcGISMap = map
            )

        }

    }

    private fun createMap(): ArcGISMap {

        return ArcGISMap(BasemapStyle.ArcGISDarkGray).apply {

            initialViewpoint = Viewpoint(
                latitude = -6.2280014,
                longitude = 106.8338039,

                scale = 1000.0

            )

        }

    }
}
