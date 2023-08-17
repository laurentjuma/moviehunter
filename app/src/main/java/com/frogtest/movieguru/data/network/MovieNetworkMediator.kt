package com.frogtest.movieguru.data.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.frogtest.movieguru.data.cache.MovieDatabase
import com.frogtest.movieguru.data.cache.entity.MovieEntity
import com.frogtest.movieguru.data.cache.entity.MovieRemoteKeyEntity
import com.frogtest.movieguru.data.mappers.toMovieEntity
import com.frogtest.movieguru.data.network.api.OMDBMovieAPI
import javax.inject.Inject

private const val TAG = "MovieNetworkMediator"

@OptIn(ExperimentalPagingApi::class)
class MovieNetworkMediator @Inject constructor(
    private val OMDBMovieApi: OMDBMovieAPI,
    private val movieDb: MovieDatabase
): RemoteMediator<Int, MovieEntity>() {

    private val movieDao = movieDb.movieDao
    private val remoteKeyDao = movieDb.movieRemoteKeyDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val response = OMDBMovieApi.getMovies(page = currentPage).data
            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            movieDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.clearAll()
                    remoteKeyDao.deleteAllRemoteKeys()
                }
                val keys = response.map { movieDto ->
                    MovieRemoteKeyEntity(
                        id = movieDto.imdbID,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                remoteKeyDao.addAllRemoteKeys(remoteKeys = keys)
                movieDao.insertAll(movies = response.map { it.toMovieEntity() })
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieEntity>
    ): MovieRemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.imdbID?.let { id ->
                remoteKeyDao.getRemoteKey(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MovieEntity>
    ): MovieRemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movieEntity ->
                remoteKeyDao.getRemoteKey(id = movieEntity.imdbID)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MovieEntity>
    ): MovieRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movieEntity ->
                remoteKeyDao.getRemoteKey(id = movieEntity.imdbID)
            }
    }
}