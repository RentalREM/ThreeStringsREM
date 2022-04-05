//Vincent Banks
//Listener class
//COPYRIGHT Vincent Banks
package ThreeStrings;
import ThreeStrings.Database.MONGODB;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Listener  extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class); //implement the Logger class to get rid of error messages
    private final CommandManager manager; //implement our command manager class
    public Listener(EventWaiter waiter){
        manager = new CommandManager(waiter);
    }
    @Override
    public void onReady(@NotNull ReadyEvent event) {   //implement the onReady JDA constructor
        LOGGER.info("ThreeStrings ready to play!"); //tells user that bot is ready to play
    }
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) { //implementing discord message received constructor
        User user = event.getAuthor(); //defines user variable
        if (user.isBot() || event.isWebhookMessage() ){ //if user is a bot or webhook message we simply just return
            return;
        }
        String prefix = Config.get("PREFIX"); //setting prefix to what it is in the database
        String raw = event.getMessage().getContentRaw();
        if (raw.equalsIgnoreCase(prefix + "shutdown") && user.getId().equals(Config.get("OWNER_ID"))){ //using boolean we can create a !shutodwn command that only the owner can use with owner id in config
            LOGGER.info("Heading in for the night! Thanks for listening!"); //sends message to user that bot is shutting down
            event.getJDA().shutdown();  //bot shuts down
            BotCommons.shutdown(event.getJDA());
            return;
        }
        if (raw.startsWith(prefix)){  //when a command is executed this if statements tells the command manager class to handle it
            manager.handle(event);
        }
        MONGODB mongo = new MONGODB();
        long discordId = event.getAuthor().getIdLong();
        String defaultRoom = "<:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506>" +
                "<:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506>" +
                "<:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506>" +
                "<:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506>" +
                "<:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506><:woodtile:940727196157374506>";
        Document defaultDoc = new Document("id",discordId).append("room", defaultRoom).append("cash","100");
        if(!mongo.checkIfExists(defaultDoc)){//if document is not already in database
            try{
                mongo.insert(defaultDoc);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    }
