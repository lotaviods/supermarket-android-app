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
    @ColumnInfo(name = "name") val nome: String? = "",
    @ColumnInfo(name = "value") val valor: String? = "",
    @ColumnInfo(name = "category") val category: Int? = 0,
    @ColumnInfo(name = "qnt") val qnt: Int? = 0,
    @ColumnInfo(name = "unit_int", defaultValue = "${Constants.UNIT_PIECE}") val unidade: Int = Constants.UNIT_PIECE,
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
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(nome)
        parcel.writeString(valor)
        parcel.writeValue(category)
        parcel.writeValue(qnt)
        parcel.writeInt(unidade)
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
