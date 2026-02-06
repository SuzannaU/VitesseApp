package com.openclassrooms.vitesseapp.domain.model

data class Candidate(
    val candidateId: Long = 0,
    val firstname: String,
    val lastname: String,
    val photoByteArray: ByteArray?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val age: Int?,
    val salaryCentsInEur: Long?,
    val notes: String?,
    val isFavorite: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Candidate

        if (candidateId != other.candidateId) return false
        if (birthdate != other.birthdate) return false
        if (age != other.age) return false
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
        result = 31 * result + (age ?: 0)
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