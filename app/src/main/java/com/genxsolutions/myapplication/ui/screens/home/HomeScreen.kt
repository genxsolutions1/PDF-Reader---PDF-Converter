package com.genxsolutions.myapplication.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
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
import com.genxsolutions.myapplication.utils.RecentPdfManager
import com.genxsolutions.myapplication.utils.PdfHelper
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
    var recentPdfs by remember { mutableStateOf<List<File>>(emptyList()) }
    var refreshKey by remember { mutableStateOf(0) }

    // Load recent PDFs
    LaunchedEffect(refreshKey) {
        recentPdfs = RecentPdfManager.getRecentPdfs(context)
    }

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
            onDone = { 
                previewFiles = null
                refreshKey++ // Refresh recent PDFs list
            },
            onRemoveFile = { file ->
                val newList = files.toMutableList().also { it.remove(file) }
                if (newList.isEmpty()) {
                    previewFiles = null
                } else {
                    previewFiles = newList
                    if (currentIndex > newList.lastIndex) currentIndex = newList.lastIndex
                }
            }
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
            // Recent PDFs List
            if (recentPdfs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No PDFs yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Create your first PDF by importing images",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Recent PDFs",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(recentPdfs, key = { it.absolutePath }) { pdfFile ->
                        PdfItem(
                            file = pdfFile,
                            onShare = {
                                try {
                                    PdfHelper.sharePdf(context, pdfFile)
                                } catch (e: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to share PDF")
                                    }
                                }
                            },
                            onDelete = {
                                if (pdfFile.delete()) {
                                    refreshKey++
                                    scope.launch {
                                        snackbarHostState.showSnackbar("PDF deleted")
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to delete PDF")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfItem(
    file: File,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete PDF") },
            text = { Text("Are you sure you want to delete ${file.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PDF Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PictureAsPdf,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // File Name
            Text(
                text = file.nameWithoutExtension,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            
            // Share Icon
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Delete Icon
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
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
            horizontalArrangement = Arrangement.Center
        ) {
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
        }
    }
}
