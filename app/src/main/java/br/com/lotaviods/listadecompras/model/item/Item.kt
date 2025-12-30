package br.com.lotaviods.listadecompras.model.item

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.lotaviods.listadecompras.constantes.Constants

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String? = "",
    @ColumnInfo(name = "value") val value: String? = "",
    @ColumnInfo(name = "category") val category: Int? = 0,
    @ColumnInfo(name = "qnt") val quantity: Int? = 0,
    @ColumnInfo(name = "unit_int", defaultValue = "${Constants.UNIT_PIECE}") val unit: Int = Constants.UNIT_PIECE,
    @ColumnInfo(name = "list_id") val listId: Int = 1
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(name)
        parcel.writeString(value)
        parcel.writeValue(category)
        parcel.writeValue(quantity)
        parcel.writeInt(unit)
        parcel.writeInt(listId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}
