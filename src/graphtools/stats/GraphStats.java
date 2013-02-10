package graphtools.stats;

import java.util.ArrayList;

import agents.BaseHuman;

public class GraphStats {
	double avgClusterCoeff;
	ArrayList<BaseHuman> highClusterCoeffs;
	ArrayList<BaseHuman> localClusterCoeffs;

	GraphStats(double avgClusterCoeff, ArrayList<BaseHuman> highClusterCoeffs, ArrayList<BaseHuman> localClusterCoeffs)
	{
		this.avgClusterCoeff = avgClusterCoeff;
		this.highClusterCoeffs = highClusterCoeffs;
		this.localClusterCoeffs = localClusterCoeffs;
	}
	
	public BaseHuman getHighClusterNode()
	{
		int index = (int)Math.floor(Math.random() * highClusterCoeffs.size());
		if(highClusterCoeffs.size() > 0)
		{
			BaseHuman rtn = highClusterCoeffs.get(index);
			highClusterCoeffs.remove(index);
			return rtn;
		}
		else
			return null;
	}
	
	public BaseHuman getLocalClusterNode()
	{
		int index = (int)Math.floor(Math.random() * localClusterCoeffs.size());
		if(localClusterCoeffs.size() > 0)
		{
			BaseHuman rtn = localClusterCoeffs.get(index);
			localClusterCoeffs.remove(index);
			return rtn;
		}
		else
			return null;
	}
	
	public double getAvgClusterCoeff() {
		return avgClusterCoeff;
	}
	
}
