package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teamrocket.passengerapp.data.api.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var age by remember { mutableStateOf(currentUser.age?.toString() ?: "") }
    var address by remember { mutableStateOf(currentUser.address ?: "") }
    var gender by remember { mutableStateOf(currentUser.gender) }
    
    // We don't allow editing mobile number here for security, typically
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { if(it.all { c -> c.isDigit() }) age = it },
                    label = { Text("Age") },
                    singleLine = true
                )
                  // Gender Selection (Simple dropdown or text for brevity, or reuse chips)
                Text("Gender", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female", "Other").forEach { 
                        FilterChip(
                            selected = gender == it,
                            onClick = { gender = it },
                            label = { Text(it) }
                        )
                    }
                }
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(currentUser.copy(
                    name = name,
                    age = age.toIntOrNull(),
                    address = address,
                    gender = gender
                ))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
