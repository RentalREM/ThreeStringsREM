//Vincent Banks
//VoiceListener Subclass
//COPYRIGHT Vincent Banks
package ThreeStrings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
public class VoiceListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class); //implement the Logger class to get rid of error messages
    @Override
    public void onReady(@NotNull ReadyEvent event) {   //implement the onReady JDA constructor
        LOGGER.info("ThreeStrings eavesdropping on tables!"); //tells user that bot is ready to play
    }
    //Disconnect bot if no users is in vc from disconnecting from server
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event){
        VoiceChannel channel = event.getChannelLeft();
        List<Member> membersInVc = channel.getMembers();
        if(membersInVc.size() == 1 && membersInVc.get(0).getUser().isBot()){
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }
    //Disconnect bot if no users is in vc from moving to another channel
    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event){
        VoiceChannel channel = event.getChannelLeft();
        List<Member> membersInVc = channel.getMembers();
        if(membersInVc.size() == 1 && membersInVc.get(0).getUser().isBot()){
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}
