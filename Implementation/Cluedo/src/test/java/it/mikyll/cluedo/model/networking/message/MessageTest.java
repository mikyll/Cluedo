package it.mikyll.cluedo.model.networking.message;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class MessageTest {

	@Test
	void messagesPassTheFilter() throws Exception {
		Message message = new Message(MessageType.GAME, "alice", new GameMessage(GameMessageType.ROLL_DICE).toJson());

		Message read = (Message) readWithFilter(message);

		assertEquals(MessageType.GAME, read.getMsgType());
		assertEquals(GameMessageType.ROLL_DICE, GameMessage.fromJson(read.getContent()).getType());
	}

	@Test
	void otherClassesAreRejected() {
		List<String> notAMessage = new ArrayList<>(List.of("a", "b"));

		assertThrows(InvalidClassException.class, () -> readWithFilter(notAMessage));
	}

	@Test
	void longConnectionsAreNotCut() throws Exception {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bytes);
		for (int i = 0; i < 1000; i++) {
			output.writeObject(new Message(MessageType.CHAT, "alice", "message " + i));
			output.reset();
		}
		output.flush();

		ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		input.setObjectInputFilter(Message.FILTER);
		for (int i = 0; i < 1000; i++)
			assertEquals("message " + i, ((Message) input.readObject()).getContent());
	}

	private static Object readWithFilter(Object object) throws Exception {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
			output.writeObject(object);
		}
		ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		input.setObjectInputFilter(Message.FILTER);
		return input.readObject();
	}
}
