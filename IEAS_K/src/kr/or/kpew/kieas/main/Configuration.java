package kr.or.kpew.kieas.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;

public class Configuration {
	
	public String configFilePath = null;
	public String pathIssuerAddress = null;
	
	public Configuration(String[] args) {
		List<String> optionHelpStrings = new ArrayList<String>();
        CmdLineParser parser = new CmdLineParser();
        Object object = null;
       
        CmdLineParser.Option configFilePath = addHelp(optionHelpStrings,
        		parser.addStringOption('c', "conf"), "This is path of configuration file.");

        try {
               parser.parse(args);
               this.configFilePath = (String)parser.getOptionValue(configFilePath);
              
        } catch (CmdLineParser.OptionException e) {
               System.err.println(e.getMessage());
               System.exit(2);
        }
        
        if (this.configFilePath != null) {
	        try {
		        YamlReader reader = new YamlReader(new FileReader(this.configFilePath));
		        object = reader.read();
	        } catch (YamlException e) {
	            System.err.println(e.getMessage());
	            System.exit(2);
	        } catch (FileNotFoundException e) {
	            System.err.println(e.getMessage());
	            System.exit(2);
			}
	
	        Map map = (Map)object;
	        this.pathIssuerAddress = (String)map.get("issuer.address");
	        System.out.println(this.pathIssuerAddress);
        }

	}
    
    private Option addHelp(List<String> optionHelpStrings, Option option, String helpString) {
            optionHelpStrings.add(" -" + option.shortForm() + ", --" + option.longForm() + ": " + helpString);
            return option;
    }
}
