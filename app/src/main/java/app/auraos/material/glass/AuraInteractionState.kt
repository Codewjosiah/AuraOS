package app.auraos.material.glass

/**
 * Physical interaction states for AURA glass components.
 */
enum class AuraInteractionState(
    val scaleMultiplier: Float,
    val highlightBoost: Float,
    val elevationOffsetDp: Float,
    val alphaMultiplier: Float
) {
    RESTING(
        scaleMultiplier = 1.0f,
        highlightBoost = 1.0f,
        elevationOffsetDp = 0f,
        alphaMultiplier = 1.0f
    ),

    PRESSED(
        scaleMultiplier = 0.97f,
        highlightBoost = 1.35f,
        elevationOffsetDp = -2f,
        alphaMultiplier = 1.1f
    ),

    FOCUSED(
        scaleMultiplier = 1.01f,
        highlightBoost = 1.25f,
        elevationOffsetDp = 2f,
        alphaMultiplier = 1.05f
    ),

    DRAGGED(
        scaleMultiplier = 1.03f,
        highlightBoost = 1.4f,
        elevationOffsetDp = 6f,
        alphaMultiplier = 1.15f
    ),

    EXPANDED(
        scaleMultiplier = 1.0f,
        highlightBoost = 1.1f,
        elevationOffsetDp = 4f,
        alphaMultiplier = 1.0f
    ),

    DISABLED(
        scaleMultiplier = 1.0f,
        highlightBoost = 0.4f,
        elevationOffsetDp = -2f,
        alphaMultiplier = 0.45f
    ),

    LOADING(
        scaleMultiplier = 0.99f,
        highlightBoost = 1.1f,
        elevationOffsetDp = 0f,
        alphaMultiplier = 0.85f
    ),

    TRANSITIONING(
        scaleMultiplier = 0.985f,
        highlightBoost = 1.2f,
        elevationOffsetDp = 2f,
        alphaMultiplier = 0.95f
    );

    val isInteractive: Boolean
        get() = this != DISABLED && this != LOADING
}
