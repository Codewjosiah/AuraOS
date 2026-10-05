package app.auraos.core.performance

/**
 * Performance profiles that govern rendering quality, blur fidelity,
 * animation physics, and resource consumption across all AURA surfaces.
 */
enum class AuraPerformanceProfile {
    /**
     * Flagship devices with high RAM and capable GPU.
     * Full RenderEffect liquid glass, dynamic specular lighting, continuous spring physics.
     */
    FULL,

    /**
     * Midrange devices or devices experiencing light thermal/battery pressure.
     * Optimized glass shader, controlled blur radius, streamlined physics.
     */
    BALANCED,

    /**
     * Low-RAM devices, severe thermal throttling, low battery, or power saver mode.
     * Simplified translucent surfaces, minimal expensive shader passes, cached backdrops.
     * Looks like "AURA, optimized" without sacrificing aesthetic identity.
     */
    LITE;

    val isFullBlurSupported: Boolean
        get() = this == FULL

    val isLightingEffectsEnabled: Boolean
        get() = this != LITE

    val isHighFidelitySpringsEnabled: Boolean
        get() = this == FULL

    val blurRadiusDp: Float
        get() = when (this) {
            FULL -> 24f
            BALANCED -> 12f
            LITE -> 0f
        }

    val glassBackgroundAlpha: Float
        get() = when (this) {
            FULL -> 0.70f
            BALANCED -> 0.82f
            LITE -> 0.92f
        }
}
