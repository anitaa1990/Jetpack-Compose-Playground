package com.an.jetpack_compose_playground.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComposeTextEditor(
    annotatedString: AnnotatedString,
    activeFormats: Set<FormattingAction>,
    onAnnotatedStringChange: (AnnotatedString) -> Unit,
    onFormattingSpansChange: (List<FormattingSpan>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Tracks the current value of the text field, including the annotated string and its formatting spans.
    var textFieldValue by remember { mutableStateOf(TextFieldValue(annotatedString)) }

    // Synchronize the local textFieldValue with the latest annotatedString from the parent.
    LaunchedEffect(annotatedString) {
        if (annotatedString != textFieldValue.annotatedString) {
            textFieldValue = TextFieldValue(annotatedString)
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val newText = newValue.text

            // Adjust existing spans to match the updated text content.
            val adjustedSpans = adjustSpansForNewText(
                existingAnnotatedString = textFieldValue.annotatedString,
                newText = newText
            )

            // Add active formatting spans based on the current selection or newly typed text.
            val updatedSpans = addActiveFormattingSpans(
                currentSpans = adjustedSpans,
                selectionStart = newValue.selection.start,
                selectionEnd = newValue.selection.end,
                activeFormats = activeFormats
            )

            // Apply the formatting spans to create a new AnnotatedString.
            val updatedAnnotatedString = applyFormattingSpans(newText, updatedSpans)

            // Update the local state with the new value.
            textFieldValue = newValue.copy(annotatedString = updatedAnnotatedString)

            // Notify the parent composable about the updated AnnotatedString and formatting spans.
            onAnnotatedStringChange(updatedAnnotatedString)
            onFormattingSpansChange(updatedSpans)
        },
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier
    )
}

/**
 * A toolbar for rich text formatting.
 * Displays buttons for various formatting actions such as bold, italics, underline, and more.
 *
 * @param activeFormats The set of currently active formatting actions.
 * @param onFormatToggle Callback to toggle a formatting action on or off.
 */
@Composable
fun EditorToolbar(
    activeFormats: Set<FormattingAction>,
    onFormatToggle: (FormattingAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Iterate through all formatting actions and display them as toggleable buttons.
        FormattingAction.entries.forEach { format ->
            IconButton(
                onClick = { onFormatToggle(format) }
            ) {
                Icon(
                    imageVector = getIconForFormat(format),
                    contentDescription = format.name,
                    tint = if (activeFormats.contains(format)) {
                        MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Maps each formatting action to its corresponding icon.
 */
private fun getIconForFormat(format: FormattingAction): ImageVector {
    return when (format) {
        FormattingAction.Heading -> Icons.Default.Title
        FormattingAction.SubHeading -> Icons.Default.Subtitles
        FormattingAction.Bold -> Icons.Default.FormatBold
        FormattingAction.Italics -> Icons.Default.FormatItalic
        FormattingAction.Underline -> Icons.Default.FormatUnderlined
        FormattingAction.Strikethrough -> Icons.Default.StrikethroughS
        FormattingAction.Highlight -> Icons.Default.Highlight
    }
}

/**
 * A data class representing a formatting span applied to a specific range of text.
 * @param start The start index of the span.
 * @param end The end index of the span.
 * @param formats The set of formatting actions applied to the span.
 */
data class FormattingSpan(val start: Int, val end: Int, val formats: Set<FormattingAction>)

/**
 * Applies formatting spans to the given text to create an AnnotatedString.
 */
private fun applyFormattingSpans(
    text: String,
    formattingSpans: List<FormattingSpan>
): AnnotatedString {
    val builder = AnnotatedString.Builder(text)

    // Reapply all formatting spans
    formattingSpans.forEach { span ->
        span.formats.forEach { format ->
            when (format) {
                FormattingAction.Bold -> builder.addStyle(
                    style = BoldStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Italics -> builder.addStyle(
                    style = ItalicsStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Underline -> builder.addStyle(
                    style = UnderlineStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Strikethrough -> builder.addStyle(
                    style = StrikeThroughStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Highlight -> builder.addStyle(
                    style = HighlightStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.Heading -> builder.addStyle(
                    style = HeadingStyle,
                    start = span.start,
                    end = span.end
                )
                FormattingAction.SubHeading -> builder.addStyle(
                    style = SubtitleStyle,
                    start = span.start,
                    end = span.end
                )
            }
        }
    }

    return builder.toAnnotatedString()
}

/**
 * Adjusts formatting spans to fit the new text after an edit.
 * Ensures spans are valid and within bounds of the updated text.
 *
 * @param existingAnnotatedString The current annotated string before the edit.
 * @param newText The updated text after the edit.
 * @return A list of adjusted formatting spans.
 */
private fun adjustSpansForNewText(
    existingAnnotatedString: AnnotatedString,
    newText: String
): List<FormattingSpan> {
    val newTextLength = newText.length

    return existingAnnotatedString.spanStyles.mapNotNull { span ->
        // Adjust start and end indices to fit the new text
        val adjustedStart = span.start.coerceAtMost(newTextLength)
        val adjustedEnd = span.end.coerceAtMost(newTextLength)

        if (adjustedStart < adjustedEnd) {
            // Convert AnnotatedString.Range into a FormattingSpan
            FormattingSpan(
                start = adjustedStart,
                end = adjustedEnd,
                formats = extractFormatsFromSpanStyle(span.item)
            )
        } else {
            null // Remove invalid spans
        }
    }
}

/**
 * Extracts formatting actions from a SpanStyle.
 */
private fun extractFormatsFromSpanStyle(spanStyle: SpanStyle): Set<FormattingAction> {
    val formats = mutableSetOf<FormattingAction>()

    if (spanStyle == BoldStyle) formats.add(FormattingAction.Bold)
    if (spanStyle == ItalicsStyle) formats.add(FormattingAction.Italics)
    if (spanStyle == UnderlineStyle) formats.add(FormattingAction.Underline)
    if (spanStyle == StrikeThroughStyle) formats.add(FormattingAction.Strikethrough)
    if (spanStyle == HighlightStyle) formats.add(FormattingAction.Highlight)
    if (spanStyle == HeadingStyle) formats.add(FormattingAction.Heading)
    if (spanStyle == SubtitleStyle) formats.add(FormattingAction.SubHeading)

    return formats
}

/**
 * Adds active formatting spans to the selected range or newly typed text.
 *
 * @param currentSpans The existing formatting spans.
 * @param selectionStart The start of the selected range.
 * @param selectionEnd The end of the selected range.
 * @param activeFormats The set of currently active formats.
 * @return A list of updated formatting spans.
 */
private fun addActiveFormattingSpans(
    currentSpans: List<FormattingSpan>,
    selectionStart: Int,
    selectionEnd: Int,
    activeFormats: Set<FormattingAction>
): List<FormattingSpan> {
    val updatedSpans = currentSpans.toMutableList()

    if (selectionStart != selectionEnd && activeFormats.isNotEmpty()) {
        // Add formatting for the selected range
        updatedSpans.add(
            FormattingSpan(selectionStart, selectionEnd, activeFormats)
        )
    } else if (activeFormats.isNotEmpty() && selectionStart > 0) {
        // Apply active formats to the last character typed
        val lastCharIndex = selectionStart - 1
        updatedSpans.add(
            FormattingSpan(lastCharIndex, selectionStart, activeFormats)
        )
    }

    return updatedSpans
}

enum class FormattingAction {
    Heading,
    SubHeading,
    Bold,
    Italics,
    Underline,
    Strikethrough,
    Highlight
}

val HighlightStyle = SpanStyle(background = Color.Yellow)
val HeadingStyle = SpanStyle(fontSize = 24.sp)
val SubtitleStyle = SpanStyle(fontSize = 20.sp)
val StrikeThroughStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
val UnderlineStyle = SpanStyle(textDecoration = TextDecoration.Underline)
val ItalicsStyle = SpanStyle(fontStyle = FontStyle.Italic)
val BoldStyle = SpanStyle(fontWeight = FontWeight.Bold)
