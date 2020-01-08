import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQL 
{
	/* Connexion � la base de donn�es */
	private String aURL;
	private String aUser;
	private String aPassword;
	private Connection aConnexion = null;
	
	MySQL(String pServer, String pPort, String pDatabase, String pUser, String pPassword)
	{
		this.aPassword = pPassword;
		this.aUser = pUser;
		this.aURL = "jdbc:mysql://" + pServer + ":" + pPort + "/" + pDatabase; 		
	}
	
	public void mConnect()
	{
		try
		{
			this.aConnexion = DriverManager.getConnection(this.aURL, this.aUser, this.aPassword);
		}
		catch ( SQLException e ) 
		{
			System.out.println("Erreur � la connexion de la base de donn�e...");
			e.printStackTrace(System.err);
			this.mClose();
			System.exit(-1);
		}
	}
	
	public Connection mConnection()
	{
		return this.aConnexion;
	}
	
	
	
	public void mClose()
	{
		if(this.aConnexion != null)
		{
			try 
			{
			    /* Fermeture de la connexion */
				this.aConnexion.close();
			}
			catch ( SQLException ignore )
			{
			    /* Si une erreur survient lors de la fermeture, il suffit de l'ignorer. */
			}
		}
	}
}
