package evolution;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
	public static Properties loadProperties(String filename) {
		Properties p = new Properties();
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(filename);
			p.load(iStream);
		} catch (IOException e) {
			System.err.println("IO exception while reading properties:" + e);
			return null;
		} finally {
			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
					System.err.println("IO exception while closing:" + e);
					return null;
				}
			}
		}
		return p;
	}
}
