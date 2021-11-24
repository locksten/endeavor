package com.example.endeavor.ui.theme

import androidx.compose.ui.graphics.Color

@Suppress("unused")
class MyColors(isDarkTheme: Boolean) {
    val primary = if (isDarkTheme) MyRawColors.gray700 else MyRawColors.teal500
    val onPrimary = if (isDarkTheme) MyRawColors.gray50 else MyRawColors.gray50

    val secondary = if (isDarkTheme) MyRawColors.gray700 else MyRawColors.teal500
    val onSecondary = if (isDarkTheme) MyRawColors.gray50 else MyRawColors.gray50

    val background = if (isDarkTheme) MyRawColors.gray900 else MyRawColors.gray50
    val onBackground = if (isDarkTheme) MyRawColors.gray300 else MyRawColors.gray900

    val surface = if (isDarkTheme) MyRawColors.gray800 else MyRawColors.teal400
    val onSurface = if (isDarkTheme) MyRawColors.gray200 else MyRawColors.gray900

    val danger = if (isDarkTheme) MyRawColors.red800 else MyRawColors.red600
    val onDanger = if (isDarkTheme) MyRawColors.red100 else MyRawColors.red100

    val positiveHabitButton =
        if (isDarkTheme) MyRawColors.green900.copy(alpha = 0.07f) else MyRawColors.green900.copy(
            alpha = 0.1f
        )
    val onPositiveHabitButton = if (isDarkTheme) MyRawColors.green100 else MyRawColors.green900

    val negativeHabitButton =
        if (isDarkTheme) MyRawColors.red900.copy(alpha = 0.07f) else MyRawColors.red900.copy(
            alpha = 0.1f
        )
    val onNegativeHabitButton = if (isDarkTheme) MyRawColors.red100 else MyRawColors.red900

    val hitpoints = if (isDarkTheme) MyRawColors.red700 else MyRawColors.red400
    val faintHitpoints =
        if (isDarkTheme) MyRawColors.red500.copy(alpha = 0.1f) else MyRawColors.red500.copy(alpha = 0.1f)

    val energy = if (isDarkTheme) MyRawColors.yellow700 else MyRawColors.yellow400
    val faintEnergy =
        if (isDarkTheme) MyRawColors.yellow500.copy(alpha = 0.1f) else MyRawColors.yellow500.copy(
            alpha = 0.1f
        )

    val experience = if (isDarkTheme) MyRawColors.purple700 else MyRawColors.purple400
    val faintExperience =
        if (isDarkTheme) MyRawColors.purple500.copy(alpha = 0.1f) else MyRawColors.purple500.copy(
            alpha = 0.1f
        )

    val difficultyButton = if (isDarkTheme) MyRawColors.gray700 else MyRawColors.teal500
    val difficultyButtonActive = if (isDarkTheme) MyRawColors.gray500 else MyRawColors.teal700
    val onDifficultyButton = if (isDarkTheme) MyRawColors.gray50 else MyRawColors.gray50

    val graySurface = if (isDarkTheme) MyRawColors.gray800 else MyRawColors.gray200
    val onGraySurface = if (isDarkTheme) MyRawColors.gray50 else MyRawColors.gray900
}

@Suppress("unused")
object MyRawColors {
    val blueGray50 = Color(0xFFF8FAFC)
    val blueGray100 = Color(0xFFF1F5F9)
    val blueGray200 = Color(0xFFE2E8F0)
    val blueGray300 = Color(0xFFCBD5E1)
    val blueGray400 = Color(0xFF94A3B8)
    val blueGray500 = Color(0xFF64748B)
    val blueGray600 = Color(0xFF475569)
    val blueGray700 = Color(0xFF334155)
    val blueGray800 = Color(0xFF1E293B)
    val blueGray900 = Color(0xFF0F172A)

    val coolGray50 = Color(0xFFF9FAFB)
    val coolGray100 = Color(0xFFF3F4F6)
    val coolGray200 = Color(0xFFE5E7EB)
    val coolGray300 = Color(0xFFD1D5DB)
    val coolGray400 = Color(0xFF9CA3AF)
    val coolGray500 = Color(0xFF6B7280)
    val coolGray600 = Color(0xFF4B5563)
    val coolGray700 = Color(0xFF374151)
    val coolGray800 = Color(0xFF1F2937)
    val coolGray900 = Color(0xFF111827)

    val gray50 = Color(0xFFFAFAFA)
    val gray100 = Color(0xFFF4F4F5)
    val gray200 = Color(0xFFE4E4E7)
    val gray300 = Color(0xFFD4D4D8)
    val gray400 = Color(0xFFA1A1AA)
    val gray500 = Color(0xFF71717A)
    val gray600 = Color(0xFF52525B)
    val gray700 = Color(0xFF3F3F46)
    val gray800 = Color(0xFF27272A)
    val gray900 = Color(0xFF18181B)

