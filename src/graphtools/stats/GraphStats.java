package graphtools.stats;

import java.util.ArrayList;

import agents.BaseHuman;

public class GraphStats {
	double avgClusterCoeff;
	ArrayList<BaseHuman> highClusterCoeffs;

	GraphStats(double avgClusterCoeff, ArrayList<BaseHuman> highClusterCoeffs)
	{
		this.avgClusterCoeff = avgClusterCoeff;
		this.highClusterCoeffs = highClusterCoeffs;
	}
	
	public BaseHuman getHighClusterNode()
	{
		int index = (int)Math.floor(Math.random() * highClusterCoeffs.size());
		BaseHuman rtn = highClusterCoeffs.get(index);
		highClusterCoeffs.remove(index);
		return rtn;
	}
	
	public double getAvgClusterCoeff() {
		return avgClusterCoeff;
	}
	
}
