package me.alexisevelyn.neuralnetwork.neurons;

import java.util.Map;

import me.alexisevelyn.neuralnetwork.NNAI;

public class BiasNeuron extends Neuron{

	public BiasNeuron(NNAI ai, int layer) {
		super(ai, layer);
	}
	@Override
	public double getTriggeredStength() {
		if(droppedOut())
			return 0;
		return 1;
	}
	@Override
	public boolean isTriggered() {
		if(droppedOut())
			return false;
		if(!useThreshold())
			return true;
		return getThreshold() < 0.5;
	}
	public Neuron generateNeuron(NNAI ai) {
		return BiasNeuron.generateNeuronStatically(ai, this.layer);
	}

	public static BiasNeuron generateNeuronStatically(NNAI ai, int layer) {
		return new BiasNeuron(ai, layer);
	}
	@Override
	public double forceTriggerStengthUpdate() {
		this.tickUpdated = getAI().getCurrentTick();
		return lastResult = 1;
	}
	public BiasNeuron(Map<String,Object> map) {
		super(map);
	}
}
