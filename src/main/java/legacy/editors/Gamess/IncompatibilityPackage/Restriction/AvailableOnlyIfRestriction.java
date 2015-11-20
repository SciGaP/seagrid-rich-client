package legacy.editors.gamess.IncompatibilityPackage.Restriction;

import legacy.editors.gamess.IncompatibilityPackage.ConditionLogic.Condition;
import legacy.editors.gamess.IncompatibilityPackage.ConditionLogic.Not;

public class AvailableOnlyIfRestriction extends ExcludedIfRestriction
{
	public AvailableOnlyIfRestriction()
	{
		xpathPreExpr = "/root/input/availableonlyif[descendant::Entity";
	}
	
	@Override
	protected void addRestrictionCondition(RestrictionType restriction, Condition condition) {
		restriction.addCondition(new Not(condition));
	}
}
