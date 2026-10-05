package app.auraos.material.glass

/**
 * Depth hierarchy for AURA surfaces, governing atmospheric elevation,
 * shadow diffusion, highlight intensity, and visual z-order.
 */
enum class AuraDepth(
    val level: Int,
    val elevationDp: Float,
    val shadowAlpha: Float,
    val shadowSpreadDp: Float,
    val highlightMultiplier: Float,
    val scaleFactor: Float
) {
    LEVEL_0(
        level = 0,
        elevationDp = 0f,
        shadowAlpha = 0.0f,
        shadowSpreadDp = 0f,
        highlightMultiplier = 0.8f,
        scaleFactor = 1.0f
    ),
    LEVEL_1(
        level = 1,
        elevationDp = 2f,
        shadowAlpha = 0.12f,
        shadowSpreadDp = 4f,
        highlightMultiplier = 1.0f,
        scaleFactor = 1.0f
    ),
    LEVEL_2(
        level = 2,
        elevationDp = 6f,
        shadowAlpha = 0.22f,
        shadowSpreadDp = 8f,
        highlightMultiplier = 1.15f,
        scaleFactor = 1.005f
    ),
    LEVEL_3(
        level = 3,
        elevationDp = 12f,
        shadowAlpha = 0.32f,
        shadowSpreadDp = 16f,
        highlightMultiplier = 1.3f,
        scaleFactor = 1.01f
    ),
    LEVEL_4(
        level = 4,
        elevationDp = 24f,
        shadowAlpha = 0.45f,
        shadowSpreadDp = 28f,
        highlightMultiplier = 1.5f,
        scaleFactor = 1.02f
    );

    val isElevated: Boolean
        get() = level > 0

    companion object {
        val Default = LEVEL_1
    }
}
