//package ir.siamak.fintrack.presentation.dashboard.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import ir.siamak.fintrack.data.model.Member
//import ir.siamak.fintrack.presentation.components.FTCard
//import androidx.compose.foundation.layout.Row
//
//
//@Composable
//private fun MemberChip(member: Member) {
//    val color = parseColorSafely(member.color)
//    val icon = iconForMember(member.icon)
//
//    FTCard() {
//        Row(
//            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .background(color.copy(alpha = 0.15f), CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = member.name,
//                    tint = color
//                )
//            }
//
//            Column {
//                Text(text = member.name, style = MaterialTheme.typography.titleSmall)
//                Text(text = member.relation, style = MaterialTheme.typography.bodySmall)
//            }
//        }
//    }
//}