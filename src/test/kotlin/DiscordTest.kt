import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.properties.Delegates


class DiscordTest {

    private var jda: JDA by Delegates.notNull()

    @Before
    fun setupBot() {
//        val manager = ReactiveEventManager()
//
//        jda = JDABuilder
//            .createDefault("ODUwODA2NzM0MTAzNTExMDUx.YLvFhA.LYqkwvn3AhzotVnBWP5S5ZqTrto")
//            .setEnabledIntents(EnumSet.allOf(GatewayIntent::class.java))
//            .setEventManager(manager)
//            .build()
//            .awaitReady()
    }

    @After
    fun disposeBot() {
//        jda.shutdownNow()
    }

    @Test
    fun test() {
//        val guild = jda.getGuildById(726835949576650852)!!
//        println(guild)
//        jda.guilds.forEach {
//            println(it.id)
//            println(it.name)
//        }
//
//        println("finding members")
//        val member = guild.findMembers { it.user.asTag == "Oskang09#4170"}.get()
//        println(member)

//        jda.on<MessageReactionAddEvent>().subscribe {
//            println(it.reactionEmote.asCodepoints)
//        }
//        println("listening emote event")
//
//        runBlocking {
//            delay(30000L)
//        }
    }

}