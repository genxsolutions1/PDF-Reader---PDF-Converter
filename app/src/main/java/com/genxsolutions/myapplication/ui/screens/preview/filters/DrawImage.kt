package com.genxsolutions.myapplication.ui.screens.preview.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clipToBounds
import com.genxsolutions.myapplication.ui.theme.PrimaryPurple
import java.io.File
import java.io.FileOutputStream

private data class StrokePath(
    val points: SnapshotStateList<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DrawImageScreen(
    file: File,
    onBack: () -> Unit,
    onSaved: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val baseBitmap = remember(file.path) {
        BitmapFactory.decodeFile(file.absolutePath)
    }

    var currentColor by remember { mutableStateOf(Color.Red) }
    var strokeWidth by remember { mutableStateOf(10f) }

    // Canvas size used to scale strokes back to original bitmap size for saving
    var canvasWidth by remember { mutableStateOf(1f) }
    var canvasHeight by remember { mutableStateOf(1f) }

    val strokes = remember { mutableStateListOf<StrokePath>() }
    var activeStroke by remember { mutableStateOf<StrokePath?>(null) }

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
                Text(text = "Draw", style = MaterialTheme.typography.titleLarge)
                Button(onClick = {
                    val ok = saveDrawing(context, file, baseBitmap, strokes, canvasWidth, canvasHeight)
                    onSaved(ok)
                }, shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)) {
                    Text("Save")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Drawing area with the image
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
                    val aspect = baseBitmap.width.toFloat() / baseBitmap.height.toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clipToBounds()
                            .aspectRatio(aspect)
                    ) {
                        Image(
                            bitmap = baseBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.FillBounds
                        )

                        Canvas(
                            modifier = Modifier
                                .matchParentSize()
                                .pointerInput(currentColor, strokeWidth) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            activeStroke = StrokePath(mutableStateListOf(offset), currentColor, strokeWidth)
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            activeStroke?.points?.add(change.position)
                                        },
                                        onDragEnd = {
                                            activeStroke?.let { strokes.add(it) }
                                            activeStroke = null
                                        },
                                        onDragCancel = {
                                            activeStroke = null
                                        }
                                    )
                                }
                        ) {
                            // track canvas size
                            canvasWidth = size.width
                            canvasHeight = size.height

                            // draw existing strokes
                            strokes.forEach { sp ->
                                drawPath(
                                    path = sp.points.toComposePath(),
                                    color = sp.color,
                                    style = Stroke(width = sp.strokeWidth)
                                )
                            }
                            // draw active stroke
                            activeStroke?.let { sp ->
                                drawPath(
                                    path = sp.points.toComposePath(),
                                    color = sp.color,
                                    style = Stroke(width = sp.strokeWidth)
                                )
                            }
                        }
                    }
                } else {
                    Text("Unable to load image", color = Color.Gray)
                }
            }

            // Color palette and tools
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6F7FA))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val colors = listOf(
                    Color.Black,
                    Color.White,
                    Color.Red,
                    Color.Green,
                    Color.Blue,
                    Color.Yellow,
                    Color.Cyan,
                    Color.Magenta
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    colors.forEach { c ->
                        val borderColor = if (c == currentColor) PrimaryPurple else Color.LightGray
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(2.dp, borderColor, CircleShape)
                                .noRippleClickable { currentColor = c }
                        )
                    }
                }

                // simple Clear button
                OutlinedButton(onClick = {
                    strokes.clear()
                    activeStroke = null
                }, shape = RoundedCornerShape(18.dp)) {
                    Text("Clear")
                }
            }
        }
    }
}

private fun List<Offset>.toComposePath(): Path {
    val path = Path()
    if (isNotEmpty()) {
        path.moveTo(this[0].x, this[0].y)
        for (i in 1 until size) {
            path.lineTo(this[i].x, this[i].y)
        }
    }
    return path
}

private fun saveDrawing(
    context: Context,
    targetFile: File,
    base: Bitmap?,
    strokes: List<StrokePath>,
    canvasW: Float,
    canvasH: Float
): Boolean {
    if (base == null || canvasW <= 0f || canvasH <= 0f) return false
    return try {
        val mutable = base.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = AndroidCanvas(mutable)
        val scaleX = mutable.width / canvasW
        val scaleY = mutable.height / canvasH
        val paint = AndroidPaint().apply {
            style = android.graphics.Paint.Style.STROKE
            isAntiAlias = true
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
        }
        strokes.forEach { sp ->
            paint.color = android.graphics.Color.argb(
                255,
                (sp.color.red * 255).toInt(),
                (sp.color.green * 255).toInt(),
                (sp.color.blue * 255).toInt()
            )
            paint.strokeWidth = sp.strokeWidth * ((scaleX + scaleY) / 2f)
            val path = AndroidPath()
            if (sp.points.isNotEmpty()) {
                path.moveTo(sp.points.first().x * scaleX, sp.points.first().y * scaleY)
                for (i in 1 until sp.points.size) {
                    val p = sp.points[i]
                    path.lineTo(p.x * scaleX, p.y * scaleY)
                }
            }
            canvas.drawPath(path, paint)
        }
        FileOutputStream(targetFile).use { fos ->
            mutable.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    clickable(
        interactionSource = interaction,
        indication = null,
        onClick = onClick
    )
}
