package standalones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVTools {
	public static void main(String args[])
	{
		String dirpath = "G:\\My Documents\\3rd Year Project\\3YP-Documents\\Model Analysis\\V0.25\\";
	
		File f = new File(dirpath);
		String[] filenames = f.list();
		ArrayList<BufferedReader> readList = new ArrayList<BufferedReader>();
		ArrayList<FileStore> writeList = new ArrayList<FileStore>();
		String[] headers = null;
		String header = null;
		
		for(String filename : filenames)
		{
			if(filename.substring(filename.length()-3, filename.length()).matches("csv"))
			{
				try {
					//this is ridiculous
					readList.add(new BufferedReader(new InputStreamReader(new FileInputStream(new File(dirpath + filename)))));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				System.out.println(filename + " is a csv");
		}
		
		
		//We need to get the first line and relevant headers and put them in a list
		try {
			header =  readList.get(0).readLine();
			//rewind the file
			//readList.get(0).reset();
			headers = header.split(",");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String h : headers)
		{
			if(h != "id")
				writeList.add(new FileStore(h));
		}
		
		int fileNo = 0;
		for(BufferedReader br : readList)
		{
			String line;
			try {
				while((line = br.readLine()) != null)
				{
					if(!line.contentEquals(header))
					{
						String[] rowVals = line.split(",");
						for(int i = 0; i < rowVals.length; i++)
						{
							writeList.get(i).addRowVal(headers[i]+fileNo, fileNo, rowVals[i]);
						}
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			fileNo++;
		}
		writeList.get(1).printCol();
		File newDir = new File(dirpath + "attrCSVs");
		newDir.mkdir();
		
		for(FileStore fs : writeList)
		{
			fs.writeToFile(dirpath + "attrCSVs\\");
		}
		
	}
		
}

class FileStore
{
	String name;
	String header;
	ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();
	
	FileStore(String name)
	{
		this.name = name;
	}
	
	public void addCol(String colName)
	{
		header += colName + ",";
		columns.add(new ArrayList<String>());
	}
	
	public void addRowVal(String colName, int columnNumber, String value)
	{
		try {
			columns.get(columnNumber).add(value);
		} 
		catch(IndexOutOfBoundsException e)
		{
			header += colName + ",";
			columns.add(new ArrayList<String>());
			columns.get(columnNumber).add(value);
		}
		
	}
	
	public void writeToFile(String dir)
	{
		header = header.substring(0, header.length() - 1);
		try {
			FileWriter fw = new FileWriter(dir + name + ".csv");
			fw.write(header + "\n");
			
			for(int i = 0; i < columns.get(0).size(); i++)
			{
				String line = "";
				for(int j = 0; j < columns.size(); j++)
				{
					line += columns.get(j).get(i) + ",";
				}
				line = line.substring(0, line.length() - 1);
				fw.write(line + "\n");
			}
			
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void printCol()
	{
		for(ArrayList<String> col : columns)
		{
			System.out.println(col.size() + " " );
		}
	}
}
