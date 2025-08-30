package red.man10.velocity.manager.config

import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import red.man10.velocity.manager.Utils
import red.man10.velocity.manager.VelocityMan10Manager
import java.io.File

object Config {
    val subConfigs = ArrayList<AbstractConfig>()

    fun load() {
        val configDir = VelocityMan10Manager.dataDirectory.resolve("config").toFile()
        if (!configDir.exists()) {
            configDir.mkdirs()
        }

        subConfigs.clear()
        val classes = Utils.getClasses(this::class.java.protectionDomain.codeSource.location, "red.man10.velocity.manager.config.sub")
        classes.forEach {
            if (AbstractConfig::class.java.isAssignableFrom(it)) {
                val constructor = it.getConstructor()
                val configInstance = constructor.newInstance() as AbstractConfig
                val file = File(configDir, "${configInstance.internalName}.yml")
                if (!file.exists()) {
                    file.createNewFile()
                    val loader = YamlConfigurationLoader.builder()
                        .nodeStyle(NodeStyle.BLOCK)
                        .indent(2)
                        .path(file.toPath()).build()
                    val node = loader.load()
                    configInstance.saveDefaultConfig(node)
                    loader.save(node)
                } else {
                    val loader = YamlConfigurationLoader.builder()
                        .nodeStyle(NodeStyle.BLOCK)
                        .indent(2)
                        .path(file.toPath()).build()
                    val node = loader.load()
                    configInstance.loadConfig(node)
                }
                subConfigs.add(configInstance)
            }
        }
    }

    inline fun <reified T: AbstractConfig> getConfig(): T? {
        return subConfigs.firstOrNull { it is T } as? T
    }

    inline fun <reified T: AbstractConfig> getOrThrow(): T {
        return getConfig<T>() ?: throw IllegalStateException("Config ${T::class.java.simpleName} not found")
    }
}