package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teamrocket.passengerapp.R
import com.teamrocket.passengerapp.ui.components.SelectionCard
import com.teamrocket.passengerapp.ui.components.StandardButton
import com.teamrocket.passengerapp.utils.LocaleManager

@Composable
fun LanguageSelectionScreen(
    onNavigateToHome: () -> Unit 
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                StandardButton(
                    text = stringResource(R.string.continue_btn),
                    onClick = {
                        val code = LocaleManager.getLanguageCode(selectedLanguage)
                        LocaleManager.setLocale(context, code)
                        onNavigateToHome()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    enabled = true
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Section with decorative curves
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(Color.White)
            ) {
                // Gradient overlay
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.White
                        )
                    )
                ))
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Text(
                        text = stringResource(R.string.select_language),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.choose_language_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Language List
            LazyColumn(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(24.dp)
            ) {
                item {
                    SelectionCard(
                        title = stringResource(R.string.lang_english),
                        subtitle = stringResource(R.string.default_label),
                        symbol = "Aa",
                        isSelected = selectedLanguage == "English",
                        onClick = { selectedLanguage = "English" }
                    )
                }
                item {
                    SelectionCard(
                        title = stringResource(R.string.lang_hindi),
                        subtitle = stringResource(R.string.hindi_label),
                        symbol = "अ",
                        isSelected = selectedLanguage == "Hindi",
                        onClick = { selectedLanguage = "Hindi" }
                    )
                }
                item {
                    SelectionCard(
                        title = stringResource(R.string.lang_gujarati),
                        subtitle = stringResource(R.string.gujarati_label),
                        symbol = "અ",
                        isSelected = selectedLanguage == "Gujarati",
                        onClick = { selectedLanguage = "Gujarati" }
                    )
                }
            }
        }
    }
}
