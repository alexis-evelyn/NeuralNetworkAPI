package me.alexisevelyn.neuralnetwork.neurons.input;

import java.util.Map;

import me.alexisevelyn.neuralnetwork.NNAI;
import me.alexisevelyn.neuralnetwork.senses.*;

public class InputNumberNeuron extends InputNeuron {

	public InputNumberNeuron(NNAI ai, Sensory2D_Numbers sl) {
		super(ai);
		this.s = sl;
	}

	public InputNumberNeuron(Map<String,Object> map) {
		super(map);
	}
	
	public InputNumberNeuron(NNAI ai, int row, int col,
			Sensory2D_Numbers sl) {
		super(ai, row, col,sl);
		this.s = sl;
	}

	@Override
	public InputNeuron generateNeuron(NNAI ai, Senses2D word) {
		return InputNumberNeuron.generateNeuronStatically(ai, 0, ' ',
				 (Sensory2D_Numbers) word);
	}
	
	public static InputNeuron generateNeuronStatically(NNAI ai, int row,
			int col, Sensory2D_Numbers sl) {
		return new InputNumberNeuron(ai, row, col, (Sensory2D_Numbers) sl);
	}

	@Override
	public boolean isTriggered() {
		return getTriggeredStength()!=0;
	}

}
