package com.foo.gol.logic;

public enum GenerationState {
	UNKNOWN,
	INITIALISING,
	READY,
	STABLE, // nothing changed at the last generation - so the state is stable
	GENERATING
}
