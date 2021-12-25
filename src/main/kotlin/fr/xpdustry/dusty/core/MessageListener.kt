package fr.xpdustry.dusty.core

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter


class MessageListener : ListenerAdapter() {
    private val listeners = HashMap<Regex, MessageHandler>()

    fun onMessage(regex: Regex, vararg replies: String) =
        onMessage(regex, ReplyingMessageHandler(arrayOf(*replies)))

    fun onMessage(regex: Regex, handler: MessageHandler) =
        listeners.put(regex, handler)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.author.isSystem) return
        listeners.forEach { (r, c) -> if (r.matches(event.message.contentRaw)) c.handleMessage(event) }
    }

    fun interface MessageHandler {
        fun handleMessage(event: MessageReceivedEvent)
    }

    open class ReplyingMessageHandler(private val replies: Array<String>) : MessageHandler {
        override fun handleMessage(event: MessageReceivedEvent) {
            event.channel.sendMessage(replies.random()).queue()
        }

        init {
            if (replies.isEmpty()) throw IllegalArgumentException("replies is empty.")
        }
    }
}