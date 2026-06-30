package ir.siamak.fintrack.presentation.dashboard

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard

/**
 * هدر هر سکشن داشبورد.
 */
@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * متن خالی بودن هر سکشن.
 */
@Composable
fun EmptySectionText(text: String) {
    FTCard {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

/**
 * تبدیل امن رشته رنگ به Color.
 */
fun parseColorSafely(colorString: String): Color {
    return try {
        Color(parseColor(colorString))
    } catch (_: Exception) {
        Color(0xFF2563EB)
    }
}

fun iconForMember(iconName: String) = when (iconName) {
    "ic_child" -> Icons.Filled.Person
    "ic_spouse" -> Icons.Filled.Person
    "ic_parent" -> Icons.Filled.Person
    else -> Icons.Filled.Person
}

fun memberIconMapper(iconName: String): ImageVector {
    return when (iconName) {
        "ic_child" -> Icons.Default.Face
        "ic_spouse" -> Icons.Default.SupervisorAccount
        "ic_parent" -> Icons.Default.SupervisorAccount
        else -> Icons.Default.Person
    }
}
