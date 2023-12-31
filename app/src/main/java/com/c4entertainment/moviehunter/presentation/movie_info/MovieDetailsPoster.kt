package com.c4entertainment.moviehunter.presentation.movie_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.c4entertainment.moviehunter.R
import com.c4entertainment.moviehunter.domain.model.movie.Movie
import com.c4entertainment.moviehunter.components.movie_listing.item.MovieListItemInfo

@Composable
fun MovieDetailsPoster(movie: Movie, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(.5f),
//            tonalElevation = 100.dp,
            color = MaterialTheme.colorScheme.surfaceTint
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movie.backdropPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .defaultMinSize(minHeight = 220.dp)
                ,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                error = painterResource(id = R.drawable.baseline_image_24),
            )

            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(.8f)
                )
                MovieListItemInfo(
                    modifier = Modifier.weight(3f),
//                painter = painterResource(id = R.drawable.baseline_movie_filter_24),
                    info = movie.releaseDate.substring(0, 4),

                    )
            }

        }

        AsyncImage(
            model = "https://image.tmdb.org/t/p/original/${movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier
                .height(150.dp)
                .defaultMinSize(minHeight = 130.dp)
                .padding(16.dp)
            ,
            placeholder = painterResource(id = R.drawable.baseline_image_24),
        )

    }
}