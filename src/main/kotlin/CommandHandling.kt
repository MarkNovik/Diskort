import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

inline fun Kord.commands(
    crossinline consumer: suspend CommandsContext.() -> Unit
) = on<MessageCreateEvent> {
    val context = CommandsContext()
    consumer(context)
    for (command in context.commands) {
        if (message.content == command.command) {
            command.action(this)
        }
    }
}

class CommandsContext {
    val commands = mutableSetOf<Command>()
    fun on(command: String, action: suspend MessageCreateEvent.() -> Unit) {
        commands += Command(command, action)
    }
}
data class Command(
    val command: String,
    val action: suspend (MessageCreateEvent) -> Unit
)