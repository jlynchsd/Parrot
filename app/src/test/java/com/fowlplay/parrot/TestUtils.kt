package com.fowlplay.parrot

import androidx.compose.ui.semantics.SemanticsProperties.EditableText
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.lifecycle.LiveData
import com.fowlplay.parrot.viewmodel.ParrotViewModel
import com.fowlplay.parrot.viewmodel.State
import com.fowlplay.parrot.viewmodel.Theme
import com.fowlplay.parrot.viewmodel.ViewState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun <T> LiveData<T>.suspendedValue(target: T) {
    if (this.value != target) {
        suspendCoroutine<T> { continuation ->
            this.observeForever {
                if (it == target) {
                    continuation.resume(it)
                }
            }
        }
    }
}

fun stubViewModel() = mockk<ParrotViewModel>(relaxed = true).also {
    every { it.state } returns MutableStateFlow(State(ViewState.OnboardingPage , Theme.BlueYellowMacaw))
}

fun SemanticsNodeInteraction.getEditableText() = this.fetchSemanticsNode().config[EditableText].text