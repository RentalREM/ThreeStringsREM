
//Vincent Banks
//PlayerManager Class
//COPYRIGHT Vincent Banks
package ThreeStrings.lavaplayer;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
public class PlayerManager {
    private static PlayerManager INSTANCE;
    public final Map<Long, GuildMusicManager> musicManagers;
    public final AudioPlayerManager audioPlayerManager;
    private final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);
    public PlayerManager() {
        this.musicManagers = new HashMap<>(); //creates hashmap for musicManagers
        this.audioPlayerManager = new DefaultAudioPlayerManager(); //sets audioPlayerManager to default
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager); //registers sources for default
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }
    public GuildMusicManager getMusicManager(Guild guild){   //tells JDA what to use when sending audio
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }
    public void LoadAndPlay(TextChannel channel, String trackUrl){ //create a load and play method with lava player methods
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild()); //get discord channel
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track); //load track into queue or play it if none is currently playing
                //sends message to chat and appends info about track
                channel.sendMessage("Adding to queue: `") //sends message to chat and appends info about track
                        .append(track.getInfo().title)
                        .append("` by `")
                        .append(track.getInfo().author)
                        .queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                channel.sendMessage("Adding to queue: `") //sends message to chat and appends info about track
                        .append(String.valueOf(tracks.size()))
                        .append("` tracks from playlist `")
                        .append(playlist.getName())
                        .queue();
                for (final AudioTrack track : tracks){
                    musicManager.scheduler.queue(track);
                    final AudioTrackInfo info = track.getInfo(); //create variable for getting track info
                    if (musicManager.scheduler.queue.isEmpty()){ //if there is nothing in queue this if statement will run
                        channel.sendMessageFormat("Now playing:`%s` by `%s`" + " Duration: " + formatTime(track.getDuration()) +
                                " Link: <%s>)", info.title, info.author, info.uri).queue();
                    }
                }
            }
            private String formatTime(long timeInMillis) { //create formatTime method
                final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
                final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
                final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1)/ TimeUnit.SECONDS.toMillis(1);

                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
            @Override
            public void noMatches() {
                LOGGER.error("No matches for link");
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                LOGGER.error("Failed to load song!\n" + exception);
            }
        });
    }
    // for playing only one track when searching with plain text code is all the same until final playing moment
    public void LoadAndPlayOnce(TextChannel channel, String trackUrl){ //create a load and play method with lava player methods
        final GuildMusicManager musicManagerSingle = this.getMusicManager(channel.getGuild()); //get discord channel
        this.audioPlayerManager.loadItemOrdered(musicManagerSingle, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManagerSingle.scheduler.queue(track); //load track into queue or play it if none is currently playing
                channel.sendMessage("Adding to queue: `") //sends message to chat and appends info about track
                        .append(track.getInfo().title)
                        .append("` by ")
                        .append(track.getInfo().author)
                        .queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                channel.sendMessage("playing ") //sends message to chat and appends info about track
                        .append(" tracks from ")
                        .append(playlist.getName())
                        .queue();
                final AudioTrack track = tracks.get(0); //takes the first result
                musicManagerSingle.scheduler.queue(track); //queues track
                channel.sendMessage("Adding to queue: `") //sends message to chat and appends info about track
                        .append(track.getInfo().title)
                        .append("` by ")
                        .append(track.getInfo().author)
                        .queue();
                final AudioTrackInfo info = track.getInfo(); //create variable for getting track info
                if (musicManagerSingle.scheduler.queue.isEmpty()){ //if there is nothing in queue this if statement will run
                    channel.sendMessageFormat("Now playing:`%s` by `%s`" + " Duration: " + formatTime(track.getDuration()) +
                            " (Link: <%s>)", info.title, info.author, info.uri).queue();
                }
            }
            private String formatTime(long timeInMillis) { //create formatTime method
                final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
                final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
                final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1)/ TimeUnit.SECONDS.toMillis(1);

                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
            @Override
            public void noMatches() {
                LOGGER.error("No matches for link");
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                LOGGER.error("Failed to load song!\n" + exception);
            }
        });
    }
    //
    public static PlayerManager getInstance(){ //if there is no instance this if statement will run
        if(INSTANCE == null){
            INSTANCE = new PlayerManager();
        }
        return INSTANCE; //returns the instance in this case a player manager
    }
}
