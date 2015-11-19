package legacy.editors.gamess.gamessGUI.IncompatibilityPackage;

import legacy.editors.gamess.gamessGUI.IncompatibilityPackage.ConditionLogic.Condition;
import legacy.editors.gamess.gamessGUI.IncompatibilityPackage.ConditionLogic.Or;
import legacy.editors.gamess.gamessGUI.IncompatibilityPackage.Restriction.RestrictionType;

public class Incompatible extends RestrictionType
{
	private boolean isAvailable = false;
	
	public Incompatible()
	{
		condition = new Or();
	}
	
	public Incompatible(RestrictionType copyRestr)
	{
		condition = new Or();
		this.Input = copyRestr.getInput();
		this.condition.add(copyRestr.getCondition());
	}

	@Override
	public void addCondition(Condition cond) 
	{
		condition.add(cond);
	}
	
	public boolean isAvailable()
	{
		return isAvailable;
	}
	
	public void setAvailable(boolean isAvail)
	{
		isAvailable = isAvail;
	}
}
