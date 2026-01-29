package com.openclassrooms.vitesseapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class CandidateDto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "candidateId")
    var candidateId: Long = 0,

    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

    @ColumnInfo(name = "photo")
    var photoPath: String?,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "birthdate")
    var birthdate: Long,

    @ColumnInfo(name = "salary_in_euro")
    var salaryInEur: Long?,

    @ColumnInfo(name = "notes")
    var notes: String?,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false,
) {
}