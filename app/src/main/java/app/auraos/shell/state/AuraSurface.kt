package app.auraos.shell.state

/**
 * Conceptual primary surfaces in AURA OS continuous physical environment.
 */
enum class AuraSurface(
    val title: String,
    val description: String
) {
    /** Central home launcher workspace */
    HOME("Home", "Central Launcher Experience"),

    /** Fluid stream / contextual feeds (swiped from left) */
    RIVER("River", "Live Information Stream"),

    /** Spatial context & AI intelligence core (swiped from right) */
    ORB("Orb", "Context & Intelligence Hub"),

    /** Quick command & universal search palette (swiped down) */
    COMMAND("Command", "Universal Action Palette"),

    /** Multitasking spatial deck (future recents surface) */
    DECK("The Deck", "Spatial Multitasking"),

    /** Lock screen ambient boundary */
    LOCK_SCREEN("Lock Screen", "Secure Liquid Surface"),

    /** Liquid glass developer validation gallery */
    MATERIAL_GALLERY("Glass Gallery", "Interactive Liquid Glass Validation");

    val isAuxiliarySurface: Boolean
        get() = this != HOME
}
