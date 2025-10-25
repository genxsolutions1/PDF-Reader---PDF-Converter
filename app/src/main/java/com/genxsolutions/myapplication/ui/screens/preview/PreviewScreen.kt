package com.genxsolutions.myapplication.ui.screens.preview

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.genxsolutions.myapplication.ui.screens.preview.filters.CropImageHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genxsolutions.myapplication.ui.theme.HeaderIconBackground
import com.genxsolutions.myapplication.ui.theme.PrimaryPurple
import java.io.File

@Composable
fun PreviewScreen(
    files: List<File>,
    currentIndex: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    var refreshKey by remember { mutableIntStateOf(0) }
    var isDrawing by remember { mutableStateOf(false) }

    val cropLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val targetFile = files.getOrNull(currentIndex)
        if (targetFile != null) {
            val success = CropImageHandler.handleCropResult(
                context = context,
                resultCode = result.resultCode,
                data = result.data,
                targetFile = targetFile
            )
            if (success) {
                refreshKey++
            }
        }
    }

    if (isDrawing) {
        val file = files.getOrNull(currentIndex)
        if (file != null) {
            com.genxsolutions.myapplication.ui.screens.preview.filters.DrawImageScreen(
                file = file,
                onBack = { isDrawing = false },
                onSaved = { success ->
                    isDrawing = false
                    if (success) refreshKey++
                }
            )
        } else {
            // fallback if file missing
            isDrawing = false
        }
        return
    }

    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryPurple
                    )
                }
                Text(
                    text = "Preview",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.size(48.dp)) // balance spacing
            }

            Spacer(Modifier.height(8.dp))

            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val file = files.getOrNull(currentIndex)
                val bitmap = remember(file?.path, refreshKey) {
                    file?.takeIf { it.exists() }?.let { f ->
                        BitmapFactory.decodeFile(f.absolutePath)?.asImageBitmap()
                    }
                }
                if (bitmap != null) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        shadowElevation = 2.dp,
                        color = Color.White
                    ) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(28.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text("No image", color = Color.Gray)
                }
            }

            // Pagination controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onPrev,
                    enabled = currentIndex > 0
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
                    ) {
                        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.ChevronLeft,
                                contentDescription = "Previous",
                                tint = PrimaryPurple
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 0.dp,
                    color = Color.White,
                    border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "Page ${currentIndex + 1}/${files.size}",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                IconButton(
                    onClick = onNext,
                    enabled = currentIndex < files.lastIndex
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.25f))
                    ) {
                        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = "Next",
                                tint = PrimaryPurple
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bottom tools row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderIconBackground)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ToolItem(icon = Icons.Outlined.Crop, label = "Crop") {
                    val file = files.getOrNull(currentIndex) ?: return@ToolItem
                    CropImageHandler.launchCrop(context, file, cropLauncher)
                }
                ToolItem(icon = Icons.Outlined.Draw, label = "Draw") {
                    isDrawing = true
                }
                ToolItem(icon = Icons.Outlined.FilterAlt, label = "Filter")

                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun ToolItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick?.invoke() }
            .padding(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = PrimaryPurple)
        Text(text = label, color = PrimaryPurple, fontSize = 12.sp)
    }
}
