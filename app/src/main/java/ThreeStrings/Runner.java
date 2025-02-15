//Vincent Banks
//Runner Class
//COPYRIGHT Vincent Banks
package ThreeStrings;
import ThreeStrings.CommandLine.CommandLineManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;
//----Runner Class----
/*
This class instantiates all basic needs for ThreeStrings
Such as JDABuilder Manipulation (status,prefix, gateway intents,etc..)
And sets event listener
And then tells bot to log in
 */
//---------------------
public final class Runner {
    private Runner(){
        //make constructor private so no instances of runner class can be made
    }
    /**
     * Run builds all necessary objects for ThreeStrings to Run,
     * I.E, Sets listeners for discord, enables gateway intents, and logins to bot with token
     * @Throws LoginException
     * */
    public static void run() throws LoginException { //class throws LoginException if error happens while attempting to login
        JDABuilder ThreeStrings = JDABuilder.createDefault(Config.get("TOKEN")); //create new bot with token in config file
        ThreeStrings.setActivity(Activity.playing("the Lute!")); //set activity status
        ThreeStrings.setStatus(OnlineStatus.ONLINE); //set online status to online
        EventWaiter waiter = new EventWaiter(); //add event waiter class
        ThreeStrings.addEventListeners(new Listener(waiter), waiter); //allows for the commands class to function and lets bot listen for commands
        ThreeStrings.addEventListeners(new VoiceListener());
        ThreeStrings.setChunkingFilter(ChunkingFilter.ALL); //allows for ThreeStrings to see all members of a discord
        ThreeStrings.setMemberCachePolicy(MemberCachePolicy.ALL); //literally no idea
        ThreeStrings.enableIntents(GatewayIntent.GUILD_VOICE_STATES); //giving bot permission to view voice states
        ThreeStrings.enableCache(CacheFlag.VOICE_STATE); //enable voice state cache
        ThreeStrings.enableIntents(GatewayIntent.GUILD_MEMBERS); //giving self permission to view members
        ThreeStrings.build(); //tells bot to log in
        //create and run command line app
        CommandLineManager cmManager = new CommandLineManager();
        cmManager.start();
    }
}
