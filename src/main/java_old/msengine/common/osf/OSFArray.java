package msengine.common.osf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.sutil.LazyLoadValue;
public class OSFArray extends OSFNode implements Iterable<OSFNode> {
	
	private final List<OSFNode> children = new ArrayList<>();
	
	private final LazyLoadValue<Class<? extends OSFNode>> onlyOneType = new LazyLoadValue<Class<? extends OSFNode>>() {
		public Class<? extends OSFNode> create() {
			return OSFArray.this.isOnlyOneTypeRaw();
		}
	};
	
	private final LazyLoadValue<Class<? extends Number>> onlyOneNumberType = new LazyLoadValue<Class<? extends Number>>() {
		public Class<? extends Number> create() {
			return OSFArray.this.isOnlyOneNumberTypeRaw();
		}
	};
	
	public OSFArray() {}
	
	public OSFArray(OSFNode[] nodes) {
		
		this.children.addAll( Arrays.asList( nodes ) );
		
	}
	
	public List<OSFNode> getChildren() {
		return Collections.unmodifiableList( this.children );
	}
	
	public void add(OSFNode node) {
		
		this.children.add( node );
		
		this.onlyOneType.reset();
		this.onlyOneNumberType.reset();
		
	}
	
	public OSFNode get(int index) {
		return this.children.get( index );
	}
	
	public int size() {
		return this.children.size();
	}
	
	private Class<? extends OSFNode> isOnlyOneTypeRaw() {
		if ( this.children.size() == 0 ) return null;
		if ( this.children.size() == 1 ) return this.children.get( 0 ).getClass();
		Class<? extends OSFNode> res = this.children.get( 0 ).getClass();
		for ( int i = 1; i < this.children.size(); i++ )
			if ( this.children.get( i ).getClass() != res )
				return null;
		return res;
	}
	
	private Class<? extends Number> isOnlyOneNumberTypeRaw() {
		if ( this.children.size() == 0 ) return null;
		OSFNode first = this.children.get( 0 );
		if ( !first.isNumber() ) return null;
		Class<? extends Number> res = first.getAsNumber().getNumberClass();
		if ( this.children.size() == 1 ) return res;
		OSFNode child;
		for ( int i = 1; i < this.children.size(); i++ ) {
			child = this.children.get( 0 );
			if ( !child.isNumber() ) return null;
			if ( child.getAsNumber().getNumberClass() != res ) return null;
		}
		return res;
	}
	
	public Class<? extends OSFNode> isOnlyOneType() {
		return this.onlyOneType.get();
	}
	
	public Class<? extends Number> isOnlyOneNumberType() {
		return this.onlyOneNumberType.get();
	}
	
	public boolean isByteArray() {
		return this.isOnlyOneNumberType() == Byte.class;
	}
	
	public boolean isShortArray() {
		return this.isOnlyOneNumberType() == Short.class;
	}
	
	public boolean isIntegerArray() {
		return this.isOnlyOneNumberType() == Integer.class;
	}
	
	public boolean isLongArray() {
		return this.isOnlyOneNumberType() == Long.class;
	}
	
	public boolean isFloatArray() {
		return this.isOnlyOneNumberType() == Float.class;
	}
	
	public boolean isDoubleArray() {
		return this.isOnlyOneNumberType() == Double.class;
	}
	
	public boolean isBooleanArray() {
		return this.isOnlyOneType() == OSFBoolean.class;
	}
	
	@Override
	public boolean isArray() {
		return true;
	}
	
	@Override
	public OSFArray getAsArray() {
		return this;
	}
	
	@Override
	public OSFNode copy() {
		OSFArray copy = new OSFArray();
		for ( OSFNode child : this.children ) copy.children.add( child.copy() );
		return copy;
	}
	
	@Override
	public String toString() {
		if ( this.children.size() == 0 ) return "[]";
		String str = "[";
		Iterator<OSFNode> childrenIt = this.children.iterator();
		while ( childrenIt.hasNext() ) {
			str += childrenIt.next().toString();
			if ( childrenIt.hasNext() ) str += ',';
		}
		return str + ']';
	}

	@Override
	public Iterator<OSFNode> iterator() {
		return this.children.iterator();
	}
	
}
