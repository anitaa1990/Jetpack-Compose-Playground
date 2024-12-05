package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.an.jetpack_compose_playground.R
import com.an.jetpack_compose_playground.ui.component.ComposeTextEditor
import com.an.jetpack_compose_playground.ui.component.EditorToolbar
import com.an.jetpack_compose_playground.ui.component.FormattingAction
import com.an.jetpack_compose_playground.ui.component.FormattingSpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.btn_txt_compose_text_editor)
                    )
                },
                modifier = Modifier
            )
        }
    ) { innerPadding ->
        var text by remember { mutableStateOf<AnnotatedString>(AnnotatedString("")) }
        var activeFormats by rememberSaveable { mutableStateOf(setOf<FormattingAction>()) }
        var formattingSpans by remember { mutableStateOf(listOf<FormattingSpan>()) }

        Column(modifier = Modifier.padding(innerPadding)) {
            EditorToolbar(
                activeFormats = activeFormats,
                onFormatToggle = { format ->
                    activeFormats = if (activeFormats.contains(format)) {
                        activeFormats - format
                    } else {
                        if (format == FormattingAction.Heading || format == FormattingAction.SubHeading) {
                            activeFormats - FormattingAction.Heading - FormattingAction.SubHeading + format
                        } else {
                            activeFormats + format
                        }
                    }
                }
            )

            ComposeTextEditor(
                annotatedString = text,
                activeFormats = activeFormats,
                onAnnotatedStringChange = { text = it },
                onFormattingSpansChange = { updatedSpans ->
                    formattingSpans = updatedSpans
                },
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}