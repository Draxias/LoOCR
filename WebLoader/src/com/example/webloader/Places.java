package com.example.webloader;

public class Places {
	String name, vicinity, address;
	String[] types;

	public Places() {
	}

	public Places(String n, String v, String a/* , String[] t */) {
		name = n;
		vicinity = v;
		address = a;
	}

	public void setTypes() {
	}

	public void setVic(String v) {
		vicinity = v;
	}

	public void setAddress(String a) {
		address = a;
	}

	public void setName(String n) {
		name = n;
	}

	public String getVic() {
		return vicinity;
	}

	public String getName() {
		return name;
	}

}
