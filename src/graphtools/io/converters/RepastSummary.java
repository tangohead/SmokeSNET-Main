package graphtools.io.converters;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;

public class RepastSummary {
	Context context;
	Network network;
	
	public RepastSummary() {
		context = null;
		network = null;
	}
	
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public Network getNetwork() {
		return network;
	}
	public void setNetwork(Network network) {
		this.network = network;
	}
	
	
}
