package app.auraos.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * AURA OS Monochrome Design Tokens.
 * Fundamental Black + White visual language. Color enters solely through translucent
 * liquid glass refraction of wallpapers or context.
 */
@Immutable
object AuraColors {
    // Pure Base
    val AuraBlack = Color(0xFF000000)
    val AuraWhite = Color(0xFFFFFFFF)

    // Monochrome Scale
    val Obsidian = Color(0xFF080808)
    val NearBlack = Color(0xFF101010)
    val Carbon = Color(0xFF181818)
    val Charcoal = Color(0xFF222222)
    val DarkSlate = Color(0xFF2E2E32)
    val MidGray = Color(0xFF4A4A50)
    val Gray = Color(0xFF767680)
    val Silver = Color(0xFFA0A0A8)
    val LightGray = Color(0xFFCECED6)
    val NearWhite = Color(0xFFF2F2F7)

    // Liquid Glass Shimmer & Light Accents (Monochrome specular)
    val GlassSpecularWhite = Color(0x66FFFFFF)
    val GlassSpecularSubtle = Color(0x2EFFFFFF)
    val GlassRimLight = Color(0x40FFFFFF)
    val GlassBorderLight = Color(0x28FFFFFF)
    val GlassBorderSubtle = Color(0x18FFFFFF)
    val GlassBorderDark = Color(0x40000000)

    // Liquid Glass Tint Fillers
    val GlassFillDarkFull = Color(0xB8080808)
    val GlassFillDarkBalanced = Color(0xD9101010)
    val GlassFillDarkLite = Color(0xF2141414)

    val GlassFillLightFull = Color(0xB8F5F5F7)
    val GlassFillLightBalanced = Color(0xD9EAEAEF)
    val GlassFillLightLite = Color(0xF2E0E0E6)

    // Glows & Shadows
    val GlassShadowPure = Color(0x80000000)
    val GlassShadowAmbient = Color(0x40000000)
    val GlassHighlight = Color(0x20FFFFFF)
}
