package fr.xpdustry.dusty

import cloud.commandframework.arguments.standard.IntegerArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.jda.JDA4CommandManager
import cloud.commandframework.jda.JDACommandSender
import cloud.commandframework.kotlin.extension.argumentDescription
import cloud.commandframework.kotlin.extension.buildAndRegister
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import fr.xpdustry.dusty.core.MessageListener
import fr.xpdustry.dusty.util.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.net.URL
import java.util.concurrent.Executors
import java.util.function.Function
import kotlin.random.Random

class Dusty(token: String) : ListenerAdapter() {
    private val jda: JDA

    val messages = MessageListener()
    val commands: JDA4CommandManager<JDACommandSender>
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        event.guild.systemChannel?.sendMessage("""
            Welcome to Scidustry, ${event.member.asMention}
            
            Scidustry focuses on logic inside of Mindustry. We have developed many gates and other logic things to build any circuit you can imagine.
            These gates can be found in <#663711036267954192>, <#663711159005872139>, and at various places in <#652253115042758678>.
            If you're here to see our finished projects they can be found in the <#651441687800971265> or <#651434234455785475> channels.
            If you want to start developing circuits of your own you can find some information in the pins of this channel, and the division specific ones.
            Once you prove yourself capable of logic you will be promoted to the division that fits your style the best.

            Always check pins before asking for help, but don't be afraid to. 

            AND FOLLOW THE DAMN RULES!
        """.trimIndent()
        )?.complete()
    }

    fun start() {
        jda.awaitReady()
    }

    init {
        try {
            jda = JDABuilder
                .createDefault(token)
                .addEventListeners(messages)
                .build()

            commands = JDA4CommandManager(
                jda, { "$" }, { _, _ -> true },
                AsynchronousCommandExecutionCoordinator.newBuilder<JDACommandSender>()
                    .withExecutor(Executors.newCachedThreadPool())
                    .build(),
                Function.identity(),
                Function.identity()
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to start JDA.", e)
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) throw IllegalArgumentException("You need to specify the token of Dusty...")

    val dusty = Dusty(args[0])

    with(dusty.messages) {
        onMessage(
            Regex("install debian", RegexOption.IGNORE_CASE),
            "Shut up ubutnu"
        )

        onMessage(
            Regex("grape", RegexOption.IGNORE_CASE),
            "My brother doesn't like grapes"
        )

        onMessage(
            Regex("graphite", RegexOption.IGNORE_CASE),
            "carbide"
        )

        onMessage(
            Regex("uwu|owo", RegexOption.IGNORE_CASE),
            "no", "stop", "please stop", "just no", "***s t o p***"
        )

        onMessage(
            Regex("pain and suffering", RegexOption.IGNORE_CASE),
            "main() and buffering"
        )

        onMessage(
            Regex("^(what|why)\\s*\\??$", RegexOption.IGNORE_CASE),
            "don't ask", "why not?"
        )

        onMessage(
            Regex("^ok$", RegexOption.IGNORE_CASE),
            ":ok_hand:"
        )

        onMessage(
            Regex("(indielm)+", RegexOption.IGNORE_CASE),
            "Indielm best server!"
        )

        onMessage(
            Regex("^lenny\\s*(face)?$", RegexOption.IGNORE_CASE),
            Fun.LENNY
        )
    }

    with(dusty.commands) {
        buildAndRegister("ping", argumentDescription("Replies \"Pong!\", nothing else."), arrayOf("p")) {
            handler { it.sender.channel.sendMessage("pong").queue() }
        }

        buildAndRegister("flip", argumentDescription("Flips a coin"), arrayOf("f")) {
            handler { it.sender.channel.sendMessage(arrayOf("Heads!", "Tails!").random()).queue() }
        }

        buildAndRegister("router", argumentDescription("Uses the :router: emoji a bunch of times.")) {
            handler { it.sender.channel.sendMessage(("<:router:924099269147906058>".repeat(5) + "\n").repeat(5)).queue() }
        }

        buildAndRegister("quote", argumentDescription("AI Inspirational quotes")) {
            handler { it.sender.channel.sendMessage(URL("https://inspirobot.me/api?generate=true").get()).queue() }
        }

        buildAndRegister("roll", argumentDescription("Rolls a dice, you can also guess numbers"), arrayOf("r")) {
            argument(IntegerArgument.newBuilder<JDACommandSender>("guess").withMin(0).withMax(6).asOptional())
            handler {
                val rand = Random.nextInt(7)
                val message = it.sender.channel.sendMessage("$rand!")

                if ("guess" in it) message.append(
                    if (rand == it["guess"]) " Nice, you guessed the number."
                    else " You didn't guess the number. Better luck next time."
                )

                message.queue()
            }
        }

        buildAndRegister("weebify", argumentDescription("Weebifies text"), arrayOf("w")) {
            argument(StringArgument.newBuilder<JDACommandSender>("text").greedy())
            handler {
                if (it.sender.channel.idLong == 652252853838151692L) {
                    it.sender.channel.sendMessage(
                        it.get<String>("text").replace('r', 'w').replace('l', 'w') + arrayOf(" OwO", " UwU", " :3").random()
                    ).queue()
                } else {
                    it.sender.channel.sendMessage("<:x:669668097745223730> No weeb shit outside of <#652252853838151692>!").queue()
                }
            }
        }

        buildAndRegister("say", argumentDescription("Make me say stuff")) {
            argument(StringArgument.newBuilder<JDACommandSender>("text").greedy())
            handler {
                val text: String = it["text"]
                if (Fun.containsMentions(text, MentionType.USER, MentionType.EVERYONE, MentionType.ROLE)) {
                    it.sender.channel.sendMessage("No").queue()
                } else {
                    it.sender.channel.sendMessage(text).queue()
                }
            }
        }

        buildAndRegister("xkcd", argumentDescription("Sends a random xkcd comic.")) {
            handler {
                val latest = dusty.gson.fromJson<JsonObject>(URL("https://xkcd.com/info.0.json").get())["num"].asInt
                val random = "https://xkcd.com/" + (0..latest).random()
                val xkcd = dusty.gson.fromJson<JsonObject>(URL("$random/info.0.json").get())

                it.sender.channel.sendMessageEmbeds(EmbedBuilder().apply {
                    setTitle(xkcd["title"].asString, random)
                    setImage(xkcd["img"].asString)
                    setFooter(xkcd["alt"].asString)
                }.build()).queue()
            }
        }

        buildAndRegister("minesweeper", argumentDescription("Creates a minesweeper grid of spoilers")) {
            argument(IntegerArgument.newBuilder<JDACommandSender>("x").withMin(4).withMax(8).asOptionalWithDefault(8))
            argument(IntegerArgument.newBuilder<JDACommandSender>("y").withMin(4).withMax(8).asOptionalWithDefault(8))
            argument(IntegerArgument.newBuilder<JDACommandSender>("n").withMin(4).withMax(8 * 8).asOptional())
            handler {
                val x: Int = it["x"]
                val y: Int = it["y"]
                val n: Int = it.getOrDefault("n", (x * y) / 5)!!
                val grid: Matrix

                try {
                    grid = Fun.minesweeper(x, y, n)
                } catch (e: IllegalArgumentException) {
                    it.sender.channel.sendMessage(e.message!!).queue()
                    return@handler
                }

                val builder = StringBuilder(7 * x * y)
                for (row in grid) {
                    for (cell in row) builder.append("||`${if (cell >= 0) cell else "X"}`||")
                    builder.append('\n')
                }

                it.sender.channel.sendMessage(builder).queue()
            }
        }

        buildAndRegister("help", argumentDescription("Display some help."), arrayOf("h")){
            handler {
                it.sender.channel.sendMessageEmbeds(EmbedBuilder().apply {
                    setTitle("Dusty, the Scidustry Discord Bot")
                    setThumbnail(jda.selfUser.avatarUrl ?: jda.selfUser.defaultAvatarUrl)
                    it.sender.event.ifPresent {e -> setColor(e.guild.selfMember.color) }
                    for (command in commands) {
                        addField(
                            commandSyntaxFormatter.apply(command.arguments, null),
                            command.components[0].argumentDescription.description,
                            false
                        )
                    }
                }.build()).queue()
            }
        }
    }

    dusty.start()
}
