import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    kordMain()
}

enum class Role(val id: Snowflake, val reactionEmoji: ReactionEmoji) {
    Gamer(Snowflake("970027449318535278"), ReactionEmoji.Unicode("""🎮""")),
    Programmer(Snowflake("970027632693510154"), ReactionEmoji.Unicode("""💻""")),
    Noob(Snowflake("970027624455897088"), ReactionEmoji.Unicode("""👶"""));

    companion object {
        val values: List<Role> = values().toList()
        fun byIdOrNull(id: Snowflake): Role? = values.find { it.id == id }
    }
}

@OptIn(PrivilegedIntent::class)
suspend fun kordMain() {
    val kord = Kord(TOKEN)
    val pingPong = ReactionEmoji.Unicode("""🏓""")

    (kord.getChannel(ROLES_CHANNEL_ID) as? MessageChannel)?.let { channel ->
        val message = channel.getMessage(ROLE_POST_ID)
        Role.values.forEach { role ->
            message.addReaction(role.reactionEmoji)
        }
    }

    kord.on<ReactionAddEvent> {
        if (messageId == ROLE_POST_ID) {
            val role = Role.values.find { it.reactionEmoji == emoji }
            if (role != null) {
                userAsMember?.addRole(role.id)
                println("${user.asUser().username} is now $role")
            }
        }
    }

    kord.on<ReactionRemoveEvent> {
        if (messageId == ROLE_POST_ID) {
            val role = Role.values.find { it.reactionEmoji == emoji }
            if (role != null) {
                userAsMember?.removeRole(role.id)
                println("${user.asUser().username} is no more $role")
            }
        }
    }

    kord.on<MessageCreateEvent> {
        val content = message.content
        if (content == "!ping") {
            val roles = member?.roles?.mapNotNull { Role.byIdOrNull(it.id) }?.toList() ?: emptyList()
            val responseText = roles.joinToString("") { it.reactionEmoji.name } + "Pong!"
            val response = message.channel.createMessage(responseText)
            response.addReaction(pingPong)
            delay(5.seconds)
            message.delete()
            response.delete()
        }
    }
    kord.login {
        presence {
            playing("!ping pong!")
        }
        intents += Intent.MessageContent
    }
}

const val TOKEN = "OTY5NTk1MjM4NjU2MTQzNDQz.GiLta8.K8rIebdolBAlRqg0SsKVuZhtO7ovqiKZITfSVY"
val ROLE_POST_ID = Snowflake(970029782060367962)
val ROLES_CHANNEL_ID = Snowflake(970029412873560115)