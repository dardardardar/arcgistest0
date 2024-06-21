@file:OptIn(ExperimentalMaterial3Api::class)

package com.fubunyn.arcgistest0

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.symbology.HorizontalAlignment
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.fubunyn.arcgistest0.ui.theme.Arcgistest0Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey()

        setContent {
            Arcgistest0Theme {
                MainScreen()
            }
        }
    }

    fun checkPermissions(context: Context): Boolean {
        // Check permissions to see if both permissions are granted.
        // Coarse location permission.
        val permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        // Fine location permission.
        val permissionCheckFineLocation = ContextCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return permissionCheckCoarseLocation && permissionCheckFineLocation
    }

    private fun setApiKey() {
        ArcGISEnvironment.applicationContext = applicationContext
        ArcGISEnvironment.apiKey =
            ApiKey.create("AAPK5d3b2261bd3c46da9e26f733d1d27eac4A7jWaJkiSwOuuLOhkiuRkNNRhAN7HOPXnu20voI17ehi_NUEnm2a4CuQIpI8iD1")
    }

    @Composable
    fun RequestPermissions(context: Context, onPermissionsGranted: () -> Unit) {

        // Create an activity result launcher using permissions contract and handle the result.
        val activityResultLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Check if both fine & coarse location permissions are true.
            if (permissions.all { it.value }) {
                onPermissionsGranted()
            } else {
                showError(context, "Location permissions were denied")
            }
        }

        LaunchedEffect(Unit) {
            activityResultLauncher.launch(
                // Request both fine and coarse location permissions.
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
                )
            )
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        ArcGISEnvironment.applicationContext = context.applicationContext

        val locationDisplay = rememberLocationDisplay().apply {
            setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
        }
        // Get the path of the mobile map package
        val mmpkFilePath =
            context.getExternalFilesDir(null)?.path + File.separator + stringResource(id = R.string.mahourivieratrails_mmpk)
        val sheetState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()
        val map = remember {
            mutableStateOf(ArcGISMap(getMapStyles()))
        }

        if (checkPermissions(context)) {
            // Permissions are already granted.
            LaunchedEffect(Unit) {
                locationDisplay.dataSource.start()
                val mapPackage = MobileMapPackage(mmpkFilePath)
                mapPackage.load().onSuccess {
                    map.value = mapPackage.maps.first()
                }.onFailure { error ->
                    showError(context, "Failed to load mobile map package: ${error.message}")
                }
            }
        } else {

            RequestPermissions(
                context = context,
                onPermissionsGranted = {
                    coroutineScope.launch {
                        locationDisplay.dataSource.start()
                    }
                }
            )

        }


        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "Maps") }, actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "",
                            Modifier.width(18.dp)
                        )

                    }
                })
            },
            bottomBar = {
                botappBar(onclick = {
                    scope.launch {
                        sheetState.bottomSheetState.expand()
                    }
                })
            }
        ) {

            MapView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                arcGISMap = map.value,
                locationDisplay = locationDisplay
            )


        }


    }

    fun showError(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

//    private fun createMap(): ArcGISMap {
//
//        return ArcGISMap(basemapStyle = getMapStyles()).apply {
//
//            initialViewpoint = Viewpoint(
//                latitude = -6.2280014,
//                longitude = 106.8338039,
//
//                scale = 1000.0
//
//            )
//
//        }
//
//    }


    @Composable
    fun ModalContent(state: BottomSheetScaffoldState, scope: CoroutineScope) {

        var value by rememberSaveable { mutableIntStateOf(0) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = "Harvest", fontSize = 24.sp)
            Spacer(Modifier.padding(8.dp))
            Text(text = "Input quantity of palm oil that has been harvested")
            Spacer(Modifier.padding(8.dp))
            Row {
                MiniIconButton(
                    onclick = { value++ },
                    imageVector = Icons.Rounded.Add
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                BasicTextField(

                    value = value.toString(),
                    onValueChange = {
                        if (it.isDigitsOnly()) {
                            value = it.toInt()
                        }
                    },

                    Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )



                Spacer(Modifier.padding(horizontal = 4.dp))
                MiniIconButton(
                    onclick = { value-- },
                    imageVector = Icons.Rounded.Close
                )
            }
            Spacer(Modifier.padding(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        state.bottomSheetState.hide()
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {


                Text(text = "Collect")

            }
        }
        if (!state.bottomSheetState.isVisible) {
            value = 0
        }
    }


    @Composable
    fun MiniIconButton(onclick: () -> Unit, imageVector: ImageVector) {
        Button(
            onClick = onclick,
            modifier = Modifier.size(32.dp),  //avoid the oval shape
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(imageVector, contentDescription = "content description")
        }
    }

    @Composable
    fun botappBar(onclick: () -> Unit) {
        BottomAppBar() {
            Row(
                Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(text = "No reg:")
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
                    onClick = onclick,
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
