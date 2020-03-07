package me.alexisevelyn.neuralnetwork.plugin.demo.prime_number_guesser;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.alexisevelyn.neuralnetwork.*;
import me.alexisevelyn.neuralnetwork.neurons.BiasNeuron;
import me.alexisevelyn.neuralnetwork.neurons.Neuron;
import me.alexisevelyn.neuralnetwork.neurons.input.InputNumberNeuron;
import me.alexisevelyn.neuralnetwork.senses.Sensory2D_Numbers;
import me.alexisevelyn.neuralnetwork.util.DeepReinforcementUtil;

public class PrimeNumberBot extends NNBaseEntity implements Controller {

	public Sensory2D_Numbers binary = new Sensory2D_Numbers(2, 10);

	public boolean wasCorrect = true;

	public HashMap<Integer, Boolean> ifNumberIsPrime = new HashMap<>();

	public PrimeNumberBot(boolean createAI) {
		super(false, 2000);
		// We want to store the last 2000 entries, so we know how accurate it is.
		initValidPrimes();
		// this.senses.add(binary);

		if (createAI) {
			this.ai = NNAI.generateAI(this, 1, 5, "is A Prime");

			for (int trueOrFalse = 0; trueOrFalse < 1; trueOrFalse++) {
				// Change 1 to 2 if you also want to include if bit is false
				for (int binaryIndex = 0; binaryIndex < 10; binaryIndex++) {
					InputNumberNeuron.generateNeuronStatically(ai, trueOrFalse, binaryIndex, this.binary);
				}
			}
			BiasNeuron.generateNeuronStatically(ai, 0);

			// Creates the neurons for layer 1.
			for (int neurons = 0; neurons < 40; neurons++)
				Neuron.generateNeuronStatically(ai, 1);
			for (int neurons = 0; neurons < 20; neurons++)
				Neuron.generateNeuronStatically(ai, 2);
			for (int neurons = 0; neurons < 10; neurons++)
				Neuron.generateNeuronStatically(ai, 3);

			connectNeurons();
		}
		this.setNeuronsPerRow(0, 10);
		this.controller = this;
	}

	private int lastNumber = 0;

	public String learn() {
		boolean[] bbb = numberToBinaryBooleans((lastNumber++ % 1023)/* (int) (Math.random() * 1023) */);
		for (int i = 0; i < bbb.length; i++) {
			binary.changeNumberAt(0, i, (bbb[i]) ? 1 : 0);
			binary.changeNumberAt(1, i, (bbb[i]) ? 0 : 1);
		}

		boolean[] thought = tickAndThink();
		float accuracy = 0;

		// If it isprime:

		boolean[] booleanBase = new boolean[10];
		for (int i = 0; i < 10; i++) {
			booleanBase[i] = binary.getNumberAt(0, i) != 0;
		}
		int number = binaryBooleansToNumber(booleanBase);
		boolean result = ifNumberIsPrime.get(number);

		wasCorrect = (result == thought[0]);

		this.getAccuracy().addEntry(wasCorrect);
		accuracy = (float) this.getAccuracy().getAccuracy();

		// IMPROVE IT
		HashMap<Neuron, Double> map = new HashMap<>();
		map.put(ai.getNeuronFromId(0), result ? 1 : -1.0);
		if (!wasCorrect)
			DeepReinforcementUtil.instantaneousReinforce(this, map, 1);
		return ((wasCorrect ? ChatColor.GREEN : ChatColor.RED) + "acc " + ((int) (100 * accuracy)) + "|=" + number
				+ "|correctResp=" + result + "|WasPrime-Score "
				+ ((int) (100 * (ai.getNeuronFromId(0).getTriggeredStength()))));
	}

	@Override
	public String update() {
		boolean[] thought = tickAndThink();
		float accuracy = 0;

		// If it isprime:

		boolean[] booleanBase = new boolean[10];
		for (int i = 0; i < 10; i++) {
			booleanBase[i] = binary.getNumberAt(0, i) != 0;
		}
		int number = binaryBooleansToNumber(booleanBase);
		boolean result = ifNumberIsPrime.get(number);

		return ((thought[0] ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + "|=" + number + "|WasPrime-Score "
				+ ((int) (100 * (ai.getNeuronFromId(0).getTriggeredStength()))));

	}

	@Override
	public NNBaseEntity clone() {
		PrimeNumberBot thi = new PrimeNumberBot(false);
		thi.ai = this.ai;
		return thi;
	}

	public void setBase(NNBaseEntity t) {
		// this.base = (PrimeNumberGuesser) t;
	}

	/**
	 * Adds string B to the hashmap with value true
	 * 
	 * @param b Fill Me In
	 */
	public void a(Integer... b) {
		for (int c : b)
			ifNumberIsPrime.put(c, true);

	}

	private boolean[] numberToBinaryBooleans(int i) {
		boolean[] k = new boolean[10];

		int tempnumber = i;
		for (int power = 9; power >= 0; power--) {
			if (tempnumber - Math.pow(2, power) >= 0) {
				k[power] = true;
				// System.out.println(tempnumber+" - "+(tempnumber-Math.pow(2,power)));
				tempnumber -= Math.pow(2, power);

			} else {
				k[power] = false;
			}
		}
		return k;
	}

	private int binaryBooleansToNumber(boolean[] b) {
		int k = 0;
		for (int i = 0; i < b.length; i++) {
			if (b[i])
				k += Math.pow(2, i);
		}
		return k;
	}

	private boolean isPrime(int n) {
		// check if n is a multiple of 2
		if (n % 2 == 0)
			return false;
		// if not, then just check the odds
		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0)
				return false;
		}
		return true;
	}

	private void initValidPrimes() {
		for (int i = 0; i < 1023; i++) {
			ifNumberIsPrime.put(i, isPrime(i));
			// ifNumberIsPrime.put(i, i%2==0);
		}
	}

	@Override
	public void setInputs(CommandSender initiator, String[] args) {
		if (this.shouldLearn) {
			initiator.sendMessage("Stop the learning before testing. use /nn stoplearning");
			return;
		}

		if (args.length > 1) {
			int test = 0;
			try {
				test = Integer.parseInt(args[1]);
			} catch (Exception e) {
				return;
			}
			int tempnumber = test;
			for (int power = 9; power >= 0; power--) {
				if (tempnumber - Math.pow(2, power) >= 0) {
					this.binary.changeNumberAt(0, power, 1);
					this.binary.changeNumberAt(1, power, 0);
					tempnumber -= Math.pow(2, power);
				} else {
					this.binary.changeNumberAt(0, power, 0);
					this.binary.changeNumberAt(1, power, 1);
				}
			}
		} else {
			initiator.sendMessage("Provide a number");
		}

	}

}
