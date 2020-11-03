package milestone_one;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageProperties {
	
	private static final Logger log = Logger.getLogger(ManageProperties.class.getName());
	private static Properties prop;
	private static InputStream input;
	
	public ManageProperties() {
		//Nothing to do
	}
	
	//implemets singleton for Properties and InputStream istancies
	public static Properties getInstance(){ 
        if (prop == null) {
            prop = new Properties();
        }
        if (input == null) {
        	try {
				input = new FileInputStream("JavaResources\\config.properties");
				prop.load(input);
			} catch (FileNotFoundException e) {
				log.log(Level.SEVERE,"FileNotFoundException", e);
			} catch (IOException e) {
				log.log(Level.SEVERE,"IOException", e);
			}
        	
        }
        return prop; 
    } 

	public static void main(String[] args) {
		Properties properties = ManageProperties.getInstance();
		log.info(properties.getProperty("marco"));
		

		Properties properties1 = ManageProperties.getInstance();
		log.info(properties1.getProperty("elisa"));
	}

}
