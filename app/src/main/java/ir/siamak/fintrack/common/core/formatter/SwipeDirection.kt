package ir.siamak.fintrack.common.core.formatter

import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** Material3 1.3 exposes physical drag direction for backgrounds and logical anchors for actions. */
@Composable
fun swipeActionDirection(physical: SwipeToDismissBoxValue): SwipeToDismissBoxValue =
    if(LocalLayoutDirection.current != LayoutDirection.Rtl) physical else when(physical) {
        SwipeToDismissBoxValue.StartToEnd -> SwipeToDismissBoxValue.EndToStart
        SwipeToDismissBoxValue.EndToStart -> SwipeToDismissBoxValue.StartToEnd
        SwipeToDismissBoxValue.Settled -> SwipeToDismissBoxValue.Settled
    }
