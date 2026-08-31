package feelings.guide.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ─────────────────────────────────────────────────────────────────────────────
   Nocturne — palette
   Generated in OKLCH on one shared lightness scale: the same step of any ramp
   carries the same visual weight. Mono scheme: there is one accent (blurple
   #9184D9); accent-2 is a near-identical stand-in kept so both sets resolve.
   Do not flood large areas with the accent — it is a line, a tint and a mark.
   No pure black or pure white anywhere; every value comes from a ramp.
   ───────────────────────────────────────────────────────────────────────── */

// Accent ramp
val Accent        = Color(0xFF9184D9)
val Accent100     = Color(0xFFF5F4FF)
val Accent200     = Color(0xFFE7E5FE)
val Accent300     = Color(0xFFD2CEFD)
val Accent400     = Color(0xFFB5ABFC)
val Accent500     = Color(0xFF968AE0)
val Accent600     = Color(0xFF796CBF)
val Accent700     = Color(0xFF5D5294)
val Accent800     = Color(0xFF423A6A)
val Accent900     = Color(0xFF2B2741)

// Neutral ramp
val Neutral100    = Color(0xFFF3F5FE)
val Neutral200    = Color(0xFFE4E7F5)
val Neutral300    = Color(0xFFCFD3E5)
val Neutral400    = Color(0xFFB2B6CA)
val Neutral500    = Color(0xFF9397AB)
val Neutral600    = Color(0xFF75798C)
val Neutral700    = Color(0xFF595D6C)
val Neutral800    = Color(0xFF3F424D)
val Neutral900    = Color(0xFF292B31)

// Grounds
val NocturneBg    = Color(0xFF161826)
val NocturneSurf  = Color(0xFF232532)
val NocturneText  = Color(0xFFE9E9ED)

// Destructive — Nocturne has no error role, so this is the accent's own
// lightness and chroma at a rotated hue: present without shouting.
val DangerDark    = Color(0xFFD99184)
val DangerLight   = Color(0xFF8C4A3F)

val Scrim         = Color(0xFF0A0B12)

/* ─────────────────────────────────────────────────────────────────────────────
   Color schemes
   ───────────────────────────────────────────────────────────────────────── */

val NocturneDarkColors = darkColorScheme(
    primary                     = Accent,
    onPrimary                   = Color(0xFF1B1633),
    primaryContainer            = Accent900,
    onPrimaryContainer          = Accent200,
    inversePrimary              = Accent700,

    secondary                   = Accent400,
    onSecondary                 = Color(0xFF2B293A),
    secondaryContainer          = Accent900,
    onSecondaryContainer        = Accent200,

    tertiary                    = Accent400,
    onTertiary                  = Color(0xFF2B293A),
    tertiaryContainer           = Accent900,
    onTertiaryContainer         = Accent200,

    background                  = NocturneBg,
    onBackground                = NocturneText,

    surface                     = NocturneBg,
    onSurface                   = NocturneText,
    surfaceVariant              = Neutral900,
    onSurfaceVariant            = Neutral400,
    surfaceTint                 = Accent,

    surfaceContainerLowest      = Color(0xFF121320),
    surfaceContainerLow         = Color(0xFF1C1E2B),
    surfaceContainer            = NocturneSurf,
    surfaceContainerHigh        = Neutral900,
    surfaceContainerHighest     = Color(0xFF2F323A),

    outline                     = Neutral600,
    outlineVariant              = Neutral800,

    error                       = DangerDark,
    onError                     = Color(0xFF2B1A16),
    errorContainer              = Color(0xFF3A2B2B),
    onErrorContainer            = Color(0xFFF5D9D3),

    inverseSurface              = Neutral200,
    inverseOnSurface            = Neutral900,
    scrim                       = Scrim,
)

val NocturneLightColors = lightColorScheme(
    primary                     = Accent700,
    onPrimary                   = Accent100,
    primaryContainer            = Accent200,
    onPrimaryContainer          = Accent800,
    inversePrimary              = Accent400,

    secondary                   = Color(0xFF5C5783),
    onSecondary                 = Accent100,
    secondaryContainer          = Accent200,
    onSecondaryContainer        = Color(0xFF423E5D),

    tertiary                    = Color(0xFF5C5783),
    onTertiary                  = Accent100,
    tertiaryContainer           = Accent200,
    onTertiaryContainer         = Color(0xFF423E5D),

    background                  = Neutral100,
    onBackground                = Neutral900,

    surface                     = Neutral100,
    onSurface                   = Neutral900,
    surfaceVariant              = Neutral200,
    onSurfaceVariant            = Neutral700,
    surfaceTint                 = Accent700,

    surfaceContainerLowest      = Color(0xFFF8F9FF),
    surfaceContainerLow         = Color(0xFFF0F2FC),
    surfaceContainer            = Color(0xFFE9ECF8),
    surfaceContainerHigh        = Neutral200,
    surfaceContainerHighest     = Color(0xFFDDE1F1),

    outline                     = Neutral600,
    outlineVariant              = Neutral300,

    error                       = DangerLight,
    onError                     = Accent100,
    errorContainer              = Color(0xFFF0E3E0),
    onErrorContainer            = Color(0xFF4A231C),

    inverseSurface              = Neutral900,
    inverseOnSurface            = Neutral100,
    scrim                       = Scrim,
)

/* ─────────────────────────────────────────────────────────────────────────────
   Type — Inter for headings over Inter for body. Headings never go past
   weight 500: hierarchy here is size and space, not weight.
   Falls back to the platform default family: no bundled font files and no
   downloadable-fonts provider dependency, so this renders correctly even
   without Google Play services present.
   ───────────────────────────────────────────────────────────────────────── */

val Inter = FontFamily.Default

private fun heading(size: Int, line: Int, tracking: Double) = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.Medium,
    fontSize = size.sp, lineHeight = line.sp, letterSpacing = tracking.sp,
)

