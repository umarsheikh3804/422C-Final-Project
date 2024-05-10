# ECE Online Library

Designed and implemented an online library based on a server-client architecture. Used Java server and client sockets, multithreading, JavaFX, and MongoDB 

To start the server, just run the Server.java file or JAR executable. 

To start the client, just run Client.java or JAR executable. The user needs to sign up to create a member account. They must select a unique username and password that is at least 8 characters, containing an uppercase letter, lowercase letter, and digit. Upon signing up, the user can browse through the catalog on the left and, further, use the filter and sort options above to refine their search. If they find an item they like, they can select it and click the checkout button on the bottom left half. The item should appear in the cart to the right. To return a book, they should select the item and click return on the bottom right half. The user can also select a profile picture on the top right corner. The user can click logout on the top right corner after they are done. Next time they use the app, they should use their previous sign-up credentials to login. 

I added several additional features, listed below:
- Sorting view of items by title, author, genre, language
- Item tagging and filtering by type, genre, language
- Images for catalog items
- A search feature to search through catalog items
- Sound effects
- Cryptography technique - password/message hashing
- Cryptography technique - message encryption
- Use MongoDB
- Unit tests that sufficiently cover code (80%)
- Nice GUI
- Profile pictures
- Password strength checker
- New user creation
