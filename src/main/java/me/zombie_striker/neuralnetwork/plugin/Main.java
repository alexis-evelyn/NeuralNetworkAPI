package me.zombie_striker.neuralnetwork.plugin;

/**
 Copyright (C) 2017  Zombie_Striker

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/

// Java Imports
import java.io.File;

// Neural Network Imports
import me.zombie_striker.neuralnetwork.*;
import me.zombie_striker.neuralnetwork.neurons.*;
import me.zombie_striker.neuralnetwork.neurons.input.*;
import me.zombie_striker.neuralnetwork.plugin.commands.CommandNeuralNetwork;
import me.zombie_striker.neuralnetwork.plugin.updater.GithubDependDownloader;
import me.zombie_striker.neuralnetwork.plugin.updater.GithubUpdater;
import me.zombie_striker.neuralnetwork.senses.*;

// Bukkit Imports
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

// Bstats
import org.bstats.bukkit.Metrics;

// Demo Neural Networks
import me.zombie_striker.neuralnetwork.plugin.demo.blackjack_helper.BlackJackHelper;
import me.zombie_striker.neuralnetwork.plugin.demo.bot_guesser.BotGuesser;
import me.zombie_striker.neuralnetwork.plugin.demo.logical.*;
import me.zombie_striker.neuralnetwork.plugin.demo.music_bot.MusicBot;
import me.zombie_striker.neuralnetwork.plugin.demo.number_adder.NumberAdder;
import me.zombie_striker.neuralnetwork.plugin.demo.prime_number_guesser.PrimeNumberBot;
import me.zombie_striker.neuralnetwork.plugin.demo.swearfilter.SwearBot;

public class Main extends JavaPlugin implements Listener {
	public FileConfiguration config;
	protected boolean enableMetrics = true;

	@Deprecated
	protected static Main plugin;

	CommandNeuralNetwork classNeuralNetwork;

	/**
	 * This class is used to make a Neural Network figure out whether a username
	 * is valid
	 */
	public void onLoad() {
		ConfigurationSerialization.registerClass(NNBaseEntity.class);
		ConfigurationSerialization.registerClass(NNAI.class);
		ConfigurationSerialization.registerClass(Layer.class);
		ConfigurationSerialization.registerClass(Senses.class);
		ConfigurationSerialization.registerClass(Controler.class);

		ConfigurationSerialization.registerClass(Senses2D.class);
		ConfigurationSerialization.registerClass(Senses3D.class);
		ConfigurationSerialization.registerClass(Sensory2D_Booleans.class);
		ConfigurationSerialization.registerClass(Sensory2D_Letters.class);
		ConfigurationSerialization.registerClass(Sensory2D_Numbers.class);
		ConfigurationSerialization.registerClass(Sensory3D_Booleans.class);
		ConfigurationSerialization.registerClass(Sensory3D_Numbers.class);

		ConfigurationSerialization.registerClass(Neuron.class);
		ConfigurationSerialization.registerClass(InputNeuron.class);
		ConfigurationSerialization.registerClass(InputBlockNeuron.class);
		ConfigurationSerialization.registerClass(InputBooleanNeuron.class);
		ConfigurationSerialization.registerClass(InputLetterNeuron.class);
		ConfigurationSerialization.registerClass(InputNumberNeuron.class);
		ConfigurationSerialization.registerClass(InputTickNeuron.class);
		ConfigurationSerialization.registerClass(OutputNeuron.class);
		ConfigurationSerialization.registerClass(BiasNeuron.class);

		ConfigurationSerialization.registerClass(LogicalAND.class);
		ConfigurationSerialization.registerClass(LogicalOR.class);
		ConfigurationSerialization.registerClass(LogicalXOR.class);
		ConfigurationSerialization.registerClass(LogicalXNOR.class);
		ConfigurationSerialization.registerClass(LogicalNAND.class);
		ConfigurationSerialization.registerClass(LogicalInverted.class);
		ConfigurationSerialization.registerClass(LogicalNOR.class);

		ConfigurationSerialization.registerClass(BlackJackHelper.class);
		ConfigurationSerialization.registerClass(NumberAdder.class);
		ConfigurationSerialization.registerClass(BotGuesser.class);
		ConfigurationSerialization.registerClass(PrimeNumberBot.class);
		ConfigurationSerialization.registerClass(MusicBot.class);
		ConfigurationSerialization.registerClass(SwearBot.class);
	}

	@Override
	public void onEnable() {
		// Friendly To Using A Class Per Comamnd
		PluginCommand commandNeuralNetwork = this.getCommand("neuralnetwork");
		classNeuralNetwork = new CommandNeuralNetwork(this);

		try {
			commandNeuralNetwork.setExecutor(classNeuralNetwork);
		}  catch(NullPointerException e) {
			getLogger().severe("Could Not Register NeuralNetwork Command!!! Failed to Set Executor!!!");
			e.getStackTrace();
		}

		try {
			commandNeuralNetwork.setTabCompleter(classNeuralNetwork);
		}  catch(NullPointerException e) {
			getLogger().severe("Could Not Register NeuralNetwork Command!!! Failed to Set Tab Completer!!!");
			e.getStackTrace();
		}

		// Set bStats Default Variable if Not Already Set
		this.config = this.getConfig();
		this.config.addDefault("bstats.enable", true);
		this.config.options().copyDefaults();

		// Save Config As Is
		saveConfig();

		this.enableMetrics = this.config.getBoolean("bstats.enable");

		checkForUpdate();

		if (this.enableMetrics) {
			/**
			 * I use bStats metrics to monitor how many servers are using my
			 * API. This does not send any personal/private information. This
			 * only sends:
			 *
			 * the server version, Java version,
			 * the plugin's version,
			 * system architecture,
			 * Core count,
			 *
			 * You can view the stats being collected at:
			 * https://bstats.org/plugin/bukkit/NeuralNetworkAPI/6651
			 */

			/* Note For Zombie_Striker, I only changed the bStat id so my changes wouldn't interfere with your data.
			 * If you would like, feel free to insert your id, 1712, back into the plugin's code.
			 * UNLESS YOU ARE ZOMBIE_STRIKER, DO NOT CHANGE THE ID TO 1712!!!
			 */

			// bStats Enabling Code
			int pluginId = 6651;
			Metrics metrics = new Metrics(this, pluginId);

			// Optional: Add custom charts
			// metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
		}
	}

	@Override
	public void onDisable() {
		classNeuralNetwork.onDisable();
	}

	private void checkForUpdate() {
		// TODO: Change Updater to Alexis' Repositories

		/**
		 * Everyone should want the most up to date version of the plugin, so
		 * any improvements made (either with performance or through new
		 * methods) should be welcome. Since it is rare that I will remove
		 * anything, and even if I did, I would deprecate the methods for a long
		 * period of time before I do, nothing should really break.
		 */

		//new Updater(this, 280241);
		GithubUpdater.autoUpdate(this, "ZombieStriker","NeuralNetworkAPI","NeuralNetworkAPI.jar");

		if (Bukkit.getPluginManager().getPlugin("PluginConstructorAPI") == null)
			// new DependencyDownloader(this, 276723);
			GithubDependDownloader.autoUpdate(this,
					new File(getDataFolder().getParentFile(), "PluginConstructorAPI.jar"), "ZombieStriker",
					"PluginConstructorAPI", "PluginConstructorAPI.jar");
	}

	@Deprecated
	public static Main getMainClass() {
		return plugin;
	}
}