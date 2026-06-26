package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.presentation.dashboard.components.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.items.MemberItem

/**
 * بخش نمایش اعضای خانواده.
 */
@Composable
fun MemberSection(
    members: List<Member>
) {
    Column {
        SectionHeader(title = "اعضای خانواده")

        if (members.isEmpty()) {
            EmptySectionText("عضوی ثبت نشده است.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(members) { member ->
                    MemberItem(member = member)
                }
            }
        }
    }
}

