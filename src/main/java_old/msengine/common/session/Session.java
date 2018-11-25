package msengine.common.session;

import java.util.UUID;

public class Session {

	private final UUID uuid;
	private String username;
	
	public Session(UUID uuid, String username) {
		
		this.uuid = uuid;
		this.username = username;
		
	}
	
	public Session(String username) {
		this( UUID.randomUUID(), username );
	}
	
	public Session() {
		this( "Player" + ( System.currentTimeMillis() % 1000L ) );
	}
	
	public UUID getUuid() { return this.uuid; }
	
	public String getUsername() { return this.username; }
	public void setUsername(String username) { this.username = username; }
	
}
