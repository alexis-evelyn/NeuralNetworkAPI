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
import java.util.ArrayList;

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
import org.bukkit.plugin.PluginDescriptionFile;
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
	protected boolean enableMetrics = true;

	@Deprecated
	protected static Main plugin;

	ArrayList<Class<? extends NNBaseEntity>> demos = new ArrayList();

	CommandNeuralNetwork classNeuralNetwork;

	@Override
	public void onEnable() {
		// Register Classes For YAML Loading/Saving
		registerClasses();

		// Register List of Demos
		registerDemos();

		// Friendly To Using A Class Per Command
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

		// Save Default Config If Not Saved Yet
		saveDefaultConfig();

		// Set Dynamically Generated Default Variables If Not Already Set
		FileConfiguration config = getConfig();

		// Get Current Plugin Info
		PluginDescriptionFile pdf = getDescription(); // Gets plugin.yml

		config.addDefault("version", pdf.getVersion());

		config.options().copyDefaults(true);
		saveConfig();

		this.enableMetrics = config.getBoolean("bstats.enable");

		getLogger().info(ChatColor.GOLD + "" + ChatColor.BOLD + "Bstats Enabled: " + this.enableMetrics);

		checkForUpdate();

		if (this.enableMetrics) {
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
		unregisterClasses();
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

	private void registerDemos() {
		// me.zombie_striker.neuralnetwork.plugin.demo.logical.*;
		this.demos.add(LogicalAND.class);
		this.demos.add(LogicalOR.class);
		this.demos.add(LogicalXOR.class);
		this.demos.add(LogicalXNOR.class);
		this.demos.add(LogicalNAND.class);
		this.demos.add(LogicalInverted.class);
		this.demos.add(LogicalNOR.class);

		// me.zombie_striker.neuralnetwork.plugin.demo.*;
		this.demos.add(BlackJackHelper.class);
		this.demos.add(NumberAdder.class);
		this.demos.add(BotGuesser.class);
		this.demos.add(PrimeNumberBot.class);
		this.demos.add(MusicBot.class);
		this.demos.add(SwearBot.class);
	}

	// TODO: REMOVE ME IN FAVOR OF ConfigurationSerialization??? Was Zombie_Striker's wishes. Not Sure if Possible to Do!!!
	// https://bukkit.org/threads/configurationserializable-and-how-to-use-it.271862/
	public ArrayList<Class<? extends NNBaseEntity>> getDemos() {
		return this.demos;
	}

	private void registerClasses() {
		// me.zombie_striker.neuralnetwork.*;
		ConfigurationSerialization.registerClass(NNBaseEntity.class);
		ConfigurationSerialization.registerClass(NNAI.class);
		ConfigurationSerialization.registerClass(Layer.class);
		ConfigurationSerialization.registerClass(Senses.class);
		ConfigurationSerialization.registerClass(Controller.class);

		// me.zombie_striker.neuralnetwork.senses.*;
		ConfigurationSerialization.registerClass(Senses2D.class);
		ConfigurationSerialization.registerClass(Senses3D.class);
		ConfigurationSerialization.registerClass(Sensory2D_Booleans.class);
		ConfigurationSerialization.registerClass(Sensory2D_Letters.class);
		ConfigurationSerialization.registerClass(Sensory2D_Numbers.class);
		ConfigurationSerialization.registerClass(Sensory3D_Booleans.class);
		ConfigurationSerialization.registerClass(Sensory3D_Numbers.class);

		// me.zombie_striker.neuralnetwork.neurons.*;
		ConfigurationSerialization.registerClass(Neuron.class);
		ConfigurationSerialization.registerClass(InputNeuron.class);
		ConfigurationSerialization.registerClass(InputBlockNeuron.class);
		ConfigurationSerialization.registerClass(InputBooleanNeuron.class);
		ConfigurationSerialization.registerClass(InputLetterNeuron.class);
		ConfigurationSerialization.registerClass(InputNumberNeuron.class);
		ConfigurationSerialization.registerClass(InputTickNeuron.class);
		ConfigurationSerialization.registerClass(OutputNeuron.class);
		ConfigurationSerialization.registerClass(BiasNeuron.class);

		// me.zombie_striker.neuralnetwork.plugin.demo.logical.*;
		ConfigurationSerialization.registerClass(LogicalAND.class);
		ConfigurationSerialization.registerClass(LogicalOR.class);
		ConfigurationSerialization.registerClass(LogicalXOR.class);
		ConfigurationSerialization.registerClass(LogicalXNOR.class);
		ConfigurationSerialization.registerClass(LogicalNAND.class);
		ConfigurationSerialization.registerClass(LogicalInverted.class);
		ConfigurationSerialization.registerClass(LogicalNOR.class);

		// me.zombie_striker.neuralnetwork.plugin.demo.*;
		ConfigurationSerialization.registerClass(BlackJackHelper.class);
		ConfigurationSerialization.registerClass(NumberAdder.class);
		ConfigurationSerialization.registerClass(BotGuesser.class);
		ConfigurationSerialization.registerClass(PrimeNumberBot.class);
		ConfigurationSerialization.registerClass(MusicBot.class);
		ConfigurationSerialization.registerClass(SwearBot.class);
	}

	private void unregisterClasses() {
		// me.zombie_striker.neuralnetwork.*;
		ConfigurationSerialization.unregisterClass(NNBaseEntity.class);
		ConfigurationSerialization.unregisterClass(NNAI.class);
		ConfigurationSerialization.unregisterClass(Layer.class);
		ConfigurationSerialization.unregisterClass(Senses.class);
		ConfigurationSerialization.unregisterClass(Controller.class);

		// me.zombie_striker.neuralnetwork.senses.*;
		ConfigurationSerialization.unregisterClass(Senses2D.class);
		ConfigurationSerialization.unregisterClass(Senses3D.class);
		ConfigurationSerialization.unregisterClass(Sensory2D_Booleans.class);
		ConfigurationSerialization.unregisterClass(Sensory2D_Letters.class);
		ConfigurationSerialization.unregisterClass(Sensory2D_Numbers.class);
		ConfigurationSerialization.unregisterClass(Sensory3D_Booleans.class);
		ConfigurationSerialization.unregisterClass(Sensory3D_Numbers.class);

		// me.zombie_striker.neuralnetwork.neurons.*;
		ConfigurationSerialization.unregisterClass(Neuron.class);
		ConfigurationSerialization.unregisterClass(InputNeuron.class);
		ConfigurationSerialization.unregisterClass(InputBlockNeuron.class);
		ConfigurationSerialization.unregisterClass(InputBooleanNeuron.class);
		ConfigurationSerialization.unregisterClass(InputLetterNeuron.class);
		ConfigurationSerialization.unregisterClass(InputNumberNeuron.class);
		ConfigurationSerialization.unregisterClass(InputTickNeuron.class);
		ConfigurationSerialization.unregisterClass(OutputNeuron.class);
		ConfigurationSerialization.unregisterClass(BiasNeuron.class);

		// me.zombie_striker.neuralnetwork.plugin.demo.logical.*;
		ConfigurationSerialization.unregisterClass(LogicalAND.class);
		ConfigurationSerialization.unregisterClass(LogicalOR.class);
		ConfigurationSerialization.unregisterClass(LogicalXOR.class);
		ConfigurationSerialization.unregisterClass(LogicalXNOR.class);
		ConfigurationSerialization.unregisterClass(LogicalNAND.class);
		ConfigurationSerialization.unregisterClass(LogicalInverted.class);
		ConfigurationSerialization.unregisterClass(LogicalNOR.class);

		// me.zombie_striker.neuralnetwork.plugin.demo.*;
		ConfigurationSerialization.unregisterClass(BlackJackHelper.class);
		ConfigurationSerialization.unregisterClass(NumberAdder.class);
		ConfigurationSerialization.unregisterClass(BotGuesser.class);
		ConfigurationSerialization.unregisterClass(PrimeNumberBot.class);
		ConfigurationSerialization.unregisterClass(MusicBot.class);
		ConfigurationSerialization.unregisterClass(SwearBot.class);
	}
}
