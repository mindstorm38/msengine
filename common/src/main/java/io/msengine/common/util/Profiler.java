package io.msengine.common.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Profiler {
	
	private final List<Section> sections = new ArrayList<>();
	private final Section root;
	private Section current;
	
	private boolean active;
	
	public Profiler(int countForAverage) {
		this.root = new Section(null, "", countForAverage);
		this.current = this.root;
	}
	
	public Profiler() {
		this(64);
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void push(String name) {
		if (this.active) {
			this.current = this.current.ensureChild(name);
			this.current.start();
		}
	}
	
	public void pop() {
		if (this.active && this.current != this.root) {
			this.current.end();
			this.current = this.current.parent;
		}
	}
	
	public void popPush(String name) {
		this.pop();
		this.push(name);
	}
	
	public Section getSection(String path) {
		if (path.isEmpty()) {
			return this.root;
		} else {
			Section sect = this.root;
			for (String name : path.split("\\.")) {
				if ((sect = sect.getChild(name)) == null) {
					return null;
				}
			}
			return sect;
		}
	}
	
	public static class Section {
		
		private static final DecimalFormat SHORT_MILLIS_FORMAT = new DecimalFormat("#");
		
		static {
			SHORT_MILLIS_FORMAT.setMinimumIntegerDigits(1);
			SHORT_MILLIS_FORMAT.setMaximumFractionDigits(5);
			SHORT_MILLIS_FORMAT.setMinimumFractionDigits(5);
			SHORT_MILLIS_FORMAT.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		}
		
		private final Section parent;
		private final String name;
		private final Map<String, Section> children = new HashMap<>();
		private final int[] deltas;
		private long start;
		private int sum;
		private float averageMillis;
		
		protected Section(Section parent, String name, int countForAverage) {
			this.parent = parent;
			this.name = name;
			this.deltas = new int[countForAverage];
		}
		
		public String getName() {
			return this.name;
		}
		
		public Map<String, Section> getChildren() {
			return this.children;
		}
		
		public Section ensureChild(String name) {
			return this.children.computeIfAbsent(name, n -> new Section(this, n, this.deltas.length));
		}
		
		public Section getChild(String name) {
			return this.children.get(name);
		}
		
		protected void start() {
			this.start = System.nanoTime();
		}
		
		protected void end() {
			
			int delta = (int) (System.nanoTime() - this.start);
			int last = this.deltas.length - 1;
			
			this.sum += delta;
			this.sum -= this.deltas[last];
			
			System.arraycopy(this.deltas, 0, this.deltas, 1, last);
			this.deltas[0] = delta;
			this.averageMillis = (float) this.sum / this.deltas.length / 1000000;
			
		}
		
		public long getLastDelta() {
			return this.deltas[0];
		}
		
		public float getLastDeltaMillis() {
			return (float) this.deltas[0] / 1000000;
		}
		
		public float getAverageDeltaMillis() {
			return this.averageMillis;
		}
		
		@Override
		public String toString() {
			return "Profiler.Section<name=" + this.name + ", average=" + SHORT_MILLIS_FORMAT.format(this.averageMillis) + "ms>";
		}
		
	}
	
}
