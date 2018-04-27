package uk.co.vodafone.hackathon.configuration;

public class Test {

	public static void main(String[] args) {
		String n = "apple iphone under 5000";
		String[] arr = n.split(" ");
		String price = null;
		for(int i=1;i<arr.length;i++) {
			if(arr[i].equals("under")) {
				price= arr[i+1];
			}
		}
		System.out.println(price);
	}

}
