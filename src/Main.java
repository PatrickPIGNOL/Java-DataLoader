public class Main 
{
	public static void main(String[] pArgs)
	{
		String vOS = System.getProperty("os.name");
		String vFilePath = "";
		if(vOS.contains("Windows"))
		{
			vFilePath = "C:\\Users\\IPME07\\Desktop\\Formation IPME\\Projet F1\\formula-1-race-data";
		}
		if(vOS.contains("Linux"))
		{
			vFilePath = "/home/patrick/Bureau/Formation IPME/Projet F1/formula-1-race-data";
		}
		
		DataLoader vDataLoader = new DataLoader(pArgs[0], pArgs[1], pArgs[2], pArgs[3], pArgs[4], pArgs[5]);		
		vDataLoader.mLoadData();
	}
}	
