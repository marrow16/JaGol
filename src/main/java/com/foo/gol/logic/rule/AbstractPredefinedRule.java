package com.foo.gol.logic.rule;

public abstract class AbstractPredefinedRule implements IChangeAliveRule {
	@Override
	public boolean isCustom() {
		return false;
	}
}
