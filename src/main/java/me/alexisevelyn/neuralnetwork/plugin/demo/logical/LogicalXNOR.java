package me.alexisevelyn.neuralnetwork.plugin.demo.logical;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.alexisevelyn.neuralnetwork.*;
import me.alexisevelyn.neuralnetwork.neurons.*;
import me.alexisevelyn.neuralnetwork.neurons.input.InputBooleanNeuron;
import me.alexisevelyn.neuralnetwork.senses.Sensory2D_Booleans;
import me.alexisevelyn.neuralnetwork.util.DeepReinforcementUtil;

public class LogicalXNOR extends NNBaseEntity implements Controller {

	public Sensory2D_Booleans binary = new Sensory2D_Booleans(1, 2);

	public LogicalXNOR(boolean createAI) {
		this.controller = this;

		if (createAI) {
			/**
			 * If createAI is true, then generate the AI with 3 layers (i.e, 1 hidden
			 * layer), 2 inputs, 3 neurons, and 2 bias neurons. After that, connect all the
			 * neurons.
			 * 
			 * If you want to test using random inputs (what should be done, but makes
			 * replication and understanding a bit hander), add randomizeNeurons() after
			 * connecting then.
			 */
			this.ai = NNAI.generateAI(this, 1, 3, "output");
			for (int binaryIndex = 0; binaryIndex < 2; binaryIndex++) {
				InputBooleanNeuron.generateNeuronStatically(ai, 0, binaryIndex, this.binary);
			}

			// Creates the neurons for layer 1.
			for (int neurons = 0; neurons < 3; neurons++) {
				Neuron.generateNeuronStatically(ai, 1);
			}
			// BiasNeuron.generateNeuronStatically(ai, 0);
			// BiasNeuron.generateNeuronStatically(ai, 1);
			connectNeurons();
		}
	}

	public String learn() {

		/**
		 * Simple explanation of these steps:
		 * 
		 * 1) If it is currently learning, change the inputs to either true or false.
		 * 
		 * 2) Let the NN tick and think. This will return the outputs from the
		 * OutpuitNeurons
		 * 
		 * 3) If it is not learning, just return the answer.
		 * 
		 * 4) Else, do the logic and see if the answer it gave (thought[0]) was correct.
		 * 
		 * 5) If it was not correct, use the DeepReinforcementUtil to improve it.
		 * 
		 * 6) After inprovement, return a message with if it was correct, the accuracy,
		 * the inputs, and what it thought was the output,
		 */
		binary.changeValueAt(0, 0, ThreadLocalRandom.current().nextBoolean());
		binary.changeValueAt(0, 1, ThreadLocalRandom.current().nextBoolean());
		boolean[] thought = tickAndThink();
		boolean logic = (binary.getBooleanAt(0, 0) == binary.getBooleanAt(0, 1));
		boolean result = logic == thought[0];
		this.getAccuracy().addEntry(result);

		// IMPROVE IT
		HashMap<Neuron, Double> map = new HashMap<>();
		for (int i = 0; i < thought.length; i++)
			map.put(ai.getNeuronFromId(i), logic ? 1 : -1.0);
		if (!result)
			DeepReinforcementUtil.instantaneousReinforce(this, map, 1);
		return ((result ? ChatColor.GREEN : ChatColor.RED) + "acc " + getAccuracy().getAccuracyAsInt() + "|"
				+ binary.getBooleanAt(0, 0) + " + " + binary.getBooleanAt(0, 1) + " ~~ " + thought[0]);

	}

	@Override
	public String update() {

		boolean[] thought = tickAndThink();

		return ("|" + binary.getBooleanAt(0, 0) + " + " + binary.getBooleanAt(0, 1) + " ~~ " + thought[0]);
	}

	@Override
	public void setInputs(CommandSender initiator, String[] args) {
		if (this.shouldLearn) {
			initiator.sendMessage("Stop the learning before testing. use /nn stoplearning");
			return;
		}
		if (args.length > 2) {
			boolean test = false;
			try {
				test = Boolean.parseBoolean(args[1]);
			} catch (Exception e) {
			}
			boolean test2 = false;
			try {
				test2 = Boolean.parseBoolean(args[2]);
			} catch (Exception e) {
			}
			binary.changeValueAt(0, 0, test);
			binary.changeValueAt(0, 1, test2);

		} else {
			initiator.sendMessage("Provide two values (True or false)");
		}
	}

	@Override
	public NNBaseEntity clone() {
		LogicalXNOR thi = new LogicalXNOR(false);
		thi.ai = this.ai;
		return thi;
	}

	@Override
	public void setBase(NNBaseEntity base) {
	}

	public LogicalXNOR(Map<String, Object> map) {
		super(map);
	}
}
