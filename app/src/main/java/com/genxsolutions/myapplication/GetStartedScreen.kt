package com.genxsolutions.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genxsolutions.myapplication.ui.theme.BackgroundGradientEnd
import com.genxsolutions.myapplication.ui.theme.BackgroundGradientStart
import com.genxsolutions.myapplication.ui.theme.LightBlue
import com.genxsolutions.myapplication.ui.theme.PrimaryPurple

@Composable
fun GetStartedScreen(onGetStartedClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundGradientStart,
                        BackgroundGradientEnd
                    )
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 250.dp, y = 100.dp)
                .background(
                    LightBlue,
                    shape = RoundedCornerShape(100.dp)
                )
        )
        
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 280.dp, y = 200.dp)
                .background(
                    LightBlue,
                    shape = RoundedCornerShape(75.dp)
                )
        )
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = (-20).dp, y = 600.dp)
                .background(
                    LightBlue,
                    shape = RoundedCornerShape(50.dp)
                )
        )
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 50.dp, y = 700.dp)
                .background(
                    LightBlue,
                    shape = RoundedCornerShape(60.dp)
                )
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // PDF Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        PrimaryPurple,
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // PDF corner bracket top
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(4.dp)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(62.dp))
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color.White)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // PDF text
                    Text(
                        text = "PDF",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // PDF corner bracket bottom
                    Row {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(62.dp))
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(4.dp)
                            .background(Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "PDF Reader",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(120.dp))

            // Get Started Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "→",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
