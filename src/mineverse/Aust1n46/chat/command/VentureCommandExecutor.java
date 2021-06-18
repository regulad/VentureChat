package mineverse.Aust1n46.chat.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.command.chat.Broadcast;
import mineverse.Aust1n46.chat.command.chat.BungeeToggle;
import mineverse.Aust1n46.chat.command.chat.Channel;
import mineverse.Aust1n46.chat.command.chat.Channelinfo;
import mineverse.Aust1n46.chat.command.chat.Chatinfo;
import mineverse.Aust1n46.chat.command.chat.Chatreload;
import mineverse.Aust1n46.chat.command.chat.Chlist;
import mineverse.Aust1n46.chat.command.chat.Chwho;
import mineverse.Aust1n46.chat.command.chat.Clearchat;
import mineverse.Aust1n46.chat.command.chat.Commandblock;
import mineverse.Aust1n46.chat.command.chat.Commandspy;
import mineverse.Aust1n46.chat.command.chat.Config;
import mineverse.Aust1n46.chat.command.chat.Edit;
import mineverse.Aust1n46.chat.command.chat.Filter;
import mineverse.Aust1n46.chat.command.chat.Force;
import mineverse.Aust1n46.chat.command.chat.Forceall;
import mineverse.Aust1n46.chat.command.chat.Kickchannel;
import mineverse.Aust1n46.chat.command.chat.Kickchannelall;
import mineverse.Aust1n46.chat.command.chat.Leave;
import mineverse.Aust1n46.chat.command.chat.Listen;
import mineverse.Aust1n46.chat.command.chat.Me;
import mineverse.Aust1n46.chat.command.chat.Nick;
import mineverse.Aust1n46.chat.command.chat.Party;
import mineverse.Aust1n46.chat.command.chat.RangedSpy;
import mineverse.Aust1n46.chat.command.chat.Removemessage;
import mineverse.Aust1n46.chat.command.chat.Setchannel;
import mineverse.Aust1n46.chat.command.chat.Setchannelall;
import mineverse.Aust1n46.chat.command.chat.VentureChatGui;
import mineverse.Aust1n46.chat.command.chat.Venturechat;
import mineverse.Aust1n46.chat.command.message.Ignore;
import mineverse.Aust1n46.chat.command.message.Message;
import mineverse.Aust1n46.chat.command.message.MessageToggle;
import mineverse.Aust1n46.chat.command.message.Notifications;
import mineverse.Aust1n46.chat.command.message.Reply;
import mineverse.Aust1n46.chat.command.message.Spy;
import mineverse.Aust1n46.chat.command.mute.Mute;
import mineverse.Aust1n46.chat.command.mute.Muteall;
import mineverse.Aust1n46.chat.command.mute.Unmute;
import mineverse.Aust1n46.chat.command.mute.Unmuteall;

/**
 * Class that initializes and executes the plugin's commands.
 */
public class VentureCommandExecutor implements TabExecutor {
	private static Map<String, VentureCommand> commands = new HashMap<String, VentureCommand>();
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static VentureCommandExecutor commandExecutor;
	
	private static final List<String> EXCLUDED_COMMANDS = Collections.unmodifiableList(Arrays.asList("ch", "r", "venturechat"));
	
	private static Field commandMapField;
	private static CommandMap commandMap;
	private static Field knownCommands;
	private static Constructor<PluginCommand> pluginCommand;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] parameters) {
		System.out.println(command.getName());
		commands.get(command.getName()).execute(sender, command.getName(), parameters);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onTabComplete(sender, command, label, args);
	}
	
	private static void loadCommandMap() {
		try {
			commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommands.setAccessible(true);
			pluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			pluginCommand.setAccessible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void register(String commandName, CommandExecutor commandExecutor) {
		try {
			PluginCommand command = (PluginCommand) pluginCommand.newInstance(commandName, plugin);
			command.setExecutor(commandExecutor);
			commandMap.register(plugin.getName(), command);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void initialize() {
		loadCommandMap();
		List<String> disabledCommands = plugin.getConfig().getStringList("disabled_commands");
		commandExecutor = new VentureCommandExecutor();
		commands.put("broadcast", new Broadcast());
		VentureCommand channel = new Channel();
		commands.put("ch", channel);
		commands.put("channel", channel);
		commands.put("join", channel);
		commands.put("channelinfo", new Channelinfo());
		commands.put("chatinfo", new Chatinfo());
		commands.put("chatreload", new Chatreload());
		commands.put("chlist", new Chlist());
		commands.put("chwho", new Chwho());
		commands.put("clearchat", new Clearchat());
		commands.put("commandblock", new Commandblock());
		commands.put("commandspy", new Commandspy());
		commands.put("config", new Config());
		commands.put("edit", new Edit());
		commands.put("filter", new Filter());
		commands.put("force", new Force());
		commands.put("forceall", new Forceall());
		commands.put("kickchannel", new Kickchannel());
		commands.put("kickchannelall", new Kickchannelall());
		commands.put("leave", new Leave());
		commands.put("listen", new Listen());
		commands.put("me", new Me());
		commands.put("venturechat", new Venturechat());
		commands.put("setnickname", new Nick());
		commands.put("notify", new Notifications());
		commands.put("party", new Party());
		commands.put("rangedspy", new RangedSpy());
		commands.put("removemessage", new Removemessage());
		commands.put("setchannel", new Setchannel());
		commands.put("setchannelall", new Setchannelall());
		commands.put("spy", new Spy());
		VentureCommand ventureChatGui = new VentureChatGui();
		commands.put("venturechatgui", ventureChatGui);
		commands.put("vchatgui", ventureChatGui);
		commands.put("messagetoggle", new MessageToggle());
		commands.put("bungeetoggle", new BungeeToggle());
		VentureCommand reply = new Reply();
		commands.put("reply", reply);
		commands.put("r", reply);
		commands.put("mute", new Mute());
		commands.put("muteall", new Muteall());
		commands.put("unmute", new Unmute());
		commands.put("unmuteall", new Unmuteall());
		VentureCommand message = new Message();
		commands.put("message", message);
		commands.put("msg", message);
		commands.put("tell", message);
		commands.put("whisper", message);
		commands.put("pm", message);
		commands.put("ignore", new Ignore());
		Map<String, VentureCommand> vCommandAliases = new HashMap<String, VentureCommand>();
		for(String command : commands.keySet()) {
			if(!disabledCommands.contains(command)) {
				register(command, commandExecutor);
				if(!EXCLUDED_COMMANDS.contains(command)) {
					vCommandAliases.put("v" + command, commands.get(command));
					register("v" + command, commandExecutor);
				}
			}
		}
		commands.putAll(vCommandAliases);
		Bukkit.getScheduler().runTask(plugin, () -> {
			for(String command : commands.keySet()) {
				if(!disabledCommands.contains(command)) {
					register(command, commandExecutor);
				}
			}
		});
	}
}
