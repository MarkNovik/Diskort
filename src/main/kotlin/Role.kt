import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.ReactionEmoji

enum class Role(val id: Snowflake, val reactionEmoji: ReactionEmoji) {
    Gamer(Snowflake("970027449318535278"), ReactionEmoji.Unicode("""🎮""")),
    Programmer(Snowflake("970027632693510154"), ReactionEmoji.Unicode("""💻""")),
    Noob(Snowflake("970027624455897088"), ReactionEmoji.Unicode("""👶""")),
    Ponger(Snowflake("970271783200645150"), ReactionEmoji.Unicode("""🏓"""));

    companion object {
        val values: List<Role> = values().toList()
        fun byIdOrNull(id: Snowflake): Role? = values.find { it.id == id }
    }
}