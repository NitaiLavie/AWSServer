package awsServer;

public class TanhAction implements ServerAction {

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		for(int i=0; i<666; i++) {
			double a = Math.sqrt(Math.tanh(Math.sqrt(Math.PI)));
			System.out.println(a);
		}
	}

}
