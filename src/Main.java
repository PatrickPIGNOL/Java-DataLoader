public class Main 
{
	public static void main(String[] pArgs)
	{
		if(pArgs.length > 5)
		{			
			DataLoader vDataLoader = new DataLoader(pArgs[0], pArgs[1], pArgs[2], pArgs[3], pArgs[4], pArgs[5]);		
			vDataLoader.mLoadData();
		}		
	}
}	
