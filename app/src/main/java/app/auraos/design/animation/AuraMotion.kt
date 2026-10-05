package app.auraos.design.animation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Centralized AURA OS motion architecture.
 * Frame-rate adaptive and reduced-motion compliant.
 */
object AuraMotion {
    // Easing curves
    val LiquidFluidCurve = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val GlassDecelCurve = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val SharpSnapCurve = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    // Spring presets
    fun <T> springBouncy(reducedMotion: Boolean = false): FiniteAnimationSpec<T> {
        if (reducedMotion) return tween(durationMillis = 150, easing = LinearEasing)
        return spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }

    fun <T> springFluid(reducedMotion: Boolean = false): FiniteAnimationSpec<T> {
        if (reducedMotion) return tween(durationMillis = 150, easing = LinearEasing)
        return spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )
    }

    fun <T> springSnap(reducedMotion: Boolean = false): FiniteAnimationSpec<T> {
        if (reducedMotion) return tween(durationMillis = 100, easing = LinearEasing)
        return spring(
            dampingRatio = 0.85f,
            stiffness = Spring.StiffnessMediumLow
        )
    }

    fun <T> springGentle(reducedMotion: Boolean = false): FiniteAnimationSpec<T> {
        if (reducedMotion) return tween(durationMillis = 150, easing = LinearEasing)
        return spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )
    }

    // Standard duration specs
    fun <T> auraFast(reducedMotion: Boolean = false): TweenSpec<T> {
        val duration = if (reducedMotion) 80 else 180
        return tween(durationMillis = duration, easing = FastOutSlowInEasing)
    }

    fun <T> auraStandard(reducedMotion: Boolean = false): TweenSpec<T> {
        val duration = if (reducedMotion) 120 else 280
        return tween(durationMillis = duration, easing = LiquidFluidCurve)
    }

    fun <T> auraSlow(reducedMotion: Boolean = false): TweenSpec<T> {
        val duration = if (reducedMotion) 160 else 420
        return tween(durationMillis = duration, easing = GlassDecelCurve)
    }

    fun <T> auraMorph(reducedMotion: Boolean = false): TweenSpec<T> {
        val duration = if (reducedMotion) 140 else 340
        return tween(durationMillis = duration, easing = LiquidFluidCurve)
    }
}
