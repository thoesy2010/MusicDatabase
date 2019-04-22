-------------------------------------------------------------------
|////////////////Apple Music Database System//////////////////////| 
|/////////////created by Rongxuan Hu&Yueran Zhang/////////////////|
-------------------------------------------------------------------
To compile:
open the file directory then:

~$ javac AdminDriver.java
~$ java -cp /usr/share/java/mysql.jar:.  AdminDriver

~$ javac ClientDriver.java
~$ java -cp /usr/share/java/mysql.jar:.  ClientDriver
General:
	Please follow the user prompt in the menu to operate, be careful with input format,
for certain key values (especially on insert&delete) you will need to put ' mark around
your string-type input(for example if I was trying to delete a person named mark from record,
 I would type in: 'mark' instead of: mark )

AdminDriver.java:
In the AdminDriver.java, user can use 3 operations and 1 query:
update,delete,create and query purchase history of any specific user
Before each operation, the system will prompt the user to input the name of the table and then 
display the attributes in that table
Please do notice that if a attribute's value does not tolarate null, when you create new entries,
that attribute must be in fields and has a fitted value.

-Update:
update will simply look for a tuple that has the attribute&attribute value we searched for, and 
it will just the input value to replace the old one. Do notice that if you are operating on a 
foreign key, this will not work. And if you are operating on an attribute thats parent of another
table's foreign key,it will cascade.

-delete:
Becareful to use the primary key, as this method will look for the value&attribute you entered and 
delete whichever tuple owns them.

-insert:
Please do follow the format very carefully, or you will not be able to generate tuple. 


ClientDriver.java:
simple-to-use, user friendly menu, just follow the prompt, no special spelling here
search by ID will only return one tuple(if found any)
whereas for the rest, it will return a list of tuples, depending on how many there could be.

Last update:2018/12/7



 