# iMemorizeAndroidStudio
iMemorize using the gradle build system and android studio

-Database is SQLLite that synchs with the online database for iMemorize.org
-db46966_imemorize DB at MediaTemple

to synch tables

1. Make modifications to the sqllite table using Firefox plugin: https://addons.mozilla.org/en-US/firefox/addon/sqlite-manager/
2. Export the table using Export Wizard > select "Include Create Table statement"
3. open the exported table in an editor and: a. remove all quotes around the table name b. change INTEGER to INT and NUMERIC to INT in the CREATE TABLE statement | add varchar(255) to each text field in the CREATE TABLE statement
4. Save the file
5. Open PHPMyAdmin in MediaTemple and import the sql into the db46966_imemorize DB

Note that the DB db46966_quotations is the old one for the main website and the iOS version which allowed users to enter their own quotes directly into the DB - that has now been moved into its own table in the db46966_quotations db

