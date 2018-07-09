package pl.socketbyte.thousand.shared.settings

import org.ini4j.Ini
import org.ini4j.IniPreferences
import pl.socketbyte.thousand.shared.writeContentsFromResource
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter

object Settings {

    /**
     * Loads *.ini files or creates new ones (and applies defaults from resources)
     * Uses ini4j library
     *
     * @param path Path to the *.ini file
     */
    fun load(path: String): IniPreferences {
        val file = File(path)
        if (!file.exists()) {
            file.createNewFile()

            file.writeContentsFromResource<Settings>(path)
        }

        val ini = Ini(file)

        return IniPreferences(ini)
    }

}