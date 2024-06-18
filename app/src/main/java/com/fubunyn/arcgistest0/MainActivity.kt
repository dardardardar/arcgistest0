@file:OptIn(ExperimentalMaterial3Api::class)

package com.fubunyn.arcgistest0

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.symbology.HorizontalAlignment
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {

        val map = remember {
            createMap()
        }
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "Maps") }, actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "",
                            Modifier.width(18.dp)
                        )

                    }
                })
            },
            bottomBar = {
                botappBar()
            }
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

        return ArcGISMap(basemapStyle = getMapStyles()).apply {

            initialViewpoint = Viewpoint(
                latitude = -6.2280014,
                longitude = 106.8338039,

                scale = 1000.0

            )

        }

    }

    @Preview(showBackground = true)
    @Composable
    fun botappBar() {
        BottomAppBar() {
            Row(
                Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(text = "No reg")
                    Text(text = "1")
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    Text(text = "Palm Near")
                    Text(text = "Pohon A")
                }
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Box(Modifier.padding(12.dp)) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "",
                            Modifier.width(18.dp)
                        )

                        Text(text = "Collect", fontSize = 10.sp)
                    }
                }
            }
        }
    }

    private fun getMapStyles(): BasemapStyle {
        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            return BasemapStyle.ArcGISDarkGray
        }
        return BasemapStyle.ArcGISCommunity
    }
}