    val trueGray50 = Color(0xFFFAFAFA)
    val trueGray100 = Color(0xFFF5F5F5)
    val trueGray200 = Color(0xFFE5E5E5)
    val trueGray300 = Color(0xFFD4D4D4)
    val trueGray400 = Color(0xFFA3A3A3)
    val trueGray500 = Color(0xFF737373)
    val trueGray600 = Color(0xFF525252)
    val trueGray700 = Color(0xFF404040)
    val trueGray800 = Color(0xFF262626)
    val trueGray900 = Color(0xFF171717)

    val warmGray50 = Color(0xFFFAFAF9)
    val warmGray100 = Color(0xFFF5F5F4)
    val warmGray200 = Color(0xFFE7E5E4)
    val warmGray300 = Color(0xFFD6D3D1)
    val warmGray400 = Color(0xFFA8A29E)
    val warmGray500 = Color(0xFF78716C)
    val warmGray600 = Color(0xFF57534E)
    val warmGray700 = Color(0xFF44403C)
    val warmGray800 = Color(0xFF292524)
    val warmGray900 = Color(0xFF1C1917)

    val red50 = Color(0xFFFEF2F2)
    val red100 = Color(0xFFFEE2E2)
    val red200 = Color(0xFFFECACA)
    val red300 = Color(0xFFFCA5A5)
    val red400 = Color(0xFFF87171)
    val red500 = Color(0xFFEF4444)
    val red600 = Color(0xFFDC2626)
    val red700 = Color(0xFFB91C1C)
    val red800 = Color(0xFF991B1B)
    val red900 = Color(0xFF7F1D1D)

    val orange50 = Color(0xFFFFF7ED)
    val orange100 = Color(0xFFFFEDD5)
    val orange200 = Color(0xFFFED7AA)
    val orange300 = Color(0xFFFDBA74)
    val orange400 = Color(0xFFFB923C)
    val orange500 = Color(0xFFF97316)
    val orange600 = Color(0xFFEA580C)
    val orange700 = Color(0xFFC2410C)
    val orange800 = Color(0xFF9A3412)
    val orange900 = Color(0xFF7C2D12)

    val amber50 = Color(0xFFFFFBEB)
    val amber100 = Color(0xFFFEF3C7)
    val amber200 = Color(0xFFFDE68A)
    val amber300 = Color(0xFFFCD34D)
    val amber400 = Color(0xFFFBBF24)
    val amber500 = Color(0xFFF59E0B)
    val amber600 = Color(0xFFD97706)
    val amber700 = Color(0xFFB45309)
    val amber800 = Color(0xFF92400E)
    val amber900 = Color(0xFF78350F)

    val yellow50 = Color(0xFFFEFCE8)
    val yellow100 = Color(0xFFFEF9C3)
    val yellow200 = Color(0xFFFEF08A)
    val yellow300 = Color(0xFFFDE047)
    val yellow400 = Color(0xFFFACC15)
    val yellow500 = Color(0xFFEAB308)
    val yellow600 = Color(0xFFCA8A04)
    val yellow700 = Color(0xFFA16207)
    val yellow800 = Color(0xFF854D0E)
    val yellow900 = Color(0xFF713F12)

    val lime50 = Color(0xFFF7FEE7)
    val lime100 = Color(0xFFECFCCB)
    val lime200 = Color(0xFFD9F99D)
    val lime300 = Color(0xFFBEF264)
    val lime400 = Color(0xFFA3E635)
    val lime500 = Color(0xFF84CC16)
    val lime600 = Color(0xFF65A30D)
    val lime700 = Color(0xFF4D7C0F)
    val lime800 = Color(0xFF3F6212)
    val lime900 = Color(0xFF365314)

    val green50 = Color(0xFFF0FDF4)
    val green100 = Color(0xFFDCFCE7)
    val green200 = Color(0xFFBBF7D0)
    val green300 = Color(0xFF86EFAC)
    val green400 = Color(0xFF4ADE80)
    val green500 = Color(0xFF22C55E)
    val green600 = Color(0xFF16A34A)
    val green700 = Color(0xFF15803D)
    val green800 = Color(0xFF166534)
    val green900 = Color(0xFF14532D)

    val emerald50 = Color(0xFFECFDF5)
    val emerald100 = Color(0xFFD1FAE5)
    val emerald200 = Color(0xFFA7F3D0)
    val emerald300 = Color(0xFF6EE7B7)
    val emerald400 = Color(0xFF34D399)
    val emerald500 = Color(0xFF10B981)
    val emerald600 = Color(0xFF059669)
    val emerald700 = Color(0xFF047857)
    val emerald800 = Color(0xFF065F46)
    val emerald900 = Color(0xFF064E3B)

    val teal50 = Color(0xFFF0FDFA)
    val teal100 = Color(0xFFCCFBF1)
    val teal200 = Color(0xFF99F6E4)
    val teal300 = Color(0xFF5EEAD4)
    val teal400 = Color(0xFF2DD4BF)
    val teal500 = Color(0xFF14B8A6)
    val teal600 = Color(0xFF0D9488)
    val teal700 = Color(0xFF0F766E)
    val teal800 = Color(0xFF115E59)
    val teal900 = Color(0xFF134E4A)

