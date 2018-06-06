package kr.or.kpew.kieas.main;

import org.apache.commons.collections4.map.HashedMap;

public class ArgumentParser {
	
	private HashedMap<String, String> value = null;
	
	public ArgumentParser (String[] args) {
		value = new HashedMap<>();
		
		try {
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-conf")) {
					value.put("conf", args[i+1]);
					i++;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	public String get(String key) {
		return value.get(key);
	}
}
