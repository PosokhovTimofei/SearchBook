package com.example.searchbook.data.model

import AuthorWrapper
import Excerpt
import Link

data class BookDetails(
    val title: String?,
    val description: Any?,
    val covers: List<Int>?,
    val subject_places: List<String>?,
    val subject_people: List<String>?,
    val subject_times: List<String>?,
    val subjects: List<String>?,
    val excerpts: List<Excerpt>? = null,
    val links: List<Link>? = null,
    val authors: List<AuthorWrapper>? = null
)