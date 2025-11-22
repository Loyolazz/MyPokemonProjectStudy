package com.trainerjourney.mypokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun TeamSelectionSection(
    teams: List<TeamOption>,
    onSelectTeam: (TeamOption) -> Unit,
    capturedAvailable: Boolean,
    onUseCaptured: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "⚡️ Equipes pré-definidas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Curadoria de trios icônicos com tipos complementares para você começar rápido.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        teams.forEach { team ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(team.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(team.description, style = MaterialTheme.typography.bodyMedium)
                        }
                        TextButton(onClick = { onSelectTeam(team) }) {
                            Text("Usar")
                        }
                    }
                    Text(
                        text = "Integrantes: ${team.members.joinToString { it.name }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (capturedAvailable) {
            TextButton(onClick = onUseCaptured) {
                Text("Usar equipe com Pokémon capturados")
            }
        }
    }
}

@Composable
fun SelectedTeamSection(team: TeamOption) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Equipe selecionada",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(team.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                team.members.forEach { member ->
                    MemberRow(member)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun MemberRow(member: TeamMember) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (member.spriteUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(member.spriteUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = member.name,
                    modifier = Modifier.height(64.dp),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                )
            }
            Text(member.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            if (member.legendary) {
                AssistChip(
                    onClick = {},
                    label = { Text("Lendário") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
        Text("Tipos: ${member.types.joinToString()}", style = MaterialTheme.typography.bodySmall)
        Text(
            "Ataques: ${member.attacks.joinToString()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
