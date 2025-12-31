package com.haroun.gymi.domain.model

data class TrainingDay(
    val date: Long,
    val sets: List<SetEntry>
)