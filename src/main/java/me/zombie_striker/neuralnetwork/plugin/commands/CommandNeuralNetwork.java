package me.zombie_striker.neuralnetwork.plugin.commands;

import me.zombie_striker.neuralnetwork.plugin.Main;
import me.zombie_striker.neuralnetwork.NNBaseEntity;
import me.zombie_striker.neuralnetwork.NeuralNetwork;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandNeuralNetwork implements CommandExecutor, TabCompleter {
    Main plugin;
    private File NeuralNetworkData;
    private FileConfiguration NeuralNetworkDataConfig;

    /**
     * If you are creating your own plugin using the NNAPI, do not use the
     * default NN. You should have your own class.
     */
    private NeuralNetwork neuralnetwork;

    public CommandNeuralNetwork(Main main) {
        this.plugin = main;
        this.NeuralNetworkDataConfig = this.plugin.getConfig();
        this.NeuralNetworkData = new File(this.plugin.getDataFolder(), "NNData.yml");
        neuralnetwork = new NeuralNetwork(main);

        // Disabled Because Results in "Plugin cannot be Null"
        //Bukkit.getPluginManager().registerEvents(new ExampleSwearListener(this.getNN()), this.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            int page = 0;
            if (args.length > 1) {
                page = Integer.parseInt(args[1]) - 1;
            }
            String[] pages = HelpPages.values()[page].lines;
            for (String p : pages) {
                sender.sendMessage(p);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("createNewNN")
                || args[0].equalsIgnoreCase("cnn")) {
            if (args.length < 2) {
                sender.sendMessage("You must specify which NN you want to create. Choose one of the following:");
                for (Class<?> c : this.plugin.getDemos()) {
                    sender.sendMessage("-" + c.getSimpleName());
                }
                return true;
            }
            NNBaseEntity base = null;
            for (Class<?> c : this.plugin.getDemos()) {
                if (args[1].equalsIgnoreCase(c.getSimpleName())) {
                    try {
                        try {
                            base = (NNBaseEntity) c.getDeclaredConstructor(
                                    Boolean.TYPE).newInstance(true);
                        } catch (Exception e) {
                            // If it does not have a parameter for boolean
                            // types, use default, empty constructor.
                            base = (NNBaseEntity) c.getDeclaredConstructor()
                                    .newInstance(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (base == null) {
                sender.sendMessage("You need to provide a valid bot type. Choose one of the following.");
                for (Class<?> c : this.plugin.getDemos()) {
                    sender.sendMessage("-" + c.getSimpleName());
                }
                return true;
            }
            sender.sendMessage("Set the NN to "
                    + base.getClass().getSimpleName());
            this.getNN().setCurrentNeuralNetwork(base);
            return true;
        }
        if (args[0].equalsIgnoreCase("setNeuronsPerRow")
                || args[0].equalsIgnoreCase("snpr")) {
            try {
                this.getNN().getCurrentNeuralNetwork().getAI()
                        .setNeuronsPerRow(0, Integer.parseInt(args[1]));
            } catch (Exception e) {
                sender.sendMessage("You must provide how many neurons should be displayed per row");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("startlearning")) {
            this.getNN().startLearningAsynchronously();
            sender.sendMessage("Starting learning");
            return true;
        }
        if (args[0].equalsIgnoreCase("stoplearning")) {
            this.getNN().stopLearning();
            sender.sendMessage("Stopped learning");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            this.getNN().stop();
            sender.sendMessage("Stopping");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            this.getNN().start();
            sender.sendMessage("Starting");
            return true;
        }
        if (args[0].equalsIgnoreCase("triggeronce")) {
            sender.sendMessage(this.getNN().triggerOnce());
            return true;
        }

        if (args[0].equalsIgnoreCase("test")) {
            this.getNN().getCurrentNeuralNetwork().getController()
                    .setInputs(sender, args);
            sender.sendMessage(this.getNN().triggerOnce());
            return true;
        }
        if (args[0].equalsIgnoreCase("openGrapher")) {
            this.getNN().openGrapher();
            sender.sendMessage("Opeining Grapher");
            return true;
        }
        if (args[0].equalsIgnoreCase("closeGrapher")) {
            this.getNN().closeGrapher();
            sender.sendMessage("closing Grapher");
            return true;
        }
        if (args[0].equalsIgnoreCase("save")) {
            if (args.length > 1) {
                String id = args[1];
                this.NeuralNetworkDataConfig.set(id, this.getNN().getCurrentNeuralNetwork());
                try {
                    this.NeuralNetworkDataConfig.save(this.NeuralNetworkData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage("Saving the NN " + id);
            } else {
                sender.sendMessage("Provide a path for the NN");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("load")) {
            if (args.length > 1) {
                String id = args[1];
                if (!this.NeuralNetworkDataConfig.contains(id)) {
                    sender.sendMessage("The path in the config is null.");
                    return true;
                }
                NNBaseEntity b = (NNBaseEntity) this.NeuralNetworkDataConfig.get(id);
                if (b == null) {
                    sender.sendMessage("The NN is null.");
                    return true;
                }
                this.getNN().setCurrentNeuralNetwork(b);
                // nn.setCurrentNeuralNetwork(Save_Config.loadnn(this, base,
                // id));
                sender.sendMessage("loading the NN " + id);
            } else {
                sender.sendMessage("Provide an id");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            this.onTabCompleteFilter(list, args[0], "save");
            this.onTabCompleteFilter(list, args[0], "load");
            this.onTabCompleteFilter(list, args[0], "list");
            this.onTabCompleteFilter(list, args[0], "startlearning");
            this.onTabCompleteFilter(list, args[0], "stoplearning");
            this.onTabCompleteFilter(list, args[0], "start");
            this.onTabCompleteFilter(list, args[0], "stop");
            this.onTabCompleteFilter(list, args[0], "createNewNN");
            this.onTabCompleteFilter(list, args[0], "help");
            this.onTabCompleteFilter(list, args[0], "test");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("load")) {
                for (String s : this.NeuralNetworkDataConfig.getConfigurationSection(
                        "NeuralNetworks").getKeys(false))
                    this.onTabCompleteFilter(list, args[1], s);
            } else if (args[0].equalsIgnoreCase("createNewNN")) {
                for (Class<?> c : this.plugin.getDemos()) {
                    this.onTabCompleteFilter(list, args[1], c.getSimpleName());
                }
            }
        }
        return list;
    }

    private void onTabCompleteFilter(List<String> list, String input, String check) {
        if (check.toLowerCase().startsWith(input.toLowerCase()))
            list.add(check);
    }

    private NeuralNetwork getNN() {
        return this.neuralnetwork;
    }

    public void onDisable() {
        // Close Grapher If Open
        if (this.getNN() != null && this.getNN().getGrapher() != null)
            this.getNN().closeGrapher();
    }
}