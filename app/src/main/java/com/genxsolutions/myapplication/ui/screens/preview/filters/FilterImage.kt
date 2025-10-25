package com.genxsolutions.myapplication.ui.screens.preview.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genxsolutions.myapplication.ui.theme.PrimaryPurple
import java.io.File
import java.io.FileOutputStream

enum class FilterType {
    NONE,
    GRAYSCALE,
    SEPIA,
    BRIGHTNESS_UP,
    BRIGHTNESS_DOWN,
    CONTRAST_UP,
    CONTRAST_DOWN,
    INVERT,
    SATURATE,
    WARM,
    COOL
}

private data class FilterOption(
    val type: FilterType,
    val label: String
)

@Composable
fun FilterImageScreen(
    file: File,
    onBack: () -> Unit,
    onSaved: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val baseBitmap = remember(file.path) {
        BitmapFactory.decodeFile(file.absolutePath)
    }

    var selectedFilter by remember { mutableStateOf(FilterType.NONE) }

    val filterOptions = listOf(
        FilterOption(FilterType.NONE, "Original"),
        FilterOption(FilterType.BRIGHTNESS_UP, "Bright+"),
        FilterOption(FilterType.BRIGHTNESS_DOWN, "Bright-"),
        FilterOption(FilterType.CONTRAST_UP, "Contrast+"),
        FilterOption(FilterType.CONTRAST_DOWN, "Contrast-"),
        FilterOption(FilterType.INVERT, "Invert"),
        FilterOption(FilterType.SATURATE, "Saturate"),
        FilterOption(FilterType.WARM, "Warm"),
        FilterOption(FilterType.COOL, "Cool"),
        FilterOption(FilterType.GRAYSCALE, "Grayscale"),
        FilterOption(FilterType.SEPIA, "Sepia"),
    )

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
                    text = "Filters",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Button(
                    onClick = {
                        val ok = if (selectedFilter == FilterType.NONE) {
                            true // no changes
                        } else {
                            applyAndSaveFilter(context, file, baseBitmap, selectedFilter)
                        }
                        onSaved(ok)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Apply")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Main preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (baseBitmap != null) {
                    val previewBitmap = remember(selectedFilter, baseBitmap) {
                        applyFilterToBitmap(baseBitmap, selectedFilter)
                    }
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("Unable to load image", color = Color.Gray)
                }
            }

            // Filter grid
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6F7FA))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filterOptions) { option ->
                    FilterThumbnail(
                        baseBitmap = baseBitmap,
                        filter = option,
                        isSelected = selectedFilter == option.type,
                        onClick = { selectedFilter = option.type }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterThumbnail(
    baseBitmap: Bitmap?,
    filter: FilterOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val thumbnail = remember(baseBitmap, filter.type) {
        baseBitmap?.let {
            // create a small preview (100x100) for performance
            val scaled = Bitmap.createScaledBitmap(it, 100, 100, true)
            applyFilterToBitmap(scaled, filter.type)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryPurple.copy(alpha = 0.15f) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryPurple else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        if (thumbnail != null) {
            Image(
                bitmap = thumbnail.asImageBitmap(),
                contentDescription = filter.label,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = filter.label,
            fontSize = 12.sp,
            color = if (isSelected) PrimaryPurple else Color.Black
        )
    }
}

private fun applyFilterToBitmap(source: Bitmap, filter: FilterType): Bitmap {
    if (filter == FilterType.NONE) return source

    val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(output)
    val paint = AndroidPaint()

    when (filter) {
        FilterType.GRAYSCALE -> {
            val matrix = ColorMatrix().apply { setSaturation(0f) }
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.SEPIA -> {
            val sepiaMatrix = ColorMatrix(
                floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(sepiaMatrix)
        }
        FilterType.BRIGHTNESS_UP -> {
            val matrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 50f,
                    0f, 1f, 0f, 0f, 50f,
                    0f, 0f, 1f, 0f, 50f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.BRIGHTNESS_DOWN -> {
            val matrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, -50f,
                    0f, 1f, 0f, 0f, -50f,
                    0f, 0f, 1f, 0f, -50f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.CONTRAST_UP -> {
            val contrast = 1.3f
            val translate = (-.5f * contrast + .5f) * 255f
            val matrix = ColorMatrix(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.CONTRAST_DOWN -> {
            val contrast = 0.7f
            val translate = (-.5f * contrast + .5f) * 255f
            val matrix = ColorMatrix(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.INVERT -> {
            val matrix = ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.SATURATE -> {
            val matrix = ColorMatrix().apply { setSaturation(1.5f) }
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.WARM -> {
            val matrix = ColorMatrix(
                floatArrayOf(
                    1.1f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 0.9f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.COOL -> {
            val matrix = ColorMatrix(
                floatArrayOf(
                    0.9f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1.1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            paint.colorFilter = ColorMatrixColorFilter(matrix)
        }
        FilterType.NONE -> { /* no-op */ }
    }

    canvas.drawBitmap(source, 0f, 0f, paint)
    return output
}

private fun applyAndSaveFilter(
    context: Context,
    targetFile: File,
    baseBitmap: Bitmap?,
    filter: FilterType
): Boolean {
    if (baseBitmap == null) return false
    return try {
        val filtered = applyFilterToBitmap(baseBitmap, filter)
        FileOutputStream(targetFile).use { fos ->
            filtered.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
