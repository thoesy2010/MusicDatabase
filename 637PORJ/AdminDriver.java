import java.sql.*;
import java.util.Scanner;
import java.io.*;
public class AdminDriver {
	Connection cn;
	// currentResults holds current results from a search() so that other methods can access them
	ResultSet currentResults;
	// currentItem holds the row number of an itme the user is looking at (so we can use currentResults.absolute(currentItem) to go to it
	Integer currentItem;
	Statement myStmt;
	boolean isConnected;
	public AdminDriver(String dbname, String userid, String password) {
		cn = null;
		currentResults = null;
		currentItem = null;
		myStmt=null;
		isConnected=false;
		try {
			try {
				//1 get connection
				cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbname, userid, password);
				//2create statement
				myStmt = cn.createStatement();
				isConnected=true;
			}catch(Exception exc0){
				System.out.println("connection failed: " + exc0);
				
			}//always start by showing all the tables
			
			while(isConnected) {
				print_tables();
				System.out.println("please enter which function you would like to apply to database");
				System.out.println("1 for update");
				System.out.println("2 for Delete");
				System.out.println("3 for Create");
				System.out.println("4 for finding purchase history of any specific user");
				System.out.println("press any other keys to close connection");
				System.out.println("------------------------------------------------------------------------");
				Scanner input = new Scanner(System.in);
				int number = input.nextInt();
				System.out.println("input is "+number);
				if(number == 1) {
					this.Update();
				}else if (number == 2){
					this.Delete();
				}else if (number == 3) {
					this.Insert();
				}else if( number==4){
					this.query_By_UserName();
				}else {
					System.out.println("closing up connection");
					try {
						this.cn.close();
						isConnected=false; //close up the connection 
						System.out.println("connection closed, byebye!");
					}catch (SQLException e) {
						e.printStackTrace();
						System.out.println("exiting failed, please close up the terminal!");
					}
				}
			}
		}catch(Exception exc0){
			System.out.println("Fatal error, please close up the terminal" );
		}
		
	}
	void print_tables() {
		try {
			System.out.println("---------------------------Main-Menu------------------------------------");
			System.out.println("welcome to our Apple music database Manage System");
			System.out.println("here is a list of the existing tables in our database:");
			DatabaseMetaData md = cn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
			  System.out.println(rs.getString(3));
			}
		}catch(Exception exc0){
			System.out.println("connection failed: " + exc0);
		}
	}
	void query_By_UserName() {
		System.out.println("please enter the user's ID you wish to search for");
		Scanner input = new Scanner(System.in);
		int user_ID = input.nextInt();
		String sql ="select distinct Song.music_ID,song_name" + 
				" from Purchase join Song on Song.music_ID = Purchase.music_ID" + 
				" where user_ID = "+user_ID+" ;";
		
		//System.out.println("your instruction was :"+sql);
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
	public void Update() {
		
		//we make changes to a tuple by refering to its primaryKey and make changes to its specific attribute
		String TableName, Attribute, AttInputValue, PrimaryKey, PrimaryKeyValue;
		System.out.println("please enter the name of the table you would like to update");
		Scanner input = new Scanner(System.in);
		TableName = input.nextLine();
		ResultSet TableColumns;
		try{
			 ResultSet rs = myStmt.executeQuery("select * from "+TableName);
			 DatabaseMetaData meta = this.cn.getMetaData();
			 TableColumns =  meta.getColumns(null, null, TableName, null);
			 System.out.println("printing out the "+TableName+"'s table structure");
			 while (TableColumns.next()) {
		         System.out.println(
		           "  column name: "+TableColumns.getString("COLUMN_NAME")
		           + ", column type: "+TableColumns.getString("TYPE_NAME")
		           + ", column size: "+TableColumns.getInt("COLUMN_SIZE")
		           +", is nullable: "+TableColumns.getInt("NULLABLE")
		         ); 
		      }
			TableColumns.close();
		}	
		catch(Exception exc){
			exc.printStackTrace();
			System.out.println("table name invalid, returning to menu");
			return;
		}
		
		
		//here we prompt for the attribute first then go for the value.
		System.out.println("please enter the name of the primarykey");
		input = new Scanner(System.in);
		PrimaryKey = input.nextLine();
		System.out.println("please enter the searched value of the primarykey,"
				+ "\n if it is a string, please enter in the form of 'string' ");
		input = new Scanner(System.in);
		PrimaryKeyValue = input.nextLine();
		System.out.println("please enter the name of the attribute you would like to modify");
		input = new Scanner(System.in);
		Attribute = input.nextLine();
		System.out.println("please enter the new value for this attribute \n"
		+ "if it is a string, please enter in the form of 'string' ");
		input = new Scanner(System.in);
		AttInputValue = input.nextLine();
		try {
			
			String sql =" update "+ TableName
					+" set "+Attribute+" = "+AttInputValue 
					+" where "+PrimaryKey+" = "+PrimaryKeyValue;
			
			System.out.println("your instruction was :"+sql);
			int rowsAffected=myStmt.executeUpdate(sql);
			System.out.println("rows affected: "+rowsAffected);
			System.out.println("update complete");
			}catch(Exception exc) {
				System.out.println("update failed, please check your input, returning to the main menu");
				return;
			}
		System.out.println("update complete!");
		
	}
	public void Delete() {
		//we delete tuple be referring to a specific attribute (using primary key in stead of lesser attributes
		//would be more accurate)
		String TableName,TargetAttribute, TargetAttValue;
		
		System.out.println("please enter the name of the table you wish to delete from");

		Scanner input = new Scanner(System.in);
		TableName = input.nextLine();
		ResultSet TableColumns;
		try{
			 ResultSet rs = myStmt.executeQuery("select * from "+TableName);
			 DatabaseMetaData meta = this.cn.getMetaData();
			 TableColumns =  meta.getColumns(null, null, TableName, null);
			 System.out.println("printing out the "+TableName+"'s table structure");
			 while (TableColumns.next()) {
		         System.out.println(
		           "  column name: "+TableColumns.getString("COLUMN_NAME")
		           + ", column type: "+TableColumns.getString("TYPE_NAME")
		           + ", column size: "+TableColumns.getInt("COLUMN_SIZE")
		           +", is nullable: "+TableColumns.getInt("NULLABLE")
		         ); 
		      }
			TableColumns.close();
		}	
		catch(Exception exc){
			exc.printStackTrace();
			System.out.println("table name invalid, returning to menu");
			return;
		}
		
		
		
		System.out.println("please enter the name of the attribute(preferable using primary key)");
		input = new Scanner(System.in);
		TargetAttribute = input.nextLine();
		System.out.println("please enter the key value of this attribute \n"
		+ "if it is a string, please enter in the form of 'string' ");
		TargetAttValue = input.nextLine();
		System.out.println("deleting....");
		try{
			String sql = "delete from " + TableName
					+ " where " + TargetAttribute + " = "+ TargetAttValue;
			System.out.println("your instruction was :"+sql);
			int rowsAffected = myStmt.executeUpdate(sql);
			
			System.out.println("rows affected: "+rowsAffected);
			System.out.println("delete complete");
		}catch(Exception exc) {
			System.out.println("delete failed, please check your input");
		}
	}
	public void Insert() {
	//	INSERT INTO table_name ( field1, field2,...fieldN )
	//	   VALUES
	//	   ( value1, value2,...valueN );
		
		System.out.println("please enter the name of the table you wish to insert into");
		String table_name;
		Scanner input = new Scanner(System.in);
		table_name = input.nextLine();
		ResultSet TableColumns;
		try{
			 ResultSet rs = myStmt.executeQuery("select * from "+table_name);
			 DatabaseMetaData meta = this.cn.getMetaData();
			 TableColumns =  meta.getColumns(null, null, table_name, null);
			 System.out.println("printing out the "+table_name+"'s table structure");
			 while (TableColumns.next()) {
		         System.out.println(
		           "  column name: "+TableColumns.getString("COLUMN_NAME")
		           + ", column type: "+TableColumns.getString("TYPE_NAME")
		           + ", column size: "+TableColumns.getInt("COLUMN_SIZE")
		           +", is nullable: "+TableColumns.getInt("NULLABLE")
		         ); 
		      }
			TableColumns.close();
		}	
		catch(Exception exc){
			exc.printStackTrace();
			System.out.println("table name invalid, returning to menu");
			return;
		}
		
		
		System.out.println("please enter the fields you wish to insert into");
		System.out.println("it must be in the format of ( field1, field2,...fieldN) ,including the brackets");
		String fields;
		input = new Scanner(System.in);
		fields = input.nextLine();
		
		System.out.println("please enter the value you wish to insert into above fields");
		System.out.println("it must be in the format of ( value1, value2,...valueN ) ,including the brackets");
		String values;
		input = new Scanner(System.in);
		values = input.nextLine();
		
		String sql = " INSERT INTO "+table_name+" "+ fields + " VALUES " + values + " ; ";
		System.out.println("your instruction was :"+sql);
		try {
			int rowsAffected = myStmt.executeUpdate(sql);
			System.out.println("rows affected: "+rowsAffected);
			System.out.println("insertion complete");	
		}catch(Exception exc){
			System.out.println("update failed, please check your input");
			System.out.println("returning back to main menu");
		}	
		
	}
	
	
	public static void main(String[] args) throws IOException {
		String dbname = "Apple_Music";
		String userid = "yueran";
		String password = "702386914";
		AdminDriver app = new AdminDriver(dbname, userid, password);
	}
}
