package com.c4entertainment.moviehunter.data.network.dto.movie_details

import com.squareup.moshi.Json


data class CreditsDto (

    @field:Json(name ="cast" ) var cast : List<CastDto>? = listOf(),
    @field:Json(name ="crew" ) var crew : List<CrewDto>? = listOf()

)