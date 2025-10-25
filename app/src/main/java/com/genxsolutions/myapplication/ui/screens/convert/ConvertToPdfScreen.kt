package com.genxsolutions.myapplication.ui.screens.convert

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genxsolutions.myapplication.ui.theme.PrimaryPurple
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConvertToPdfScreen(
    files: List<File>,
    onBack: () -> Unit,
    onConvert: (List<File>) -> Unit,
    onRemoveFile: (File) -> Unit,
    isLoading: Boolean = false
) {
    // Maintain a mutable list so items can be removed from the conversion batch
    val items: SnapshotStateList<File> = remember(files) { files.toMutableStateList() }
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
                    text = "All images",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                // Placeholder for dropdown icon
                IconButton(onClick = { /* TODO: Add dropdown menu */ }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "More",
                        tint = PrimaryPurple
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // "All Files" label
            Text(
                text = "All Files",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // File list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items, key = { _, f -> f.absolutePath.hashCode() }) { index, file ->
                    ImageFileItem(
                        file = file,
                        index = index + 1,
                        onDelete = {
                            items.remove(file)
                            onRemoveFile(file)
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Convert button
            val canConvert = items.isNotEmpty()
            Button(
                onClick = { onConvert(items.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = canConvert && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    disabledContainerColor = PrimaryPurple.copy(alpha = 0.4f)
                )
            ) {
                Text("Convert", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ImageFileItem(
    file: File,
    index: Int,
    onDelete: () -> Unit
) {
    val bitmap = remember(file.path) {
        try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val fileDate = remember(file) {
        dateFormat.format(Date(file.lastModified()))
    }
    val fileSize = remember(file) {
        val sizeInKB = file.length() / 1024.0
        String.format("%.1f KB", sizeInKB)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE8E8E8)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "IMG",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // File info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Image_$index.jpg",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Small red badge like in the design
                Box(
                    modifier = Modifier
                        .size(18.dp, 12.dp)
                        .background(Color.Red, RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "IMG",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$fileDate . $fileSize",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Three-dot menu with dropdown
        var expanded by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Options",
                    tint = Color.Gray
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}
