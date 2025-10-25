package com.genxsolutions.myapplication.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.genxsolutions.myapplication.ui.theme.HeaderIconBackground
import com.genxsolutions.myapplication.ui.theme.PrimaryPurple
import com.genxsolutions.myapplication.utils.ImageImportManager
import com.genxsolutions.myapplication.ui.screens.preview.PreviewScreen
import kotlinx.coroutines.launch
import java.io.File

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun HomeScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var previewFiles by remember { mutableStateOf<List<File>?>(null) }
    var currentIndex by remember { mutableStateOf(0) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 20)
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            pendingUris = uris
            scope.launch {
                val result = ImageImportManager.importImages(context, uris)
                if (result.savedFiles.isNotEmpty()) {
                    previewFiles = result.savedFiles
                    currentIndex = 0
                } else {
                    val msg = buildString {
                        append("No images imported")
                        if (result.failures.isNotEmpty()) {
                            append(" • Failed: ")
                            append(result.failures.size)
                        }
                    }
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    // If we have files, show preview screen full-screen
    val files = previewFiles
    if (files != null) {
        PreviewScreen(
            files = files,
            currentIndex = currentIndex,
            onPrev = { if (currentIndex > 0) currentIndex-- },
            onNext = { if (currentIndex < files.lastIndex) currentIndex++ },
            onBack = { previewFiles = null },
            onDone = { previewFiles = null }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { HomeHeader() },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        pickImagesLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            // Content intentionally left minimal as per request
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {}
        }
    }
}

@Composable
private fun HomeHeader() {
    Surface(color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Leading squared icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HeaderIconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    tint = PrimaryPurple
                )
            }

            // Title: PDF Reader (PDF in purple)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.Bold)) {
                        append("PDF ")
                    }
                    withStyle(SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                        append("Reader")
                    }
                },
                fontSize = 24.sp
            )

            // Settings square button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HeaderIconBackground)
                    .clickable { /* TODO: open settings */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = PrimaryPurple
                )
            }
        }
    }
}
