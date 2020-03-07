package me.alexisevelyn.neuralnetwork;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import me.alexisevelyn.neuralnetwork.neurons.*;
import me.alexisevelyn.neuralnetwork.util.Accuracy;

public class NNBaseEntity implements ConfigurationSerializable {

	public NNAI ai;

	public Controller controller;
	public boolean shouldLearn = false;

	// TODO: Default accuracy is 500. This makes sure slight changes to the
	// amount of correct answers should not affect the total accuracy that much
	Accuracy accuracy = new Accuracy(500);

	public Accuracy getAccuracy() {
		return accuracy;
	}

	public NNBaseEntity() {
	}

	public NNBaseEntity(boolean createAI) {
		if (createAI)
			ai = new NNAI(this);
		accuracy = new Accuracy(500);
	}

	public NNBaseEntity(boolean createAI, int accuracyCheck) {
		if (createAI)
			ai = new NNAI(this);
		accuracy = new Accuracy(accuracyCheck);
	}

	public void connectNeurons() {
		for (Neuron n : ai.getAllNeurons()) {
			connectNeuron(n);
		}
	}

	public void connectNeuron(Neuron n) {
		if (ai.maxlayers > n.layer + 1) {
			// n.setWeight((Math.random() * 2) - 1);
			// n.setWeight(0.1);
			for (Neuron output : ai.getNeuronsInLayer(n.layer + 1)) {
				if (output instanceof BiasNeuron)
					continue;
				// n.getOutputs().add(output.getID());
				// output.getInputs().add(n.getID());
				// n.setStrengthForNeuron(output, (Math.random() * 2) - 1);
				n.setStrengthForNeuron(output, 0);
			}
		}
	}

	/**
	 * In case the neuron needs more neurons after it has already been trained, this
	 * will add the connection from previous layers.
	 * 
	 * @param newNeuron
	 *            - the new neuron that was added.
	 * @param randomize Fill Me In
	 */
	public void backPropNeuronConnections(Neuron newNeuron, boolean randomize) {
		if (newNeuron.getLayer() > 0) {
			for (Neuron n : getAI().getNeuronsInLayer(newNeuron.getLayer() - 1)) {
				if (n.allowNegativeValues())
					n.setStrengthForNeuron(newNeuron, randomize ? (Math.random() * 2) - 1 : 0.1);
				else
					n.setStrengthForNeuron(newNeuron, randomize ? Math.random() : 0.1);
			}
		}
	}

	public void randomizeNeurons() {
		for (Neuron n : ai.getAllNeurons()) {
			randomizeNeuron(n);
		}
	}

	public void randomizeNeuron(Neuron n) {
		if (ai.maxlayers > n.layer + 1) {
			if (n.allowNegativeValues()) {
				n.setWeight((Math.random() * 2) - 1);
				n.setThreshold((Math.random() * 2) - 1);
			} else {
				n.setWeight((Math.random()));
				n.setThreshold((Math.random()));
			}
			for (Neuron output : ai.getNeuronsInLayer(n.layer + 1)) {
				if (n.allowNegativeValues()) 
				n.setStrengthForNeuron(output, (Math.random() * 2) - 1);
				else
					n.setStrengthForNeuron(output, (Math.random()));
			}
		}
	}

	public boolean[] tickAndThink() {
		return ai.think();
	}

	public NNBaseEntity clone() {
		return null;
	}

	public Controller getController() {
		return controller;
	}

	public boolean shouldLearn() {
		return shouldLearn;
	}

	public void setShouldLearn(boolean b) {
		shouldLearn = b;
	}

	public void setNeuronsPerRow(int row, int amount) {
		getAI().setNeuronsPerRow(row, amount);
	}

	public int getNeuronsPerRow(int row) {
		return getAI().getNeuronsPerRow(row);
	}

	public NNAI getAI() {
		return ai;
	}

	public NNBaseEntity(Map<String, Object> map) {
		this.ai = (NNAI) map.get("ai");
		this.ai.entity = this;
		if (map.containsKey("c")) {
			this.controller = (Controller) map.get("c");
		} else if (this instanceof Controller) {
			this.controller = (Controller) this;
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = new HashMap<String, Object>();
		if (this.controller != this)
			m.put("c", controller);
		m.put("ai", ai);
		return m;
	}
}
