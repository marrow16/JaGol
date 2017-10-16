package com.foo.gol.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foo.gol.logic.rule.ChangeAliveRuleFactory;
import com.foo.gol.logic.rule.Custom;
import com.foo.gol.logic.rule.IChangeAliveRule;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeRuleLoadSaveWrapper {
	private IChangeAliveRule changeAliveRule;

	public ChangeRuleLoadSaveWrapper() {
		changeAliveRule = null;
	}

	public ChangeRuleLoadSaveWrapper(IChangeAliveRule changeAliveRule) {
		this.changeAliveRule = changeAliveRule;
	}

	@JsonIgnore
	IChangeAliveRule getActualRule() {
		return changeAliveRule;
	}

	public String getType() {
		return changeAliveRule.getType();
	}
	public void setType(String type) {
		if (changeAliveRule != null) {
			// already set by incoming rule
			if (changeAliveRule.isCustom()) {
				((Custom)changeAliveRule).setType(type);
			}
			return;
		}
		if (ChangeAliveRuleFactory.CUSTOM_RULE_LABEL.equalsIgnoreCase(type)) {
			changeAliveRule = new Custom();
		} else {
			IChangeAliveRule newType = ChangeAliveRuleFactory.createFromLabel(type);
			if (newType != null) {
				changeAliveRule = newType;
			} else {
				changeAliveRule = new Custom(type);

			}
		}
	}

	public String getRule() {
		return changeAliveRule.getRleString();
	}
	public void setRule(String rule) {
		if (changeAliveRule == null) {
			changeAliveRule = new Custom();
			((Custom)changeAliveRule).setRleString(rule);
		} else if (changeAliveRule.isCustom()) {
			((Custom)changeAliveRule).setRleString(rule);
		}
	}
}
