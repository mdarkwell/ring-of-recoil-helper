package com.darkwell.utils;

import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChatHelper {
	@Inject
	private ChatMessageManager chat;

	public void sendMessage(String message) {
		sendMessage(message, ChatColorType.NORMAL);
	}

	public void sendMessage(String message, ChatColorType color) {
		sendMessage(message, color, ChatMessageType.GAMEMESSAGE);
	}

	public void sendMessage(String message, ChatColorType color, ChatMessageType type) {
		var string = new ChatMessageBuilder()
				.append(color)
				.append(message)
				.build();
		chat.queue(QueuedMessage.builder()
				.type(type)
				.runeLiteFormattedMessage(string)
				.build());
	}

	public void sendConsoleMessage(String message){
		sendMessage(message, ChatColorType.HIGHLIGHT, ChatMessageType.CONSOLE);
	}
}
