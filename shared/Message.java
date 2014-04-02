package shared;

import java.util.HashMap;

public class Message {
	
	private HashMap<String, String> f = new HashMap<>();

	public Message(HashMap<String, String> fields) {
		this.f = fields;
	}

	public Message(String rawMsg) {
		this.f = decode(rawMsg);
	}

	public String encode() {
		StringBuilder b = new StringBuilder();

		for (String k : f.keySet()) {
			b.append(k + "[" + f.get(k) + "]");
		}

		return b.toString();
	}

	public HashMap<String, String> decode(String msg) {
		for (int i = 0; i < msg.length() - 1;) {

			StringBuilder b = new StringBuilder();
			while (true) {
				if (msg.charAt(i) == '[') {
					i++;
					break;
				}

				b.append(msg.charAt(i));
				i++;
			}

			StringBuilder v = new StringBuilder();
			while (true) {
				if (msg.charAt(i) == ']') {
					i++;
					break;
				}

				v.append(msg.charAt(i));
				i++;
			}

			f.put(b.toString(), v.toString());
		}

		return f;
	}

	public void set(String key, String value) {
		f.put(key, value);
	}

	public String get(String key) {
		return f.get(key);
	}
}
