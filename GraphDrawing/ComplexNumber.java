package GraphDrawing;

/**
 * @author Arsen Maxyutov.
 */
public class ComplexNumber {

	private double a;

	private double b;

	private int iterations = 0;

	public ComplexNumber(double a, double b) {
		this.a = a;
		this.b = b;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public int getIterations() {
		return iterations;
	}

	public ComplexNumber add(ComplexNumber toAdd) {
		ComplexNumber output = new ComplexNumber(toAdd.getA() + a, toAdd.getB() + b);
		return output;
	}

	public ComplexNumber exp(int n) {
		ComplexNumber z = new ComplexNumber(a, b);
		for(int i = 1; i < n; i++) {
			z = z.multiply(z);
		}
		return z;
	}

	public ComplexNumber multiply(ComplexNumber toMultiply) {
		double newa = a * toMultiply.getA() - b * toMultiply.getB();
		double newb = a * toMultiply.getB() + b * toMultiply.getA();
		ComplexNumber output = new ComplexNumber(newa, newb);
		return output;
	}

	public boolean diverges() {
		iterations = 1;
		ComplexNumber z = new ComplexNumber(a, b);
		final ComplexNumber c = new ComplexNumber(a, b);
		for(int i = 1; i < MandelBrot.MAX_ITERATIONS; i++) {
			z = z.exp(MandelBrot.POWER).add(c);
			iterations++;
			if(Math.abs(z.getA()) > 2 || Math.abs(z.getB()) > 2)
				return true;
		}
		return false;
	}

	private static final int MAXRGB = 256 * 256 * 256;

	public int getRGB() {
		double oneIteration = MAXRGB / 1000;
		return (int) (oneIteration * iterations);
	}

	public String toString() {
		return a + " + " + b + "i";
	}
}
