package com.fowlplay.parrot.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DatabasePagingSource(
    private val model: ParrotModel,
    private val loadFavorites: Boolean = false
) : PagingSource<Int, Screech>() {

    override suspend fun load(params: LoadParams<Int>) =
        withContext(Dispatchers.IO) {
            formatLoadResult(
                pageScreeches((params.key ?: 0) * params.loadSize, params.loadSize),
                params.key ?: 1,
                params.loadSize
            )
        }

    override fun getRefreshKey(state: PagingState<Int, Screech>) =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    private suspend fun pageScreeches(offset: Int, count: Int) =
        emptyList<Screech>().toMutableList().also { result ->
            result.addAll(
                if (loadFavorites) {
                    model.pageFavoriteScreeches(
                        offset,
                        count
                    )
                } else {
                    model.pageUserScreeches(
                        model.getSettingsFlow().first().username,
                        offset,
                        count
                    )
                }
            )
        }

    private fun formatLoadResult(screeches: List<Screech>, page: Int, pageSize: Int) =
        LoadResult.Page(
            data = screeches,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (screeches.size < pageSize) null else page + 1
        )
}