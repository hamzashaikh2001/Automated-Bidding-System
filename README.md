# **Automated Bidding System**

**By Hamza Shaikh**

**Programmers Point of View:**

The overall system is structured in two separate projects: the Server and the Client
projects. The server project is linked to all the data, and communicates specific aspects of the
data to the client. The client, on the other hand, listens to the user’s input and communicates
that input to the server. The server will deal with whatever calculations are required and
command the clients to alter accordingly. Hence, all the various clients will be identical since
they are displaying the same server information.

The server begins by connecting to the online Mongo database, which holds all the
usernames and passwords of the various users. The server will then read a document
containing all the necessary information to display on the auction, such as item names and
descriptions. The server will store this information into an arrayList and modify it accordingly
depending on future changes to the auction items. The server will then wait for the user to
connect into it. When the user finally connects to the server, it will wait for the user to input a
username and password. If the username and password matches what is in the database, then
the client will be forwarded the arraylist of item information.
On the client side, it will first initialize a scene to ask for the IP address of the server. If
the wrong IP address is given, the client will alert the client that it is wrong and wait for another
ip address. If the IP address is correct, then the client will connect to the server. You can also
just click on the local host button if you are running the client on the same computer as the
server. Either way, the client will connect and then ask for a username and password. You can
choose to give one of the built in usernames and passwords, or you could go as a guest. The
choice is forwarded to the server, which compares the information to the database. If it is
correct, the server will transfer the necessary information to the client so that it could display the
items.

The client is set up to display the many items with buttons to bid and buy for each one.
An amount of money can be specified in the textbox, which will then be sent over to the server
when bid is selected. If buy is selected, the max number listed on the buy button will be
transferred over to the server. Either way, this transfer is synchronized in case multiple buttons
are somehow clicked at the same time. The server is also synchronized so that changes to the
same items will be evaluated one at a time. Therefore, the entire transfer of information is safe
from multiple writes. The server will then alert all the clients of the change through the
observable method, which will send the newly changed item information to each of the clients.
The clients will evaluate this information to find where it is in the displayed scene. The item will
then be altered accordingly, by changing the price and ending the sale in case the maximum
amount is reached. If the history button is clicked, a display of all transactions will be shown by
iterating through an arraylist of items that were altered. Clicking the history button again, which
is now renamed to auction, will redisplay the old scene. If the quit button is clicked, the client
program will end smoothly.

**Client Point of View:**

Double click the client executable file to start the client. You will be prompted to enter an
IP address. Enter the IP address of the server you want to join. This IP address will be the
server computer’s IPv4 address. The program will alert you if this IP address is incorrect, so
don’t worry about it if you get it wrong a couple of times. You can also just click the local host
button, which will check your client computer’s IPv4 address. If the server is running on the
same computer, it will automatically connect to it without needing any input.
You will now be prompted for a username and password. Enter a username and
password from the Mongo database that is connected to the server. This information is listed at
the end of this document. If the information is incorrect, you will be safely alerted. You can keep
trying till you get the right information, in which case the client will then display the auction
server. You can also just click the guest button, which will bypass any database verification and
allow you to browse as a guest.

The client will now display the auction information, which is the list of all items that are
currently being sold. You will be able to see the item name, description, current price. You will
also have a text box to input your current bid, as well as a bid button to send it over. If the
inputted bid is not valid, you will be alerted to the reason why it is wrong. There are two alerts
you can be given in this case. The first one is that the input is not a number. The second one is
that the input is not greater than the current price. Either way, you will not be able to bid to the
server till the input is valid. If the input is valid, your information will be sent to the server and will
be displayed as the new price for all clients. If your price exceeds the max price value of the
item, then you will be given it automatically. This price can be found on the buy button, which will
automatically buy the item at that price. Either way, once an item is no longer available, the
client will turn the buttons red and alter the text to display who bought the item. The final price
will be listed as the price of the item. If you click the history button, you will be able to see every
transaction that occurred by all clients. If you click quit, the program will end gracefully, without
any errors.

**Additional Features:**

● Set a minimum starting price > 0 for every item.

● Sets a high limit that is a 'Buy It Now' price. When a customer bids that amount, he/she
gets it right away.

● Creates a buy it now button that will automatically bid the buy it price.

● Every customer can see the bid history of every item, including who made the bid. If the
item has been sold, every customer is able to see the buyer and the selling price.

● Items have descriptions that are visible to the customers.

● Non-volatile history of auctions and customer activity.

● Using the Observable class and Observer interface.

● Using a Mongo database to hold my usernames and passwords.

● Use of a password for logging in, on top of using a username.

● Allowing the user to input an IP address of the server they wish to join, with its own
scene dedicated to it.

● Allowing the user to safely get the IP address wrong without crashing. Includes alerts if
the IP address didn’t work.

● Including a button to automatically connect using the local IP address information, in the
case that the server is running locally. This overall makes the connection process a lot
easier.

● Allowing the user to safely get the username wrong without crashing the client. It will
furthermore alert the user if he/she got it wrong and allow for them to correct it.

● History button changes the scene to another and safely displays the information. The
button also alters the text and allows for the user to click it again to go back to the
auction.

● History will display the winning bids as green to differentiate them from the other bids.

● Sold buttons and the quit button are colored red to give a sense of urgency.

● Use of bold text, borders, and various scene colors to look nice.

● The server is a maven project, rather than a traditional java project.

● Use of a pom.xml file to import necessary files.

● Use of Json for all forms of communication between the server, client, and database.

● Included a kill code for the server and client. If you use “exit” for the username, the
server and the client that typed it will be closed safely. This is just so no random server
keeps running in the background.

**Database usernames and passwords:**

Username: Hamza

Password: 1234

Username: Jonathan

Password: abcd

Username: Anthony

Password: 0000

Username: Jason

Password: 56789

Username: Billy

Password: wasd
