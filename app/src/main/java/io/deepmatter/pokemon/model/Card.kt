package io.deepmatter.pokemon.model

import com.google.gson.annotations.SerializedName

data class CardWrapper(@SerializedName("cards") val cards: List<Card> = emptyList())

data class Card(@SerializedName("imageUrl") val image: String = "",
                @SerializedName("rarity") val rarity: Rarity = Rarity.Common)

enum class Rarity {
    Common,
    Uncommon,
    Rare,
    RareHolo,
    RareUltra,
    RareSecret
}
