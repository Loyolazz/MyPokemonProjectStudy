package com.trainerjourney.mypokemon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PokeRed,
    onPrimary = PokeCream,
    secondary = PokeYellow,
    onSecondary = PokeNavy,
    tertiary = PokeBlue,
    background = PokeNavy,
    surface = PokeNavy,
    surfaceVariant = PokeNavy.copy(alpha = 0.8f),
    onSurface = PokeCream,
    onSurfaceVariant = PokeCream.copy(alpha = 0.75f),
    outline = PokeMint.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = PokeRed,
    onPrimary = PokeCream,
    secondary = PokeYellow,
    onSecondary = PokeNavy,
    tertiary = PokeBlue,
    background = PokeCream,
    surface = PokeCream,
    surfaceVariant = PokeMint.copy(alpha = 0.4f),
    onSurface = PokeNavy,
    onSurfaceVariant = PokeNavy.copy(alpha = 0.8f),
    outline = PokeBlue.copy(alpha = 0.35f)
)

@Composable
fun MyPokemonTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}