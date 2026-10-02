package br.com.fenix.readerserver.enums.comicinfo

enum class AgeRating(val descricao: String) {
    Unknown("Unknown"),
    Adults("Adults Only 18+"),
    Early("Early Childhood"),
    Everyone("Everyone"),
    Everyone10("Everyone 10+"),
    G("G"),
    Kids("Kids to Adults"),
    M("M"),
    MA15("MA15+"),
    Mature("Mature 17+"),
    PG("PG"),
    R18("R18+"),
    Pending("Rating Pending"),
    Teen("Teen"),
    X18("X18+");

    override fun toString(): String {
        return descricao
    }
}
