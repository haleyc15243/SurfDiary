package com.halebop.surfdiary.ui

import android.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SaveCancelDialog(
    onSavePressed: () -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(stringResource(com.halebop.surfdiary.application.R.string.button_title_cancel))
                    }
                    TextButton(
                        onClick = onSavePressed,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(stringResource(com.halebop.surfdiary.application.R.string.button_title_save))
                    }
                }
            }
        }
    }
}

@Composable
fun AppTextEntry(
    currentText: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    TextField(
        value = currentText,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            Box(modifier = Modifier
                .wrapContentSize()
                .background(MaterialTheme.colors.primary)
                .clickable { onValueChange("") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = null
                )
            }
        },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(9.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onSurface,
        )
    )
}

@Composable
fun AppCardListItem(
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun CircularBackgroundIcon(
    @DrawableRes iconRes: Int,
    backgroundColor: Color = MaterialTheme.colors.primary
) {
    Box(modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(backgroundColor)) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Diary Entry",
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}