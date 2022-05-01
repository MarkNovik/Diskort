import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking { kordMain() }

@OptIn(PrivilegedIntent::class)
suspend fun kordMain() = Kord(TOKEN).run kord@{
    val pingPong = ReactionEmoji.Unicode("""🏓""")

    on<ReactionAddEvent> {
        if (messageId == ROLE_POST_ID && message.channelId == ROLES_CHANNEL_ID) {
            val role = Role.values.find { it.reactionEmoji == emoji }
            if (role != null) {
                userAsMember?.addRole(role.id)
                println("${user.asUser().username} is now $role")
            }
        }
    }

    on<ReactionRemoveEvent> {
        if (messageId == ROLE_POST_ID && message.channelId == ROLES_CHANNEL_ID) {
            val role = Role.values.find { it.reactionEmoji == emoji }
            if (role != null) {
                userAsMember?.removeRole(role.id)
                println("${user.asUser().username} is no more $role")
            }
        }
    }

    commands {
        on("!ping") {
            val roles = member?.roles?.mapNotNull { Role.byIdOrNull(it.id) }?.toList() ?: emptyList()
            val response = if (Role.Ponger in roles) {
                val response = message.channel.createMessage("Pong!")
                response.addReaction(pingPong)
                response
            } else {
                message.channel.createMessage("Only Pongers can play ping pong!")
            }
            delay(5.seconds)
            message.delete()
            response.delete()
        }

        on("!myroles") {
            val username = message.author?.username
            val roles = member?.roles?.mapNotNull { Role.byIdOrNull(it.id) }?.toList() ?: emptyList()
            val responseText = "Hello, $username. You are" + when {
                roles.isEmpty() -> " just a common user."
                roles.size == 1 -> " a ${roles.single()}"
                else -> roles.dropLast(1).joinToString(
                    prefix = " ",
                    separator = ", "
                ) { "a " + it.name } + " and a ${roles.last()}! Impressive!"
            }
            val response = message.channel.createMessage(responseText)
            roles.forEach { response.addReaction(it.reactionEmoji) }
            delay(5.seconds)
            message.delete()
            response.delete()
        }
    }

    login {
        presence {
            playing("!ping pong!")
        }
        intents += Intent.MessageContent
    }
}