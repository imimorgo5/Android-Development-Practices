package com.example.android_development_practices.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
internal fun <T : Any> FilterSettingsContent(
    definitions: List<CatalogFilterDefinition<T>>,
    options: Map<String, List<CatalogOption>>,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Выберите условия для этого каталога", style = MaterialTheme.typography.bodyMedium)
        definitions.forEach { definition ->
            if (definition.isDate) {
                ApiDatePickerField(
                    label = definition.label,
                    value = values[definition.key].orEmpty(),
                    onValueChange = { onValueChange(definition.key, it) },
                )
            } else {
                CatalogOptionPicker(
                    label = definition.label,
                    selectedValue = values[definition.key].orEmpty(),
                    options = options[definition.key].orEmpty(),
                    onSelected = { onValueChange(definition.key, it) },
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onReset) { Text("Сбросить") }
            Button(onClick = onApply) { Text("Готово") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogOptionPicker(
    label: String,
    selectedValue: String,
    options: List<CatalogOption>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label.orEmpty()
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Любое") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Любое") }, onClick = { onSelected(""); expanded = false })
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option.label) }, onClick = { onSelected(option.value); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApiDatePickerField(label: String, value: String, onValueChange: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val state = rememberDatePickerState(initialSelectedDateMillis = value.toUtcMillis())
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("Не выбрана") },
        trailingIcon = {
            Row {
                if (value.isNotBlank()) IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, "Очистить дату")
                }
                IconButton(onClick = { showPicker = true }) { Text("▦") }
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    )
    if (showPicker) DatePickerDialog(
        onDismissRequest = { showPicker = false },
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onValueChange(it.toUtcDate()) }
                showPicker = false
            }) { Text("Выбрать") }
        },
        dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Отмена") } },
    ) { DatePicker(state = state) }
}

private fun String.toUtcMillis(): Long? = takeIf(String::isNotBlank)?.let { value ->
    runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
            .parse(value.take(10))?.time
    }.getOrNull()
}

private fun Long.toUtcDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(this))

