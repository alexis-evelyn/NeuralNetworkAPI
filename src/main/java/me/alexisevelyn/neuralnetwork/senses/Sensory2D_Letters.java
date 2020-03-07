package me.alexisevelyn.neuralnetwork.senses;

import java.util.HashMap;
import java.util.Map;

import me.alexisevelyn.neuralnetwork.neurons.input.InputLetterNeuron;

public class Sensory2D_Letters implements Senses2D{


	private char[] letters= InputLetterNeuron.letters;
	private String word="null";
	private String fullWord="null";
	public static final int MAX_LETTERS = 19;
	
	@Override
	public double getPowerFor(int x, int y) {
		//if(word.length()>x&&letters.length>y)
		return (fullWord.length()>x&&fullWord.charAt(x)==letters[y])?1:0;
		//return -1;
	}
	

	public Sensory2D_Letters(String word) {
		this.word = word;
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		for(int i = word.length(); i < MAX_LETTERS;i++){
			sb.append(" ");
		}
		this.fullWord = sb.toString();
	}

	public char getCharacterAt(int index) {
		if(fullWord==null)return ' ';
		try{
		return fullWord.charAt(index);
		}catch(Exception e){
			/**
			 * Seriously, I have tried doing a length check, a null check. Do I seriously need to add a catch statement?>
			 */
			return ' ';
		}
	}
	public String getWord(){
		return word;
	}
	public void changeWord(String word) {
		this.word = word;
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		for(int i = word.length(); i < MAX_LETTERS;i++){
			sb.append(" ");
		}
		fullWord = sb.toString();
	}
	public Sensory2D_Letters(Map<String, Object> map) {
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> v = new HashMap<String, Object>();
		return v;
	}

}
