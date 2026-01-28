@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val id: Int,
    val nameAr: String,
    val nameEn: String,
    val ayahCount: Int
)