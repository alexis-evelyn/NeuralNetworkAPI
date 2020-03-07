package me.alexisevelyn.neuralnetwork;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface Controller extends ConfigurationSerializable {
	/**
	 * This is what will cause the AI to think. The return is a message that will be
	 * printed to console.
	 * 
	 * @return Fill Me In
	 */
	public String update();

	public void setBase(NNBaseEntity base);

	public void setInputs(CommandSender initiator, String[] args);

	/**
	 * This is a method designed for learning. Instead of relying on the update
	 * method to learn, this will help separate code designed for testing and for
	 * learning
	 * 
	 * The learn method will not be called in this update. However, in future
	 * updates, this is the method that will be called when learning.
	 *
	 * @return Fill Me In
	 */
	public String learn();
}
