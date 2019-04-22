import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ClientDriver {
	Connection cn;
	// currentResults holds current results from a search() so that other methods can access them
	ResultSet currentResults;
	// currentItem holds the row number of an itme the user is looking at (so we can use currentResults.absolute(currentItem) to go to it
	Integer currentItem;
	Statement myStmt;
	boolean isConnected;
	public ClientDriver(String dbname, String userid, String password) {
		cn = null;
		currentResults = null;
		currentItem = null;
		myStmt=null;
		isConnected=false;
		
		try {
			//1 get connection
			cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbname, userid, password);
			//2create statement
			myStmt = cn.createStatement();
			isConnected=true;
		}catch(Exception exc0){
			isConnected=false;
			System.out.println("connection failed: " + exc0);
		}
		while(isConnected) { //keep looping in the query until user hit exit
			System.out.println("---------------------------Main-Menu------------------------------------");
			System.out.println("Hello there, welcome to apple music search");
			System.out.println("please enter which query you would like to use to search the database");
			System.out.println("1 for querying a song's detailed info by music_ID");
			System.out.println("2 for querying a list of songs' detailed info by singer's full name");
			System.out.println("3 for querying a list of songs with basic infos by title(name of the song)");
			System.out.println("4 for querying  a list of songs' title(name), ID,  album name by composer name");
			System.out.println("5 for querying a list of songs' detail info by Lyricist' full name");
			System.out.println("press any other keys to close connection");
			System.out.println("-----------------------------------------------------------------------");
			Scanner input = new Scanner(System.in);
			int command = input.nextInt();
			if(command==1) {
				this.query_By_MusicID();
			}else if(command ==2) {
				this.query_By_SingerName();
			}else if(command ==3) {
				this.query_By_Musictitle();
			}else if(command ==4) {
				this.query_By_ComposerName();
			}else if(command ==5) {
				this.query_By_Lyricist();
			}
			else {
				System.out.println("closing up connection");
				try {
					this.cn.close();
					isConnected=false; //close up the connection 
					System.out.println("connection closed, byebye!");
				}catch (SQLException e) {
					//e.printStackTrace();
					System.out.println("having trouble closing connection, please shut down the terminal");
				}
			}
		}
	}
	void query_By_MusicID(){
		System.out.println("please enter the music ID you wish to search for");
		String music_ID;
		Scanner input = new Scanner(System.in);
		music_ID = input.nextLine();
		String sql ="select distinct song_name,singer_name,album_name,duration " + 
				" from Song join Perform on Song.music_ID = Perform.music_ID " + 
				" where Song.music_ID = "+ music_ID+" ;";
		
		
		try {
			PreparedStatement ClientStmt = cn.prepareStatement(sql);
			ResultSet myRs = ClientStmt.executeQuery();
			ResultSetMetaData myRsMeta = myRs.getMetaData();
			int columnCount = myRsMeta.getColumnCount();
			//System.out.println("colum count: "+columnCount+"\n");
			/**for (int column =1;column<=columnCount;column++) {
				System.out.println("Column name: "+myRsMeta.getColumnName(column));
				System.out.println("Column type name: "+myRsMeta.(column));
			}**/
			System.out.println("query complete! the results are: ");
			while (myRs.next()) {
			    for (int i = 1; i <= columnCount; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = myRs.getString(i);
			        System.out.print(myRsMeta.getColumnName(i)+ ": " +columnValue  );
			    }
			    System.out.println("");
			}
		}catch(Exception exc) {
			exc.printStackTrace();
			System.out.println("query failed!");
		}
		
	}
	
	void query_By_SingerName(){ // use this query to find out singers name
		System.out.println("please enter the singer name you wish to search for");
		Scanner input = new Scanner(System.in);
		String singer_name = input.nextLine();
		String sql ="select distinct Song.music_ID,song_name,album_name,duration " + 
				" from Song join Perform on Song.music_ID = Perform.music_ID " + 
				" where Perform.singer_name = '"+singer_name+"' ;";
		
		
		try {
			PreparedStatement ClientStmt = cn.prepareStatement(sql);
			ResultSet myRs = ClientStmt.executeQuery();
			ResultSetMetaData myRsMeta = myRs.getMetaData();
			int columnCount = myRsMeta.getColumnCount();
			
			
			System.out.println("query complete! the results are: ");
			while (myRs.next()) {
			    for (int i = 1; i <= columnCount; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = myRs.getString(i);
			        System.out.print(myRsMeta.getColumnName(i)+ ": " +columnValue  );
			    }
			    System.out.println("");
			}
		}catch(Exception exc) {
			exc.printStackTrace();
			System.out.println("query failed!");
		}
	}
	void query_By_Musictitle(){ 
		System.out.println("please enter the title name you wish to search for");
		Scanner input = new Scanner(System.in);
		String title_name = input.nextLine();
		String sql ="select distinct Song.music_ID,song_name,album_name, singer_name" + 
				" from Song join Perform on Song.music_ID = Perform.music_ID" + 
				" where song_name = '"+title_name+"' ;";
		
		
		try {
			PreparedStatement ClientStmt = cn.prepareStatement(sql);
			ResultSet myRs = ClientStmt.executeQuery();
			ResultSetMetaData myRsMeta = myRs.getMetaData();
			int columnCount = myRsMeta.getColumnCount();
			
			System.out.println("query complete! the results are: ");
			while (myRs.next()) {
			    for (int i = 1; i <= columnCount; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = myRs.getString(i);
			        System.out.print(myRsMeta.getColumnName(i)+ ": " +columnValue  );
			    }
			    System.out.println("");
			}
		}catch(Exception exc) {
			exc.printStackTrace();
			System.out.println("query failed!");
		}
		
	}
	void query_By_ComposerName() {
		System.out.println("please enter the Composer's name you wish to search for");
		Scanner input = new Scanner(System.in);
		String Composer_name = input.nextLine();
		String sql ="select distinct song_name,Song.music_ID,album_name" + 
				" from (Song join Compose on Song.music_ID = Compose.music_ID)" + 
				" join Composer on Composer.composer_name = Compose.composer_name and Composer.composer_DoB = Compose.composer_DoB" + 
				" where Composer.composer_name = '"+Composer_name+"' ;";
		
		
		try {
			PreparedStatement ClientStmt = cn.prepareStatement(sql);
			ResultSet myRs = ClientStmt.executeQuery();
			ResultSetMetaData myRsMeta = myRs.getMetaData();
			int columnCount = myRsMeta.getColumnCount();
			//System.out.println("colum count: "+columnCount+"\n");
			/**for (int column =1;column<=columnCount;column++) {
				System.out.println("Column name: "+myRsMeta.getColumnName(column));
				System.out.println("Column type name: "+myRsMeta.(column));
			}**/
			System.out.println("query complete! the results are: ");
			while (myRs.next()) {
			    for (int i = 1; i <= columnCount; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = myRs.getString(i);
			        System.out.print(myRsMeta.getColumnName(i)+ ": " +columnValue  );
			    }
			    System.out.println("");
			}
		}catch(Exception exc) {
			exc.printStackTrace();
			System.out.println("query failed!");
		}
	}
	
	
	void query_By_Lyricist() {
		System.out.println("please enter the lyricist's name you wish to search for");
		Scanner input = new Scanner(System.in);
		String lyricist_name = input.nextLine();
		String sql ="select distinct Song.music_ID,song_name,album_name, duration,Writes.lyricist_name" + 
				"	 from Song join Writes on Song.music_ID = Writes.music_ID join Lyricist on Lyricist.lyricist_name=Writes.lyricist_name" + 
				"	 where Writes.lyricist_name = '"+lyricist_name+"' ;";
		
		
		try {
			PreparedStatement ClientStmt = cn.prepareStatement(sql);
			ResultSet myRs = ClientStmt.executeQuery();
			ResultSetMetaData myRsMeta = myRs.getMetaData();
			int columnCount = myRsMeta.getColumnCount();
			System.out.println("query complete! the results are: ");
			while (myRs.next()) {
			    for (int i = 1; i <= columnCount; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = myRs.getString(i);
			        System.out.print(myRsMeta.getColumnName(i)+ ": " +columnValue  );
			    }
			    System.out.println("");
			}
		}catch(Exception exc) {
			exc.printStackTrace();
			System.out.println("query failed!");
		}
		
		
	}
	public static void main(String[] args) throws IOException {
		String dbname = "Apple_Music";
		String userid = "yueran";
		String password = "702386914";
		ClientDriver app = new ClientDriver(dbname, userid, password);
	}
}
