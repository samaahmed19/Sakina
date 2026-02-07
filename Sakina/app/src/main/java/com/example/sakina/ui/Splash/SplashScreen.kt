package com.example.sakina.ui.Splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.sakina.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import com.example.sakina.navigation.AppNavGraph


@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) { delay(2000)
        onTimeout() }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )


    }
}