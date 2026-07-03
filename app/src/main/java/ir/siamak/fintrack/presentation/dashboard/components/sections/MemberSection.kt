package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.presentation.dashboard.components.MemberAvatar

@Composable
fun MemberSection(

    members: List<Member>,

    onMembersClick: () -> Unit

) {

    Column {

        Text(
            text = "اعضای خانواده",
            style = MaterialTheme.typography.titleLarge
        )

        if (members.isEmpty()) {

            TextButton(
                onClick = onMembersClick
            ) {

                Text("افزودن عضو")

            }

            return

        }

        LazyRow(

            horizontalArrangement = Arrangement.spacedBy(14.dp)

        ) {

            items(members) {

                MemberAvatar(

                    member = it,

                    onMemberClick = {}

                )

            }

        }

    }

}