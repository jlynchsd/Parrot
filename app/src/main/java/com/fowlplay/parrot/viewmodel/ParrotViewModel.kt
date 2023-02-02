package com.fowlplay.parrot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.fowlplay.parrot.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ParrotViewModel @Inject constructor(injectModel: ParrotModel) : ViewModel() {

    val model: ParrotModel = injectModel

    private val persistentPagingSources = arrayOfNulls<PagingSource<Int, Screech>>(2)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val screeches = Pager(PagingConfig(pageSize = 15)) {
        ScreechPagingSource(model)
    }.flow.cachedIn(viewModelScope)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val userScreeches = Pager(PagingConfig(pageSize = 15)) {
        DatabasePagingSource(model, false).also {
            persistentPagingSources[0] = it
        }
    }.flow.cachedIn(viewModelScope)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val favoriteScreeches = Pager(PagingConfig(pageSize = 15)) {
        DatabasePagingSource(model, true).also {
            persistentPagingSources[1] = it
        }
    }.flow.cachedIn(viewModelScope)


    private val initialSettings = runBlocking { model.getSettingsFlow().first() }
    private val _state = MutableStateFlow(State(ViewState.SplashPage, initialSettings.theme))
    val state: StateFlow<State> = _state

    private val stateHandler = StateHandler(_state, initialSettings, model, persistentPagingSources)

    init {
        viewModelScope.launch {
            stateHandler.initHandler()
        }
    }

    fun updateState(newState: State) {
        _state.value = newState
    }

    fun postIntent(intent: AppIntent) {
        viewModelScope.launch {
            stateHandler.handleIntent(intent)
        }
    }
}