package wow.app.accessibilityservicedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wow.app.accessibilityservicedemo.overlay.OverlayViewService
import wow.app.accessibilityservicedemo.overlay.requestOverlayPermission
import wow.app.accessibilityservicedemo.ui.theme.AccessibilityServiceDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccessibilityServiceDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var showAskOverlayPermissionDialog by remember {
                        mutableStateOf(false)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Button(
                            onClick = {
                                val permissionOk = OverlayViewService.showOverlay(this@MainActivity)
                                if (!permissionOk) {
                                    showAskOverlayPermissionDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Show")
                        }
                        Button(
                            onClick = { OverlayViewService.hideOverlay(this@MainActivity) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Hide")
                        }
                        Spacer(modifier = Modifier.padding(vertical = 24.dp))
                    }
                    if (showAskOverlayPermissionDialog) {
                        AlertDialog(
                            onDismissRequest = { showAskOverlayPermissionDialog = false },
                            confirmButton = {
                                Button(onClick = {
                                    showAskOverlayPermissionDialog = false
                                    requestOverlayPermission(this@MainActivity)
                                }) {
                                    Text(text = "Grant")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showAskOverlayPermissionDialog = false }) {
                                    Text(text = "Cancel")
                                }
                            },
                            title = {
                                Text(text = "Grant show overlay permission?")
                            },
                            text = {
                                Text(text = "Overlay permission is required to show a floating view")
                            }
                        )
                    }
                }
            }
        }
    }
}