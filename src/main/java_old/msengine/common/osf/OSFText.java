package msengine.common.osf;

public class OSFText extends OSFNode {
	
	private String text;
	
	public OSFText(String text) {
		this.setText( text );
	}
	
	public OSFText(char c) {
		this( String.valueOf( c ) );
	}
	
	public OSFText() {
		this("");
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text == null ? "" : text;
	}
	
	@Override
	public boolean isText() {
		return true;
	}
	
	@Override
	public OSFText getAsText() {
		return this;
	}

	@Override
	public OSFNode copy() {
		return new OSFText( this.text );
	}
	
	@Override
	public String toString() {
		return "\"" + this.text + "\"";
	}
	
}
