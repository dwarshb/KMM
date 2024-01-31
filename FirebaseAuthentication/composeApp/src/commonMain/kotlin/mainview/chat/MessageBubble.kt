package mainview.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import firebase.Firebase
import kotlinx.coroutines.delay
import mainview.chat.model.Message

@Composable
inline fun MessageBubble(message: Message, modifier: Modifier = Modifier) {
    val currentUser = Firebase.getCurrentUser()
    val isReceiver = message.sender.uid!=currentUser.uid
    val senderName = message.sender.email
    val bubbleColor =
        if (isReceiver) MaterialTheme.colors.secondary
        else MaterialTheme.colors.primary

    var visibility by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        visibility = true
    }
    AnimatedVisibility(
        visible = visibility,
        enter = slideInHorizontally()
                + expandHorizontally(expandFrom = Alignment.Start)
                + scaleIn(transformOrigin = TransformOrigin(0.5f, 0f))
                + fadeIn(initialAlpha = 0.3f),
    ) {
        Box(
            contentAlignment = if (isReceiver) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = modifier
//                .padding(
//                    start = if (isReceiver) 0.dp else 50.dp,
//                    end = if (isReceiver) 50.dp else 0.dp,
//                )
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    bottomStart = 20.dp,
                                    bottomEnd = 20.dp,
                                    topEnd = if (isReceiver) 20.dp else 2.dp,
                                    topStart = if (isReceiver) 2.dp else 20.dp
                                )
                            )
                            .fillMaxWidth()
                            .background(color = bubbleColor)
                            .padding(vertical = 5.dp, horizontal = 16.dp),
                    ) {
                        Column {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                text = senderName,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 14.sp,
                                textAlign = if(isReceiver) TextAlign.Left else TextAlign.Right,
                                color = if (!isReceiver) Color.White else Color.Black,
                            )
                            Text(
                                text = message.text,
                                textAlign = if(isReceiver) TextAlign.Left else TextAlign.Right,
                                color = if (!isReceiver) Color.White else Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = message.messageTime,
                                textAlign = if(isReceiver) TextAlign.Left else TextAlign.Right,
                                fontSize = 12.sp,
                                color = if (!isReceiver) Color.White else Color.Black
                            )

                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 25.dp,
    circleColor: Color = MaterialTheme.colors.primary,
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 20.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200 with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        circleValues.forEach { value ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        translationY = -value * distance
                    }
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    )
            )
        }
    }

}