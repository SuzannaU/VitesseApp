package com.openclassrooms.vitesseapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class CandidateDto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "candidateId")
    var id: Long = 0,

    @ColumnInfo(name = "firstname")
    var name: String,

) {
}