//Vincent Banks
//CreateRoomCommand Class
//COPYRIGHT Vincent Banks
package ThreeStrings.Rooms;
import ThreeStrings.Config;
import ThreeStrings.Database.MemberMongo;
import ThreeStrings.command.CommandContext;
import ThreeStrings.command.ICommand;
import org.bson.Document;
import java.util.List;
public final class CreateRoomCommand implements ICommand {
    List<String> defaultRoom;
    public CreateRoomCommand(){
        defaultRoom = List.of(
                Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"),"\n",
                Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"),"\n",
                Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"),"\n",
                Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"),"\n",
                Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"), Config.get("WOOD_TILE"),"\n"
        ); //create default room
    }
    @Override
    public void handle(CommandContext ctx) {
        final MemberMongo mongo = new MemberMongo();
        long discordId = ctx.getAuthor().getIdLong(); //pull user's discord id as long value
        Document check = new Document("id",discordId); //create default doc to check if user exists in db
        List<String> inventory = List.of(" ");
        Document defaultDoc = new Document("id", discordId)
                .append("room", defaultRoom)
                .append("dragons", 100L)
                .append("inventory",inventory)
                .append("goldstars",0)
                .append("points",0);
        if (!mongo.checkIfExists(check)) { // if document doest exist in database
            try {
                mongo.insert(defaultDoc);
                ctx.getChannel().sendMessage("Alright I have a spot open for ya!\n" +
                        "I have registered " + ctx.getAuthor().getName() + " into the tavern!\n" +
                        "Here's a 100 Dragons to get you off your feet.").queue();
            } catch (Exception e) {
                ctx.getChannel().sendMessage("I uhh dont feel to good...\n" + e).queue();
                e.printStackTrace();
            }
        } else {
            ctx.getChannel().sendMessage("You already have a spot in the tavern!").queue();
        }
    }
        @Override
        public String getName () {
            return "createroom";
        }
        @Override
        public String getHelp () {
            return "Creates a tavern room for you to customize";
        }
    @Override
    public String getType() {
        return "rooms";
    }
}

