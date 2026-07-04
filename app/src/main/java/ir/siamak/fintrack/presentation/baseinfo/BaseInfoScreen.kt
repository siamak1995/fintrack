package ir.siamak.fintrack.presentation.baseinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTTopBar

data class BaseInfoMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun BaseInfoScreen(
    onWalletClick: () -> Unit,
    onMemberClick: () -> Unit,
    //@TODO - phase-2
//    onInstallmentClick: () -> Unit,
    onTagClick: () -> Unit
) {
    val items = listOf(
        BaseInfoMenuItem("حساب‌ها", Icons.Default.AccountBalanceWallet, onWalletClick),
        BaseInfoMenuItem("اعضا", Icons.Default.People, onMemberClick),
        //@TODO - phase-2
//        BaseInfoMenuItem("اقساط", Icons.Default.ReceiptLong, onInstallmentClick),
        BaseInfoMenuItem("تگ‌ها", Icons.Default.Label, onTagClick)
    )

    Scaffold(
        topBar = {
            FTTopBar(
                title = "اطلاعات اولیه",
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ListItem(
                        headlineContent = { Text(item.title) },
                        leadingContent = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        modifier = Modifier.clickable { item.onClick() }
                    )
                }
            }
        }
    }
}
