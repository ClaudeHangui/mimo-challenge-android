package com.changui.mimochallengeandroid.data.local

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Entity(tableName = "ActiveLesson")
@Parcelize
data class LocalLesson(
    @PrimaryKey
    val id: Int,
    @Embedded(prefix = "input_")
    @TypeParceler<Input, InputParceler>
    val input: Input?,
    @TypeParceler<Content, ContentParceler>
    val content: List<Content>
    ): Parcelable


@Parcelize
data class Input(
    val endIndex: Int,
    val startIndex: Int
): Parcelable

object InputParceler: Parceler<Input> {
    override fun create(parcel: Parcel) = Input (
        parcel.readInt(),
        parcel.readInt()
    )

    override fun Input.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(endIndex)
        parcel.writeInt(startIndex)
    }
}

@Parcelize
data class Content(
    val color: String,
    val text: String
): Parcelable

object ContentParceler: Parceler<Content> {
    override fun create(parcel: Parcel) = Content(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
    )

    override fun Content.write(parcel: Parcel, flags: Int) {
        parcel.writeString(color)
        parcel.writeString(text)
    }

}