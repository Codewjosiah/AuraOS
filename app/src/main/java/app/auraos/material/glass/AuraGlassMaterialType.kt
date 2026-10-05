package app.auraos.material.glass

/**
 * Material types defining physical glass optical characteristics.
 * All types are variations handled by the same underlying AuraMaterialEngine.
 */
enum class AuraGlassMaterialType(
    val title: String,
    val description: String,
    val defaultAlpha: Float,
    val blurMultiplier: Float,
    val specularMultiplier: Float,
    val borderMultiplier: Float,
    val colorBleedMultiplier: Float
) {
    /** Primary AURA material: soft, translucent, organic, fluid */
    LIQUID(
        title = "Liquid",
        description = "Soft, organic, fluid glass with subtle surface sheen",
        defaultAlpha = 0.72f,
        blurMultiplier = 1.0f,
        specularMultiplier = 1.0f,
        borderMultiplier = 1.0f,
        colorBleedMultiplier = 0.8f
    ),

    /** Sharper, clearer, high-sheen crystal glass */
    CRYSTAL(
        title = "Crystal",
        description = "Pristine, crisp, high-reflection glass with refined clarity",
        defaultAlpha = 0.55f,
        blurMultiplier = 1.25f,
        specularMultiplier = 1.4f,
        borderMultiplier = 1.3f,
        colorBleedMultiplier = 0.5f
    ),

    /** Deep obsidian, dense luxurious dark glass */
    OBSIDIAN(
        title = "Obsidian",
        description = "Deep monochrome, dense black glass with high contrast",
        defaultAlpha = 0.88f,
        blurMultiplier = 0.8f,
        specularMultiplier = 0.9f,
        borderMultiplier = 0.85f,
        colorBleedMultiplier = 0.3f
    ),

    /** Frost: diffused, soft, silky matte finish */
    FROST(
        title = "Frost",
        description = "Silky, diffuse matte glass with dispersed highlights",
        defaultAlpha = 0.84f,
        blurMultiplier = 1.4f,
        specularMultiplier = 0.6f,
        borderMultiplier = 0.9f,
        colorBleedMultiplier = 1.0f
    ),

    /** Aurora: vibrant contextual environment & wallpaper color bleed */
    AURORA(
        title = "Aurora",
        description = "Environment-reactive glass absorbing background color tones",
        defaultAlpha = 0.65f,
        blurMultiplier = 1.1f,
        specularMultiplier = 1.1f,
        borderMultiplier = 1.0f,
        colorBleedMultiplier = 1.8f
    ),

    /** Solid: lightweight zero-blur geometry fallback */
    SOLID(
        title = "Solid",
        description = "High-performance opaque substrate preserving AURA form",
        defaultAlpha = 0.96f,
        blurMultiplier = 0.0f,
        specularMultiplier = 0.5f,
        borderMultiplier = 0.7f,
        colorBleedMultiplier = 0.0f
    );

    companion object {
        val Default = LIQUID
    }
}
