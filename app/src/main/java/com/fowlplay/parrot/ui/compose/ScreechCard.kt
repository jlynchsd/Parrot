package com.fowlplay.parrot.ui.compose

import android.os.Build
import android.text.Html
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fowlplay.parrot.R
import com.fowlplay.parrot.model.Screech
import com.fowlplay.parrot.viewmodel.AppIntent
import com.fowlplay.parrot.viewmodel.ParrotViewModel

internal const val SCREECH_CARD_FAVORITE_BUTTON_TEST_ID = "Screech Card Favorite Button"
internal const val SCREECH_CARD_BODY_TEST_ID = "Screech Card Body"

@Composable
fun ScreechCard(screech: Screech, viewModel: ParrotViewModel) {
    Surface (
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (modifier = Modifier.padding(dimensionResource(R.dimen.card_padding))) {
            Image(
                painter = painterResource(R.drawable.parrot_head),
                contentDescription = "Caaw",
                modifier = Modifier
                    .size(dimensionResource(R.dimen.icon_size))
                    .clip(CircleShape)
                    .border(
                        dimensionResource(R.dimen.icon_border_size),
                        MaterialTheme.colorScheme.secondary,
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(4.dp))

            var expanded by remember { mutableStateOf(false) }
            var favorite by remember { mutableStateOf(screech.favorite) }

            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .testTag(SCREECH_CARD_BODY_TEST_ID)
            ) {
                Text(
                    text = screech.username,
                    fontSize = dimensionResource(R.dimen.username_font_size).value.sp,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(screech.message, Html.FROM_HTML_MODE_LEGACY).toString()
                    } else {
                        Html.fromHtml(screech.message).toString()
                    },
                    fontSize = dimensionResource(R.dimen.message_font_size).value.sp,
                    softWrap = true,
                    lineHeight = dimensionResource(R.dimen.message_line_height).value.sp,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    if (expanded) {
                        IconButton(onClick = {
                            favorite = !favorite
                            viewModel.postIntent(
                                AppIntent.ScreechButton.Favorite(screech.copy(favorite = favorite))
                            )
                        }, modifier = Modifier.testTag(SCREECH_CARD_FAVORITE_BUTTON_TEST_ID)) {
                            Icon(
                                if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(R.string.screech_card_favorite_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}