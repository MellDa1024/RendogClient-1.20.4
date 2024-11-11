package kr.rendog.client.utils

import net.minecraft.util.Formatting

object TextUtils {
    fun String.deColorize(): String {
        return Formatting.strip(this)!!
    }
}