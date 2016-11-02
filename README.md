# step by step
From the host machine clone this project and run mvn package in order to build the runtime jar file

## Inside the code...
1. WsTrackServer.class

a. ### Path Parameters
We can use path paraneters to so some filtering and more app logic
- Change your server endpoint annotation to the followed
```
@ServerEndpoint(value = "/ws-track/{username}", configurator = WsTrackConfigurator.class)
```

b. implement: onOpen(), onClose(),  onMessage()

c. method main
```
Server server = new Server(9090);
final ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
context.addFilter(AuthenticationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
WebSocketServerContainerInitializer.configureContext(context).addEndpoint(WsTrackServer.class);
server.start();
```

d. Implement: onOepn(), onClose(), onMessage()
```
@OnOpen
public void onOpen(final Session session) throws Exception {
	// do your thing
	session.getAsyncRemote().sendText("you are connected");
}

@OnMessage
public void onMessage (final Session session, final String incomingMessage) throws Exception {
	// do your thing
	session.getAsyncRemote().sendText("Your message: " + incomingMessage + " arrived");
} 

@OnClose
public void onClose(final Session session) {
    // do your cleanup
}
```
e. - Add: `` static ConcurrentHashSet<Session> sessions = new ConcurrentHashSet<>(); `` as a class variable, this will let us keep multiple session from various clients
- Add `` sessions.add(session); `` to the onOpen method to save the opening session
- In the onMessage replace the send message line with: 
`` s.getAsyncRemote().sendText(userName + " message " + msg );``
- Add some cleanup code ``` sessions.remove(session); ``` to the onClose

2. AuthenticationFilter
Add filter for request parameter "token" and for uri parameter "username"
a. if no token exists: use early failure
```
if(token==null){ 
   ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
}
```
b. Application support subset of token from file input.txt
use token not authorized from the list

## Execution
In Chrome: http://www.websocket.org/echo.html  
fill location = ws://localhost:9090/ws-track/eladw?token=1234  
press connect  
expected output: connected.  

- 2. use same url but remove the token

fill location = ws://localhost:9090/ws-track/eladw

open F12 in chrome

press connect

expected output: early fail 403

- 3. use unautorized token(not 1234/4567/1111 but 222)

fill location = ws://localhost:9090/ws-track/eladw?token=222

press connect

expected output: disconnect after establish websocket.

### Lets add some JSON stuff (ex2)
- Add the followed to your pom dependencies

```
<dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.5.0</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.5.0</version>
        </dependency>
```

- create class Person with multiple fields with getters and setters

- In your WsTrackServer add the followed right next to the sessions definitions
```
ObjectMapper om = new ObjectMapper();

//can add fields for later versions - it means don't fail on extra fields
om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 

//don't send null fields in order to save badwidth
om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//in case of wrong enum put null and don't fail req
om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,true); 
```
- Add the followed under the onMessage method
```
 final Session s = sessions.get(userName);
        try{
            Person person = om.readValue(msg, Person.class);
            LOGGER.info("msg after jackson serialize {}", person);
            if (s != null) {
	    	//send echo
                s.getAsyncRemote().sendText(om.writeValueAsString(person));
            } else {
                LOGGER.warn("Can't echo msg, user {} not connected ", userName);
            }
        } catch(Exception e){
            if(s!=null){
                s.getAsyncRemote().sendText(userName + "send unsupported message " + msg );
            }
        }
```


Now we are JSON aware and can add logic per the received json object...
Lets give it a try.
First connect: ws://localhost:9090/ws-track/eladw?token=1234
Then use the json:
```
{"name":"eladw","age":22,","msgs":["msg1","msg2"]}
```
the echo test page



* Restart the server
* Reconnect the echo tabs in your browser
* Add a user name to each tab connection for example one tab uri will be: ``` ws://localhost:9090/ws-track/oded ``` and the other tab with another user name
* send a message like ``` {"message":"hi", "to":"oded"} ``` you can see that message appears only on "oded" tab
* Do the same as above with the user name in the "to" and you can see that message arrived only to the other tab
