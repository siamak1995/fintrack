package ir.siamak.fintrack.presentation.baseinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTTopBar

data class BaseInfoMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconContainerColor: Color,
    val iconColor: Color,
    val onClick: () -> Unit
)

@Composable
fun BaseInfoScreen(
    onWalletClick: () -> Unit,
    onMemberClick: () -> Unit,
    onTagClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val items = listOf(
        BaseInfoMenuItem(
            title = "حساب‌ها",
            subtitle = "مدیریت کیف پول‌ها و حساب‌های مالی",
            icon = Icons.Default.AccountBalanceWallet,
            iconContainerColor = colorScheme.primary.copy(alpha = 0.12f),
            iconColor = colorScheme.primary,
            onClick = onWalletClick
        ),
        BaseInfoMenuItem(
            title = "اعضا",
            subtitle = "ثبت و مدیریت اعضای خانواده",
            icon = Icons.Default.People,
            iconContainerColor = colorScheme.tertiary.copy(alpha = 0.14f),
            iconColor = colorScheme.tertiary,
            onClick = onMemberClick
        ),
        BaseInfoMenuItem(
            title = "تگ‌ها",
            subtitle = "دسته‌بندی بهتر تراکنش‌ها با برچسب‌ها",
            icon = Icons.Default.Label,
            iconContainerColor = colorScheme.secondary.copy(alpha = 0.14f),
            iconColor = colorScheme.secondary,
            onClick = onTagClick
        )
    )

    Scaffold(
        topBar = {
            FTTopBar(
                title = "اطلاعات اولیه",
                subtitle = "مدیریت اطلاعات پایه برنامه",
                actionIcon = Icons.Default.Settings,
                actionContentDescription = "تنظیمات",
                onActionClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BaseInfoHeaderCard()
            }

            items(items) { item ->
                BaseInfoMenuCard(item = item)
            }

            item {
                SettingsQuickCard(
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
private fun BaseInfoHeaderCard() {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.primary.copy(alpha = 0.75f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "مدیریت اطلاعات پایه",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary
                )

                Text(
                    text = "حساب‌ها، اعضا، تگ‌ها و تنظیمات برنامه را یک‌جا مدیریت کن.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun BaseInfoMenuCard(
    item: BaseInfoMenuItem
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = item.iconContainerColor,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsQuickCard(
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colorScheme.secondaryContainer,
                            colorScheme.tertiaryContainer
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = colorScheme.surface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "تنظیمات",
                        tint = colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.size(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "تنظیمات برنامه",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "شخصی‌سازی برنامه، گزینه‌های نمایش و تنظیمات عمومی",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
