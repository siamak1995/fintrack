package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardSpeedDial(

    onWallet: () -> Unit,

    onTransaction: () -> Unit,

    onMember: () -> Unit,

    onInstallment: () -> Unit

) {

    var expanded by remember {

        mutableStateOf(false)

    }

    Column(

        verticalArrangement = Arrangement.Bottom

    ) {

        AnimatedVisibility(

            visible = expanded,

            enter = fadeIn() + scaleIn(),

            exit = fadeOut() + scaleOut()

        ) {

            Column {

                FabItem(

                    "ثبت تراکنش",

                    Icons.Default.Payments

                ) {

                    expanded = false
                    onTransaction()

                }

                Spacer(Modifier.height(8.dp))

                FabItem(

                    "افزودن کیف پول",

                    Icons.Default.AccountBalanceWallet

                ) {

                    expanded = false
                    onWallet()

                }

                Spacer(Modifier.height(8.dp))

                FabItem(

                    "افزودن عضو",

                    Icons.Default.Groups

                ) {

                    expanded = false
                    onMember()

                }

                Spacer(Modifier.height(8.dp))

                FabItem(

                    "ثبت قسط",

                    Icons.Default.ReceiptLong

                ) {

                    expanded = false
                    onInstallment()

                }

                Spacer(Modifier.height(12.dp))

            }

        }

        FloatingActionButton(

            onClick = {

                expanded = !expanded

            }

        ) {

            Icon(

                Icons.Default.Add,

                null

            )

        }

    }

}

@Composable
private fun FabItem(

    title: String,

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    onClick: () -> Unit

) {

    ExtendedFloatingActionButton(

        onClick = onClick,

        icon = {

            Icon(

                icon,

                null

            )

        },

        text = {

            Text(title)

        }

    )

}
