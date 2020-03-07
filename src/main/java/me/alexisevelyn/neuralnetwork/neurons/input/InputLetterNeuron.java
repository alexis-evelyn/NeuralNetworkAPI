package me.alexisevelyn.neuralnetwork.neurons.input;

import java.util.Map;

import me.alexisevelyn.neuralnetwork.NNAI;
import me.alexisevelyn.neuralnetwork.senses.*;

public class InputLetterNeuron extends InputNeuron {

	public static char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '_' };
	
	public char letter;

	public InputLetterNeuron(NNAI ai, Sensory2D_Letters sl) {
		super(ai);
		this.s = sl;
	}

	public InputLetterNeuron(NNAI ai, int index, int charr,
			Sensory2D_Letters sl) {
		super(ai, index, charr, sl);
		this.letter = letters[charr];
		this.s = sl;
	}

	@Override
	public InputNeuron generateNeuron(NNAI ai, Senses2D word) {
		return InputLetterNeuron.generateNeuronStatically(ai, 0, ' ',
				(Sensory2D_Letters) word);
	}

	public static InputNeuron generateNeuronStatically(NNAI ai, int index,
			char letter, Sensory2D_Letters sl) {
		int index2 = 0;
		for(;index2<letters.length;index2++)
			if(letters[index2]==letter)
				break;	
		InputNeuron link = new InputLetterNeuron(ai, index, index2, sl);
		return link;
	}
	public static InputNeuron generateNeuronStatically(NNAI ai, int index,
			int letter, Sensory2D_Letters sl) {
		InputNeuron link = new InputLetterNeuron(ai, index, letter, sl);
		return link;
	}

	@Override
	public boolean isTriggered() {
		if(this.tickUpdated == this.getAI().getCurrentTick())
			return isTriggeredLast;
			if (((Sensory2D_Letters) s).getCharacterAt(xlink) == letter)
				return isTriggeredLast=true;
		return isTriggeredLast=false;
	}

	public InputLetterNeuron(Map<String, Object> map) {
		super(map);
		this.letter= (map.get("char")+"").toCharArray()[0];
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = super.serialize();
		map.put("char", letter);
		return map;
	}

}
