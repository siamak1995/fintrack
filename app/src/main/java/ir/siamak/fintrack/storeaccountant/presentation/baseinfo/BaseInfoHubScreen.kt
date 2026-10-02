package ir.siamak.fintrack.storeaccountant.presentation.baseinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseInfoHubScreen(
    onStoreInfoClick: () -> Unit,
    onSellerManagementClick: () -> Unit,
    onProductManagementClick: () -> Unit,
    onMaterialManagementClick: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("اطلاعات پایه") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                BaseInfoItem(
                    title = "اطلاعات فروشگاه",
                    icon = Icons.Default.Business,
                    onClick = onStoreInfoClick
                )
            }
            item {
                BaseInfoItem(
                    title = "مدیریت فروشندگان",
                    icon = Icons.Default.Person,
                    onClick = onSellerManagementClick
                )
            }
            item {
                BaseInfoItem(
                    title = "مدیریت کالاها",
                    icon = Icons.Default.ShoppingCart,
                    onClick = onProductManagementClick
                )
            }
            item {
                BaseInfoItem(
                    title = "مدیریت مواد اولیه",
                    icon = Icons.Default.Inventory,
                    onClick = onMaterialManagementClick
                )
            }
        }
    }
}

@Composable
private fun BaseInfoItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
