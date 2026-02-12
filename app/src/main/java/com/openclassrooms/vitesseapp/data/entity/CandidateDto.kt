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
    var photoByteArray: ByteArray?,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "birthdate")
    var birthdate: Long,

    @ColumnInfo(name = "salary_in_cents_euro")
    var salaryCentsInEur: Long?,

    @ColumnInfo(name = "notes")
    var notes: String?,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CandidateDto

        if (candidateId != other.candidateId) return false
        if (birthdate != other.birthdate) return false
        if (salaryCentsInEur != other.salaryCentsInEur) return false
        if (isFavorite != other.isFavorite) return false
        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (!photoByteArray.contentEquals(other.photoByteArray)) return false
        if (phone != other.phone) return false
        if (email != other.email) return false
        if (notes != other.notes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = candidateId.hashCode()
        result = 31 * result + birthdate.hashCode()
        result = 31 * result + (salaryCentsInEur?.hashCode() ?: 0)
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + (photoByteArray?.contentHashCode() ?: 0)
        result = 31 * result + phone.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (notes?.hashCode() ?: 0)
        return result
    }
}