package com.fowlplay.parrot.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreechPagingSource(private val model: ParrotModel) : PagingSource<Int, Screech>() {

    override suspend fun load(params: LoadParams<Int>) =
        withContext(Dispatchers.IO) {
            formatLoadResult(
                generateScreeches(params.loadSize),
                params.key ?: 1
            )
        }

    override fun getRefreshKey(state: PagingState<Int, Screech>) =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    private suspend fun generateScreeches(count: Int) =
        emptyList<Screech>().toMutableList().also {
            if (model.initialCache.isNotEmpty()) {
                it.addAll(model.initialCache)
                model.initialCache.clear()
            }
            val initialSize = it.size
            for (i in 0 until count - initialSize) {
                it.add(model.generateScreech())
            }
        }

    private fun formatLoadResult(screeches: List<Screech>, page: Int) =
        LoadResult.Page(
            data = screeches,
            prevKey = if (page == 1) null else page - 1,
            nextKey = page + 1
        )
}