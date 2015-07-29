package tests;

class Precision {
	
	public static void main (String[] args){
	double f1 = 0.0005d - 0.0004d; //d12 - d11
	
	double f2 = 0.0004d - 0.0005d;//d21 - d22

	double d = Math.exp(f1) + Math.exp(f2);
	System.out.println(d);
	d /= 1 + (d + Math.exp(f1 + f2));
	System.out.println(d);
	f1 = 1/(1 + 1/Math.cosh(f1));
	f2 = 1/(1 + 1/Math.cosh(f2));
	System.out.println(f1);
	System.out.println(f2);
	d = Math.log((f1 + f2)/(2*d))/400;
	System.out.println(d);
	}
}