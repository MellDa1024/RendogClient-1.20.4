package kr.rendog.client.service

class LootPerMinuteService {
    private val loots = ArrayDeque<Long>()

    fun addCount() {
        val now = System.currentTimeMillis()
        loots.addLast(now)
        while (loots.isNotEmpty() && now - loots.first() > 60_000) {
            loots.removeFirst()
        }
    }

    fun getLPM(): Int {
        val now = System.currentTimeMillis()
        while (loots.isNotEmpty() && now - loots.first() > 60_000) {
            loots.removeFirst()
        }
        return loots.size
    }
}