package com.example.gymtracker.ui.foldable

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo

data class FoldableState(
    val isFolded: Boolean = false,
    val foldOrientation: FoldingFeature.Orientation? = null,
    val foldState: FoldingFeature.State? = null,
    val isTabletopMode: Boolean = false,
    val isBookMode: Boolean = false,
    val isSeparating: Boolean = false,
    val isFlat: Boolean = false // Полностью разложено
)

@Composable
fun rememberFoldableState(): State<FoldableState> {
    val context = LocalContext.current
    val windowInfoTracker = remember { WindowInfoTracker.getOrCreate(context) }
    
    val windowLayoutInfo by windowInfoTracker.windowLayoutInfo
        .collectAsStateWithLifecycle(
            initialValue = WindowLayoutInfo(emptyList())
        )

    val foldableState = remember(windowLayoutInfo) {
        val foldingFeature = windowLayoutInfo.displayFeatures
            .filterIsInstance<FoldingFeature>()
            .firstOrNull()

        val isFolded = foldingFeature?.state == FoldingFeature.State.FOLDED
        val foldOrientation = foldingFeature?.orientation
        val foldState = foldingFeature?.state
        val isSeparating = foldingFeature?.isSeparating ?: false
        
        // Определяем режимы для Galaxy Fold 6
        // Book mode: вертикальная складка, разложено (вертикально)
        val isBookMode = foldOrientation == FoldingFeature.Orientation.VERTICAL && 
                        foldState == FoldingFeature.State.HALF_OPENED &&
                        isSeparating
        
        // Tabletop mode: горизонтальная складка, разложено (горизонтально)
        val isTabletopMode = foldOrientation == FoldingFeature.Orientation.HORIZONTAL && 
                            foldState == FoldingFeature.State.HALF_OPENED &&
                            isSeparating
        
        // Flat mode: полностью разложено (нет складки или складка полностью открыта)
        // Учитываем только если не в режиме book или tabletop
        val isFlat = (foldingFeature == null || 
                     foldState == FoldingFeature.State.FLAT) && 
                     !isBookMode && !isTabletopMode

        FoldableState(
            isFolded = isFolded,
            foldOrientation = foldOrientation,
            foldState = foldState,
            isTabletopMode = isTabletopMode,
            isBookMode = isBookMode,
            isSeparating = isSeparating,
            isFlat = isFlat
        )
    }

    return rememberUpdatedState(foldableState)
}