    val cyan50 = Color(0xFFECFEFF)
    val cyan100 = Color(0xFFCFFAFE)
    val cyan200 = Color(0xFFA5F3FC)
    val cyan300 = Color(0xFF67E8F9)
    val cyan400 = Color(0xFF22D3EE)
    val cyan500 = Color(0xFF06B6D4)
    val cyan600 = Color(0xFF0891B2)
    val cyan700 = Color(0xFF0E7490)
    val cyan800 = Color(0xFF155E75)
    val cyan900 = Color(0xFF164E63)

    val sky50 = Color(0xFFF0F9FF)
    val sky100 = Color(0xFFE0F2FE)
    val sky200 = Color(0xFFBAE6FD)
    val sky300 = Color(0xFF7DD3FC)
    val sky400 = Color(0xFF38BDF8)
    val sky500 = Color(0xFF0EA5E9)
    val sky600 = Color(0xFF0284C7)
    val sky700 = Color(0xFF0369A1)
    val sky800 = Color(0xFF075985)
    val sky900 = Color(0xFF0C4A6E)

    val blue50 = Color(0xFFEFF6FF)
    val blue100 = Color(0xFFDBEAFE)
    val blue200 = Color(0xFFBFDBFE)
    val blue300 = Color(0xFF93C5FD)
    val blue400 = Color(0xFF60A5FA)
    val blue500 = Color(0xFF3B82F6)
    val blue600 = Color(0xFF2563EB)
    val blue700 = Color(0xFF1D4ED8)
    val blue800 = Color(0xFF1E40AF)
    val blue900 = Color(0xFF1E3A8A)

    val indigo50 = Color(0xFFEEF2FF)
    val indigo100 = Color(0xFFE0E7FF)
    val indigo200 = Color(0xFFC7D2FE)
    val indigo300 = Color(0xFFA5B4FC)
    val indigo400 = Color(0xFF818CF8)
    val indigo500 = Color(0xFF6366F1)
    val indigo600 = Color(0xFF4F46E5)
    val indigo700 = Color(0xFF4338CA)
    val indigo800 = Color(0xFF3730A3)
    val indigo900 = Color(0xFF312E81)

    val violet50 = Color(0xFFF5F3FF)
    val violet100 = Color(0xFFEDE9FE)
    val violet200 = Color(0xFFDDD6FE)
    val violet300 = Color(0xFFC4B5FD)
    val violet400 = Color(0xFFA78BFA)
    val violet500 = Color(0xFF8B5CF6)
    val violet600 = Color(0xFF7C3AED)
    val violet700 = Color(0xFF6D28D9)
    val violet800 = Color(0xFF5B21B6)
    val violet900 = Color(0xFF4C1D95)

    val purple50 = Color(0xFFFAF5FF)
    val purple100 = Color(0xFFF3E8FF)
    val purple200 = Color(0xFFE9D5FF)
    val purple300 = Color(0xFFD8B4FE)
    val purple400 = Color(0xFFC084FC)
    val purple500 = Color(0xFFA855F7)
    val purple600 = Color(0xFF9333EA)
    val purple700 = Color(0xFF7E22CE)
    val purple800 = Color(0xFF6B21A8)
    val purple900 = Color(0xFF581C87)

    val fuchsia50 = Color(0xFFFDF4FF)
    val fuchsia100 = Color(0xFFFAE8FF)
    val fuchsia200 = Color(0xFFF5D0FE)
    val fuchsia300 = Color(0xFFF0ABFC)
    val fuchsia400 = Color(0xFFE879F9)
    val fuchsia500 = Color(0xFFD946EF)
    val fuchsia600 = Color(0xFFC026D3)
    val fuchsia700 = Color(0xFFA21CAF)
    val fuchsia800 = Color(0xFF86198F)
    val fuchsia900 = Color(0xFF701A75)

    val pink50 = Color(0xFFFDF2F8)
    val pink100 = Color(0xFFFCE7F3)
    val pink200 = Color(0xFFFBCFE8)
    val pink300 = Color(0xFFF9A8D4)
    val pink400 = Color(0xFFF472B6)
    val pink500 = Color(0xFFEC4899)
    val pink600 = Color(0xFFDB2777)
    val pink700 = Color(0xFFBE185D)
    val pink800 = Color(0xFF9D174D)
    val pink900 = Color(0xFF831843)

    val rose50 = Color(0xFFFFF1F2)
    val rose100 = Color(0xFFFFE4E6)
    val rose200 = Color(0xFFFECDD3)
    val rose300 = Color(0xFFFDA4AF)
    val rose400 = Color(0xFFFB7185)
    val rose500 = Color(0xFFF43F5E)
    val rose600 = Color(0xFFE11D48)
    val rose700 = Color(0xFFBE123C)
    val rose800 = Color(0xFF9F1239)
    val rose900 = Color(0xFF881337)
}