package app.auraos.material.glass

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography

enum class GalleryBackground(val title: String) {
    BLACK("Black"),
    WHITE("White"),
    DARK_PHOTO("Dark Photo"),
    BRIGHT_PHOTO("Bright Photo"),
    COLORFUL("Colorful"),
    HIGH_DETAIL("High-Detail")
}

@Composable
fun AuraMaterialGallery(
    currentProfile: AuraPerformanceProfile,
    onProfileChange: (AuraPerformanceProfile) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    var selectedBg by remember { mutableStateOf(GalleryBackground.BLACK) }
    var selectedMaterialType by remember { mutableStateOf(AuraGlassMaterialType.LIQUID) }
    var selectedDepth by remember { mutableStateOf(AuraDepth.LEVEL_1) }
    var liveConfig by remember {
        mutableStateOf(AuraMaterialConfig.forProfileAndType(currentProfile, selectedMaterialType))
    }

    var sliderValue by remember { mutableFloatStateOf(0.65f) }
    var selectedNavId by remember { mutableStateOf("home") }
    var isSheetVisible by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var isInspectorVisible by remember { mutableStateOf(false) }

    // Dynamic background brush according to selected background test environment
    val bgModifier = when (selectedBg) {
        GalleryBackground.BLACK -> Modifier.background(Color.Black)
        GalleryBackground.WHITE -> Modifier.background(Color(0xFFEEEEF2))
        GalleryBackground.DARK_PHOTO -> Modifier.background(
            Brush.radialGradient(
                colors = listOf(Color(0xFF1E232A), Color(0xFF090A0D)),
                center = Offset(400f, 300f),
                radius = 1200f
            )
        )
        GalleryBackground.BRIGHT_PHOTO -> Modifier.background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFFD6E4F0), Color(0xFFF9F7F7), Color(0xFFB9D7EA))
            )
        )
        GalleryBackground.COLORFUL -> Modifier.background(
            Brush.linearGradient(
                colors = listOf(Color(0xFF5E17EB), Color(0xFFFF416C), Color(0xFFFF4B2B)),
                start = Offset(0f, 0f),
                end = Offset(800f, 1600f)
            )
        )
        GalleryBackground.HIGH_DETAIL -> Modifier.background(
            Brush.sweepGradient(
                colors = listOf(Color(0xFF1F1C2C), Color(0xFF928DAB), Color(0xFF1F1C2C))
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(bgModifier)
            .testTag("aura_material_gallery")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = AuraSpacing.md, vertical = AuraSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(AuraSpacing.md)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AuraGlassPill(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AuraColors.AuraWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(AuraSpacing.xs))
                        Text(text = "BACK TO OS", style = AuraTypography.labelSmall, color = AuraColors.AuraWhite)
                    }

                    AuraGlassButton(
                        onClick = { isInspectorVisible = true },
                        shape = AuraShapes.pill
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Inspector",
                            tint = AuraColors.AuraWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(AuraSpacing.xs))
                        Text(text = "INSPECTOR", style = AuraTypography.labelSmall, color = AuraColors.AuraWhite)
                    }
                }
            }

            // Title
            item {
                Column {
                    Text(
                        text = "LIQUID GLASS GALLERY",
                        style = AuraTypography.headlineLarge,
                        color = AuraColors.AuraWhite
                    )
                    Text(
                        text = "Real-time verification of all 9 glass components under dynamic environments",
                        style = AuraTypography.bodyMedium,
                        color = AuraColors.Silver
                    )
                }
            }

            // Background Switcher
            item {
                Column {
                    Text(text = "TEST ENVIRONMENT BACKDROP", style = AuraTypography.labelSmall, color = AuraColors.Gray)
                    Spacer(modifier = Modifier.height(AuraSpacing.xs))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
                    ) {
                        items(GalleryBackground.entries) { bg ->
                            val isSelected = selectedBg == bg
                            AuraGlassButton(
                                onClick = { selectedBg = bg },
                                shape = AuraShapes.roundedSm
                            ) {
                                Text(
                                    text = bg.title,
                                    style = AuraTypography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
                                )
                            }
                        }
                    }
                }
            }

            // Performance Profile Switcher
            item {
                Column {
                    Text(text = "PERFORMANCE PROFILE", style = AuraTypography.labelSmall, color = AuraColors.Gray)
                    Spacer(modifier = Modifier.height(AuraSpacing.xs))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
                    ) {
                        AuraPerformanceProfile.entries.forEach { profile ->
                            val isSelected = currentProfile == profile
                            AuraGlassButton(
                                onClick = {
                                    onProfileChange(profile)
                                    liveConfig = AuraMaterialConfig.forProfileAndType(profile, selectedMaterialType)
                                },
                                modifier = Modifier.weight(1f),
                                shape = AuraShapes.roundedSm
                            ) {
                                Text(
                                    text = profile.name,
                                    style = AuraTypography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
                                )
                            }
                        }
                    }
                }
            }

            // Material Type Selector
            item {
                Column {
                    Text(text = "GLASS MATERIAL TYPE", style = AuraTypography.labelSmall, color = AuraColors.Gray)
                    Spacer(modifier = Modifier.height(AuraSpacing.xs))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
                    ) {
                        items(AuraGlassMaterialType.entries) { matType ->
                            val isSelected = selectedMaterialType == matType
                            AuraGlassButton(
                                onClick = {
                                    selectedMaterialType = matType
                                    liveConfig = AuraMaterialConfig.forProfileAndType(currentProfile, matType)
                                },
                                shape = AuraShapes.roundedSm
                            ) {
                                Text(
                                    text = matType.title,
                                    style = AuraTypography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
                                )
                            }
                        }
                    }
                }
            }

            // 1. AuraGlassSurface Showcase
            item {
                SectionHeader("1. AURAGLASSSURFACE (PRIMITIVE)")
                AuraGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AuraShapes.roundedLg,
                    materialType = selectedMaterialType,
                    depth = selectedDepth,
                    customConfig = liveConfig
                ) {
                    Column(modifier = Modifier.padding(AuraSpacing.md)) {
                        Text(
                            text = "AURA Liquid Substrate",
                            style = AuraTypography.titleLarge,
                            color = AuraColors.AuraWhite
                        )
                        Spacer(modifier = Modifier.height(AuraSpacing.xxs))
                        Text(
                            text = "Active type: ${selectedMaterialType.title} • Blur: ${liveConfig.blurRadiusDp.toInt()}dp • Transparency: ${(liveConfig.surfaceAlpha * 100).toInt()}%",
                            style = AuraTypography.bodyMedium,
                            color = AuraColors.Silver
                        )
                    }
                }
            }

            // 2. AuraGlassCard Showcase
            item {
                SectionHeader("2. AURAGLASSCARD (INTERACTIVE TOUCH)")
                AuraGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    materialType = selectedMaterialType,
                    depth = AuraDepth.LEVEL_2
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Interactive Spring Card", style = AuraTypography.titleLarge, color = AuraColors.AuraWhite)
                            Text(text = "Tap to feel physical spring compression", style = AuraTypography.bodyMedium, color = AuraColors.Silver)
                        }
                        Icon(
                            imageVector = Icons.Default.NorthEast,
                            contentDescription = "Card Action",
                            tint = AuraColors.AuraWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 3. AuraGlassButton Showcase
            item {
                SectionHeader("3. AURAGLASSBUTTON (STATES & HAPTICS)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
                ) {
                    AuraGlassButton(
                        text = "Standard",
                        icon = Icons.Default.Bolt,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        materialType = selectedMaterialType
                    )
                    AuraGlassButton(
                        text = "Loading",
                        isLoading = true,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        materialType = selectedMaterialType
                    )
                    AuraGlassButton(
                        text = "Disabled",
                        enabled = false,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        materialType = selectedMaterialType
                    )
                }
            }

            // 4. AuraGlassPill Showcase
            item {
                SectionHeader("4. AURAGLASSPILL (STATUS CAPSULES)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.sm)
                ) {
                    AuraGlassPill(materialType = selectedMaterialType) {
                        Text(text = "LIVE", style = AuraTypography.labelSmall, color = AuraColors.AuraWhite)
                    }
                    AuraGlassPill(materialType = selectedMaterialType) {
                        Icon(imageVector = Icons.Default.Circle, contentDescription = null, modifier = Modifier.size(10.dp), tint = AuraColors.AuraWhite)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ORB ENGINE", style = AuraTypography.labelSmall, color = AuraColors.Silver)
                    }
                    AuraGlassPill(materialType = selectedMaterialType) {
                        Text(text = "120 HZ", style = AuraTypography.monoMetric, color = AuraColors.AuraWhite)
                    }
                }
            }

            // 5 & 6. AuraGlassSheet & AuraGlassDialog Triggers
            item {
                SectionHeader("5 & 6. GLASS SHEET & GLASS DIALOG")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.sm)
                ) {
                    AuraGlassButton(
                        text = "Open Sheet",
                        onClick = { isSheetVisible = true },
                        modifier = Modifier.weight(1f),
                        materialType = selectedMaterialType
                    )
                    AuraGlassButton(
                        text = "Open Dialog",
                        onClick = { isDialogVisible = true },
                        modifier = Modifier.weight(1f),
                        materialType = selectedMaterialType
                    )
                }
            }

            // 7. AuraGlassIcon Showcase
            item {
                SectionHeader("7. AURAGLASSICON (LIGHTING & SHAPES)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AuraGlassIcon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Widgets",
                        materialType = selectedMaterialType,
                        onClick = {}
                    )
                    AuraGlassIcon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Energy",
                        materialType = selectedMaterialType,
                        shape = AuraShapes.pill,
                        onClick = {}
                    )
                    AuraGlassIcon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        materialType = selectedMaterialType,
                        depth = AuraDepth.LEVEL_3,
                        onClick = {}
                    )
                    AuraGlassIcon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Disabled Icon",
                        enabled = false,
                        materialType = selectedMaterialType
                    )
                }
            }

            // 8. AuraGlassSlider Showcase
            item {
                SectionHeader("8. AURAGLASSSLIDER (LIQUID KNOB)")
                AuraGlassSlider(
                    label = "Tactile Liquid Slider",
                    value = sliderValue,
                    valueRange = 0f..1f,
                    valueDisplay = "${(sliderValue * 100).toInt()}%",
                    onValueChange = { sliderValue = it },
                    materialType = selectedMaterialType
                )
            }

            // 9. AuraGlassNavigation Showcase
            item {
                SectionHeader("9. AURAGLASSNAVIGATION (SEGMENTED DOCK)")
                val navItems = remember {
                    listOf(
                        AuraNavItem("home", "HOME", Icons.Default.Circle),
                        AuraNavItem("river", "RIVER", Icons.Default.Layers),
                        AuraNavItem("orb", "ORB", Icons.Default.Widgets)
                    )
                }
                AuraGlassNavigation(
                    items = navItems,
                    selectedId = selectedNavId,
                    onItemSelected = { selectedNavId = it.id },
                    materialType = selectedMaterialType
                )
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Draggable Bottom Sheet Overlay
        AnimatedVisibility(
            visible = isSheetVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AuraGlassSheet(
                onDismissRequest = { isSheetVisible = false },
                materialType = selectedMaterialType
            ) {
                Text(text = "AURAGLASSSHEET ACTIVE", style = AuraTypography.headlineMedium, color = AuraColors.AuraWhite)
                Spacer(modifier = Modifier.height(AuraSpacing.xs))
                Text(
                    text = "Drag downward to dismiss with physics spring-back.",
                    style = AuraTypography.bodyMedium,
                    color = AuraColors.Silver
                )
                Spacer(modifier = Modifier.height(AuraSpacing.lg))
                AuraGlassButton(
                    text = "Close Sheet",
                    onClick = { isSheetVisible = false },
                    modifier = Modifier.fillMaxWidth(),
                    materialType = selectedMaterialType
                )
                Spacer(modifier = Modifier.height(AuraSpacing.lg))
            }
        }

        // Glass Dialog Modal
        if (isDialogVisible) {
            AuraGlassDialog(
                onDismissRequest = { isDialogVisible = false },
                materialType = selectedMaterialType
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "AURAGLASSDIALOG", style = AuraTypography.headlineMedium, color = AuraColors.AuraWhite)
                    Spacer(modifier = Modifier.height(AuraSpacing.sm))
                    Text(
                        text = "Physical glass modal with atmospheric depth and dynamic contrast protection.",
                        style = AuraTypography.bodyMedium,
                        color = AuraColors.Silver
                    )
                    Spacer(modifier = Modifier.height(AuraSpacing.md))
                    AuraGlassButton(
                        text = "Dismiss Dialog",
                        onClick = { isDialogVisible = false },
                        materialType = selectedMaterialType
                    )
                }
            }
        }

        // Material Inspector Overlay
        AnimatedVisibility(
            visible = isInspectorVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            AuraMaterialInspector(
                config = liveConfig,
                onConfigChange = { liveConfig = it },
                currentProfile = currentProfile,
                onProfileChange = {
                    onProfileChange(it)
                    liveConfig = AuraMaterialConfig.forProfileAndType(it, selectedMaterialType)
                },
                onDismiss = { isInspectorVisible = false }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = AuraTypography.labelSmall,
        color = AuraColors.Gray,
        modifier = Modifier.padding(top = AuraSpacing.sm, bottom = AuraSpacing.xxs)
    )
}
