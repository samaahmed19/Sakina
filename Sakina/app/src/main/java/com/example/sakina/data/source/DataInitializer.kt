package com.example.sakina.data.source

import android.content.Context
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.dao.DuaDao
import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.dao.PrayerDao
import com.example.sakina.data.local.database.dao.TasbeehDao
import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.SurahEntity
import com.example.sakina.data.source.mapper.JsonMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject

class DataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val azkarDao: AzkarDao,
    private val duaDao: DuaDao,
    private val quranDao: QuranDao,
    private val tasbeehDao: TasbeehDao,
    private val checklistDao: ChecklistDao,
    private val prayerDao: PrayerDao
) {

    suspend fun initAzkarIfNeeded() = withContext(Dispatchers.IO) {
        if (azkarDao.getAllCategories().isNotEmpty()) return@withContext

        val json = context.assets
            .open("azkar.json")
            .bufferedReader()
            .use { it.readText() }

        val (categories, azkar) = JsonMapper.mapCategories(json)

        azkarDao.insertCategories(categories)
        azkarDao.insertAzkar(azkar)
    }
    private val surahNamesAr = arrayOf(
        "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس", "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون", "النور", "الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت", "الروم", "لقمان", "السجدة", "الأحزاب", "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر", "فصلت", "الشورى", "الزخرف", "الدخان", "الجاثية", "الأحقاف", "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج", "نوح", "الجن", "المزمل", "المدثر", "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس", "التكوير", "الانفطار", "المطففين", "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح", "التين", "العلق", "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر", "العصر", "الهمزة", "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص", "الفلق", "الناس"
    )
    suspend fun initQuranIfNeeded() = withContext(Dispatchers.IO) {
        if (quranDao.getAyahsCount() > 0) return@withContext

        try {
            val jsonString = context.assets
                .open("quran.json")
                .bufferedReader()
                .use { it.readText() }

            val rootObject = org.json.JSONObject(jsonString)
            val ayahs = mutableListOf<AyahEntity>()
            val surahs = mutableListOf<SurahEntity>()
            val surahKeys = rootObject.keys()

            while (surahKeys.hasNext()) {
                val surahKey = surahKeys.next()
                val ayahsArray = rootObject.getJSONArray(surahKey)

                val surahId = surahKey.toInt()


                val name = if (surahId <= 114) surahNamesAr[surahId - 1] else "سورة $surahId"

                surahs.add(
                    SurahEntity(
                        id = surahId,
                        nameAr = name,
                        nameEn = "Surah $surahId",
                        ayahCount = ayahsArray.length()
                    )
                )
                for (i in 0 until ayahsArray.length()) {
                    val ayahJson = ayahsArray.getJSONObject(i)
                    ayahs.add(
                        AyahEntity(
                            surahId = surahId,
                            text = ayahJson.getString("text"),
                            number = ayahJson.getInt("verse")
                        )
                    )
                }
            }
            quranDao.insertSurahs(surahs)
            quranDao.insertAyahs(ayahs)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun initDuasIfNeeded() = withContext(Dispatchers.IO) {
        if (duaDao.getAllCategories().isNotEmpty()) return@withContext

        val json = context.assets
            .open("dua.json")
            .bufferedReader()
            .use { it.readText() }

        val (categories, duas) = JsonMapper.mapDuas(json)
        duaDao.insertCategories(categories)
        duaDao.insertAll(duas)
    }

    suspend fun initTasbeehIfNeeded() = withContext(Dispatchers.IO) {
        if (tasbeehDao.getAll().isNotEmpty()) return@withContext

        val json = context.assets
            .open("tasbeeh.json")
            .bufferedReader()
            .use { it.readText() }

        val tasbeehList = JsonMapper.mapTasbeeh(json)

        tasbeehDao.insertAll(tasbeehList)
    }
}
