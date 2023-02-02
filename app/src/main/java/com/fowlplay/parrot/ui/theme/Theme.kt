package com.fowlplay.parrot.ui.theme

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.TypedValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.fowlplay.parrot.viewmodel.Theme
import com.fowlplay.parrot.R

private data class ParrotColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
)

private fun loadColor(context: Context, theme: Theme, attribute: Int) =
    context.obtainStyledAttributes(theme.toStyle(), intArrayOf(attribute)).let {
        val result = it.getColor(0, -1)
        it.recycle()
        result
    }

private fun loadColorScheme(context: Context, theme: Theme): ParrotColors {
    return ParrotColors(
        Color(loadColor(context, theme, R.attr.colorPrimary)),
        Color(loadColor(context, theme, R.attr.colorSecondary)),
        Color(loadColor(context, theme, R.attr.colorTertiary))
    )
}

private fun getLightColorScheme(parrotColors: ParrotColors) =
    lightColorScheme(
        primary = parrotColors.primary,
        secondary = parrotColors.secondary,
        tertiary = parrotColors.tertiary
    )

private fun getDarkColorScheme(parrotColors: ParrotColors) =
    darkColorScheme(
        primary = parrotColors.primary,
        secondary = parrotColors.secondary,
        tertiary = parrotColors.tertiary
    )

@Composable
fun ParrotTheme(
    theme: Theme = Theme.BlueYellowMacaw,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> getDarkColorScheme(loadColorScheme(LocalContext.current, Theme.AfricanGrey))
        else -> getLightColorScheme(loadColorScheme(LocalContext.current, theme))
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}