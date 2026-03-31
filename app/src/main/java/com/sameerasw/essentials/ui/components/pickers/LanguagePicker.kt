package com.sameerasw.essentials.ui.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalView
import com.sameerasw.essentials.R
import com.sameerasw.essentials.utils.LanguageUtils
import com.sameerasw.essentials.utils.HapticUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LanguagePicker(
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var expanded by remember { mutableStateOf(false) }
    val languages = LanguageUtils.languages
    val selectedLanguage = languages.find { it.code == selectedLanguageCode } ?: languages.first()

    ListItem(
        onClick = {},
        modifier = modifier.fillMaxWidth(),
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.rounded_globe_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        trailingContent = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = "${selectedLanguage.nativeName} (${selectedLanguage.name})",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .widthIn(max = 200.dp) // Limit width to prevent overflow
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = {
                                Text(text = "${language.nativeName} (${language.name})")
                            },
                            onClick = {
                                HapticUtil.performVirtualKeyHaptic(view)
                                onLanguageSelected(language.code)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        },
        content = {
            Text(
                text = stringResource(R.string.label_app_language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}