private fun body(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = Inter, fontWeight = weight,
    fontSize = size.sp, lineHeight = line.sp,
)

val NocturneTypography = Typography(
    displaySmall  = heading(34, 38, (-0.6)),   // screen-level display
    headlineSmall = heading(22, 28, (-0.3)),   // answer screen question
    titleLarge    = heading(20, 26, (-0.2)),   // top app bar
    titleMedium   = heading(17, 23, (-0.2)),   // question card title
    titleSmall    = heading(15, 20, 0.0),      // feelings group row
    bodyLarge     = body(15, 23),              // answers, fields
    bodyMedium    = body(13, 20),              // supporting copy
    bodySmall     = body(12, 17),              // meta lines
    labelLarge    = heading(14, 18, 0.0),      // buttons
    labelMedium   = body(12, 16, FontWeight.Medium),
    labelSmall    = TextStyle(                 // the .card-kicker: 10sp, tracked, uppercase
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp, lineHeight = 13.sp, letterSpacing = 1.0.sp,
    ),
)

/* ─────────────────────────────────────────────────────────────────────────────
   Shape — radius 8dp is the system's own step; 4 and 14 are its neighbours.
   ───────────────────────────────────────────────────────────────────────── */

val NocturneShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(4.dp),
    medium     = RoundedCornerShape(8.dp),
    large      = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(28.dp), // dialogs and bottom sheets
)

/* ─────────────────────────────────────────────────────────────────────────────
   Theme entry point. No dynamic color: Nocturne is the brand, and wallpaper
   extraction would replace the one accent the system is built around.
   ───────────────────────────────────────────────────────────────────────── */

@Composable
fun FeelingsGuideTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) NocturneDarkColors else NocturneLightColors,
        typography = NocturneTypography,
        shapes = NocturneShapes,
        content = content,
    )
}

/* ─────────────────────────────────────────────────────────────────────────────
   Usage notes, so the scheme is not undone downstream
   ─────────────────────────────────────────────────────────────────────────
   · Primary actions are OUTLINED, not filled: OutlinedButton for Save, the
     sheet CTA and Restore; TextButton for Cancel. Reserve filled Button for
     nothing in this app.
   · The FAB is a SmallFloatingActionButton with containerColor =
     primaryContainer, contentColor = onPrimaryContainer, shape = large —
     a tinted square, not an accent-flooded circle.
   · Cards are surfaceContainer with no elevation shadow; on the dark ground
     elevation is an edge, so prefer a 1dp outlineVariant border over
     stacking shadows.
   · Destructive text (Clear log, Delete) uses colorScheme.error on
     transparent; its container is errorContainer.
   · The theme setting maps to darkTheme: Dark -> true, Light -> false,
     System -> isSystemInDarkTheme() (computed by the caller, since the
     setting is stored outside Compose in DataStore).
   ───────────────────────────────────────────────────────────────────────── */
