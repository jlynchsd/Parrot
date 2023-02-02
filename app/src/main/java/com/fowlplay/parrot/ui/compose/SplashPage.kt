package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.fowlplay.parrot.R

@Composable
fun SplashPage() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val image = createRef()
        val parrotStartGuideline = createGuidelineFromStart(.2f)
        val parrotEndGuideline = createGuidelineFromEnd(.2f)
        val parrotTopGuideline = createGuidelineFromTop(.3f)
        val parrotBottomGuideline = createGuidelineFromBottom(.3f)
        Image(
            painter = painterResource(R.drawable.parrot_head),
            contentDescription = "Caaw",
            alignment = Alignment.Center,
            modifier = Modifier.constrainAs(image) {
                start.linkTo(parrotStartGuideline)
                end.linkTo(parrotEndGuideline)
                top.linkTo(parrotTopGuideline)
                bottom.linkTo(parrotBottomGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )
    }
}