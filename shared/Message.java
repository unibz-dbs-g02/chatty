package shared;

import java.util.HashMap;

public class Message {
	
	private HashMap<String, String> fields = new HashMap<>();

	public Message(HashMap<String, String> fields) {
		this.fields = fields;
	}

	public Message(String rawMsg) {
		this.fields = decode(rawMsg);
	}

	public String encode() {
		StringBuilder builder = new StringBuilder();

		for (String key : fields.keySet()) {
			builder.append(key + "[" + fields.get(key) + "]");
		}

		return builder.toString();
	}

	public HashMap<String, String> decode(String message) {
		for (int i = 0; i < message.length() - 1;) {

			StringBuilder keyBuilder = new StringBuilder();
			while (true) {
				if (message.charAt(i) == '[') {
					i++;
					break;
				}

				keyBuilder.append(message.charAt(i));
				i++;
			}

			StringBuilder valueBuilder = new StringBuilder();
			while (true) {
				if (message.charAt(i) == ']') {
					i++;
					break;
				}

				valueBuilder.append(message.charAt(i));
				i++;
			}

			fields.put(keyBuilder.toString(), valueBuilder.toString());
		}

		return fields;
	}

	public void set(String key, String value) {
		fields.put(key, value);
	}

	public String get(String key) {
		return fields.get(key);
	}
}
